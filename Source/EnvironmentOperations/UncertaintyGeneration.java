package EnvironmentOperations;

import java.util.Vector;

import CommonStructures.IntSet;
import CommonStructures.SetSet;
import GUI.ConsoleWindow;
import GUI.EnvironmentTab;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Contains the algorithm for generating the environment for any module, by utilising "uncertainties" */
public class UncertaintyGeneration{

	/* The parent part of the algorithm for generating the environment for any module. It closely follows the
	 * algorithm given in the thesis. See the ConsoleWindow.output calls below for more information as to
	 * what operations are performed at each step */
	public static SetNotationModule generate(SetNotationModule moduleDefinition) {
		SetNotationModule environmentDefinition = new SetNotationModule();

		for(int i=0;i<moduleDefinition.getNoOfStates();i++){
			
			ConsoleWindow.output("Creating environment state E:{"+moduleDefinition.getStateName(i)+";{};{}}" 
				+" corresponding to module state "+moduleDefinition.getStateName(i) +" with no uncertainty");
			
			environmentDefinition.addStateName("E:{"+moduleDefinition.getStateName(i)+";{};{}}");
		}
		for(int i=0;i<moduleDefinition.getNoOfInputs();i++){
			
			ConsoleWindow.output("Creating environment output "+moduleDefinition.getInputName(i)
			+" corresponding to module input");
			
			environmentDefinition.addOutputName(moduleDefinition.getInputName(i));
		}
		for(int i=0;i<moduleDefinition.getNoOfOutputs();i++){
			
			ConsoleWindow.output("Creating environment input "+moduleDefinition.getOutputName(i)
			+" corresponding to module output");
			
			environmentDefinition.addInputName(moduleDefinition.getOutputName(i));
		}
		Vector<Uncertainty> visitedUncertaintys= new Vector<Uncertainty>();

		for(int i=0;i<moduleDefinition.getNoOfStates();i++){
			Uncertainty startingUncertainty = new Uncertainty();
			Configuration startConfiguration = new Configuration();
			startConfiguration.setState(i);
			startConfiguration.setPendingSignals(new IntSet());
			startConfiguration.setTravellingOutputs(new IntSet());
			startingUncertainty.addConfiguration(startConfiguration);
			
			ConsoleWindow.output("Beginning recursive calculation of environment actions in environment state "+environmentDefinition.getStateName(i)
				+" corresponding to module state "+moduleDefinition.getStateName(i) +" with no uncertainty");
			
			environmentDefinition=recurse(moduleDefinition,environmentDefinition,startingUncertainty,visitedUncertaintys);
			
			ConsoleWindow.output("Finished calculating all sequences of available environment actions starting from module state "+moduleDefinition.getStateName(i));
		}
		if(EnvironmentTab.instance.StateRemoval.isSelected()){
			
			ConsoleWindow.output("Removing any duplicate state definitions");
			
			environmentDefinition=NonArbGeneration.removeDuplicateStates(environmentDefinition);
		}
		
		ConsoleWindow.output("Tidying state names to prevent conflicts (within the environment state names, and between environment and module) "
				+ "and to remove non-alphanumeric chars (such as dashes, commas etc.)");
		
		environmentDefinition=NonArbGeneration.tidyStateNames(moduleDefinition,environmentDefinition);
		if(!environmentDefinition.allStatesHaveTransitions()){
			ConsoleWindow.output("Environment deadlock guaranteed due to unavailability of actions to signal safely in some states (see empty state definitions).");
		}
		return environmentDefinition;
	}

	/* The recursive part of the algorithm for generating the environment for any module. It closely follows the
	 * algorithm given in the thesis. The set of uncertainties which have been "recursed to" is remembered using the variable
	 * visitedUncertainties. The corresponding environment state for a particular uncertainty is found using the findEnvrionmentStateIndex
	 * method.  See the ConsoleWindow.output calls below for more information as to what operations are performed at each step */
	private static SetNotationModule recurse(SetNotationModule moduleDefinition, SetNotationModule environmentDefinition,
			Uncertainty currentUncertainty, Vector<Uncertainty> visitedUncertainties) {

		/* If we have not visited this currentUncertainty yet */
		if(!UncertaintyVisited(currentUncertainty,visitedUncertainties)){

			/* Add that we have visited it */
			visitedUncertainties.add(currentUncertainty);

			/*Retrieve the index of the corresponding environment state (guaranteed to already exist) */
			int environmentStateIndex= findEnvironmentStateIndex(currentUncertainty,moduleDefinition,environmentDefinition);	

			SetSet signallableSets = new SetSet();
			
			ConsoleWindow.output("Calculating signals which are safe to send when the environment is in the uncertainty: "+
					currentUncertainty.printConfigurationSet(moduleDefinition));
			
			for(int j=0;j<currentUncertainty.getNoOfConfiguration();j++){
				ConsoleWindow.output("Retrieving the set of transitions available to the module in the "+j+"ths configuration's state ("
						+moduleDefinition.getStateName(currentUncertainty.getConfiguration(j).getState())+")");
				
				Vector<SetTransition> transitions = moduleDefinition.getTransitionsWithSource(currentUncertainty.getConfiguration(j).getState());
				
				ConsoleWindow.output("Now checking that each transition for this state contains the corresponding set of pending signals ("+
						currentUncertainty.getConfiguration(j).getPendingSignals().printStringRepresentation(moduleDefinition.getInputNames()) +" from the current configuration, and if it does"
								+ ", then we subtract the pending signals from the set and the resulting set is a candidate for signalling");
				for(int i=0;i<transitions.size();i++){
					
					ConsoleWindow.output("Checking input set "+transitions.get(i).getInputSet().printStringRepresentation(moduleDefinition.getInputNames()));
					
					if(currentUncertainty.getConfiguration(j).getPendingSignals().subset(transitions.get(i).getInputSet())){
						signallableSets.add(transitions.get(i).getInputSet().setDifference(currentUncertainty.getConfiguration(j).getPendingSignals()));
						ConsoleWindow.output("Input set contains the set of pending signals ("+
						currentUncertainty.getConfiguration(j).getPendingSignals().printStringRepresentation(moduleDefinition.getInputNames())+
							") so we subtract from this the set of pending signals and make the resulting set a candidate for signalling");
						ConsoleWindow.output("Set: "+signallableSets.get(signallableSets.size()-1).printStringRepresentation(moduleDefinition.getInputNames())+" is a candidate");
					}
					else{
						ConsoleWindow.output("Input set does not contain the set of pending signals ("+
						currentUncertainty.getConfiguration(j).getPendingSignals().printStringRepresentation(moduleDefinition.getInputNames())+
							") so the resulting set (input set SetMinus pending signals) is not a candidate for signalling "
								+ "(it could violate safety in the current configuration)");
					}
				}
			}
			
			ConsoleWindow.output("Initial list of candidate sets generated. Now we check that signalling each of these does not cause a problem (safety violation, or input/output clash) "
					+ "in any of the configuration in the uncertainty.");

			for(int i=signallableSets.size()-1;i>=0;i--){
				IntSet signalledSet = signallableSets.get(i);
				
				ConsoleWindow.output("Checking if signalling candidate "+signalledSet.printStringRepresentation(moduleDefinition.getInputNames())
					+" causes any problems for any configuration in the uncertainty");
				
				if(signalledSet.size()==0){
					signallableSets.remove(i);
					
					ConsoleWindow.output("This candidate is empty, so is simply removed (obviously not a valid set to signal)");
					
					continue;
				}
				
				for(int j=0;j<currentUncertainty.getNoOfConfiguration();j++){
					
					ConsoleWindow.output("Checking candidate against configuration: "+currentUncertainty.getConfiguration(j).print(moduleDefinition));
					
					int currentConfigurationState = currentUncertainty.getConfiguration(j).getState();
					IntSet ConfigurationPendingSignals = currentUncertainty.getConfiguration(j).getPendingSignals();
					IntSet ConfigurationTravellingOutputs = currentUncertainty.getConfiguration(j).getTravellingOutputs();

					if(!signalledSet.setDifference(ConfigurationPendingSignals).equals(signalledSet)){
						ConsoleWindow.output("There is an overlap between the candidate and the set of pending signals for this configuration, "
								+ " so the current candidate is invalid (signalling could cause input clash for this configuration) and must be removed");
						signallableSets.remove(i);
						break;
					}

					IntSet totalSignalled = ConfigurationPendingSignals.setUnion(signalledSet);
					
					ConsoleWindow.output("Combining candidate with pending set for this configuration, to see if resulting combination of inputs causes any problems for this configuration");		
					ConsoleWindow.output("Resulting combination of inputs for current configuration is therefore: "+totalSignalled.printStringRepresentation(moduleDefinition.getInputNames()));
					
					int safetyOrClash=moduleDefinition.violateSafetyOrClash(currentConfigurationState,totalSignalled,ConfigurationTravellingOutputs);
					
					if(safetyOrClash==1){
						
						ConsoleWindow.output("Resulting combination of inputs can violate safety for this configuration, so the current candidate is invalid and must be removed");
						
						signallableSets.remove(i);
						break;
					}
					else if(safetyOrClash==2){
						
						ConsoleWindow.output("Resulting combination of inputs can cause an output clash for this configuration, so the current candidate is invalid and must be removed");
						
						signallableSets.remove(i);
						break;
					}
					
					ConsoleWindow.output("Candidate set doesn't cause any problems for this configuration");
					
				}
			}
			
			if(signallableSets.size()>0){
				StringBuffer listOfSignalSets = new StringBuffer();
				for(int i=0;i<signallableSets.size();i++){
					listOfSignalSets.append(signallableSets.get(i).printStringRepresentation(moduleDefinition.getInputNames()));
					if(i<signallableSets.size()-1){
						listOfSignalSets.append(",");
					}
				}
				
				ConsoleWindow.output("Final list of signals that are safe for the environment to send generated, environment can signal safely signal: "+listOfSignalSets);
				
				for(int i=0;i<signallableSets.size();i++){
					IntSet outputSignals=signallableSets.get(i).deepCopy();
					
					ConsoleWindow.output("Building environment transition corresponding to signalling "+
						outputSignals.printStringRepresentation(moduleDefinition.getInputNames())+" in the uncertainty: "+
						currentUncertainty.printConfigurationSet(moduleDefinition));
					ConsoleWindow.output("Adding signalled set to all configuration in the uncertainty to build the resulting uncertainty");
					
					Uncertainty resultingUncertainty = new Uncertainty();
					for(int j=0;j<currentUncertainty.getNoOfConfiguration();j++){
						Configuration resultingConfiguration=new Configuration();
						resultingConfiguration.setState(currentUncertainty.getConfiguration(j).getState());
						resultingConfiguration.setPendingSignals(currentUncertainty.getConfiguration(j).getPendingSignals().deepCopy().setUnion(outputSignals.deepCopy()));
						resultingConfiguration.setTravellingOutputs(currentUncertainty.getConfiguration(j).getTravellingOutputs().deepCopy());
						resultingUncertainty.addConfiguration(resultingConfiguration);
					}
					
					ConsoleWindow.output("Resulting uncertainty after signalling this set is: "+resultingUncertainty.printConfigurationSet(moduleDefinition));

					int resultingUncertaintyIndex=findEnvironmentStateIndex(resultingUncertainty,moduleDefinition, environmentDefinition);
					if(resultingUncertaintyIndex==-1){
						StringBuffer resultingConfigurationName = new StringBuffer("E");
						for(int k=0;k<resultingUncertainty.getNoOfConfiguration();k++){
							resultingConfigurationName.append(":"+resultingUncertainty.getConfiguration(k).print(moduleDefinition));
						}
						environmentDefinition.addStateName(resultingConfigurationName.toString());
						
						ConsoleWindow.output("Environment state corresponding to resulting state uncertainty doesn't exist. Adding new state "+resultingConfigurationName.toString());
						
						resultingUncertaintyIndex=environmentDefinition.getNoOfStates()-1;
					}

					SetTransition outputTransition = new SetTransition(environmentStateIndex,
						new IntSet(),resultingUncertaintyIndex,outputSignals,environmentDefinition);

					ConsoleWindow.output("Built environment output transition "+outputTransition.printStringAction()
						+" for environment state "+environmentDefinition.getStateName(environmentStateIndex));
					
					if(environmentDefinition.addTransition(outputTransition)){
						
						ConsoleWindow.output("Environment output transition added, recursing to resulting uncertainty to continue calculating available environment actions");
						
						environmentDefinition=recurse(moduleDefinition, environmentDefinition,
								resultingUncertainty, visitedUncertainties);
						
						ConsoleWindow.output("Leaving recursion and returning to calculation of available environment output actions in state uncertainty "
							+currentUncertainty.printConfigurationSet(moduleDefinition));
						
					}
					else{
						ConsoleWindow.output("Environment output transition already exists, so no need to add. No need to recurse to resulting state uncertainty either");
					}
				}
				
			}
			else{
				
				ConsoleWindow.output("Environment cannot safely signal any input sets in this uncertainty! "
						+ "(individual inputs may be safe to signal but no combinations that can actually affect a module");
				
			}
			
			ConsoleWindow.output("Finished building output transitions (signals to send by environment) for state uncertainty "
				+currentUncertainty.printConfigurationSet(moduleDefinition));

			ConsoleWindow.output("Beginning building input transitions (signals received by environment) for state uncertainty "
				+currentUncertainty.printConfigurationSet(moduleDefinition)+" which correspond to the arrival of outputs from each Configuration");
			
			ConsoleWindow.output("Retrieiving set of auto-fire paths which can result from each configuration in the uncertainty");

			Vector<Vector<Vector<SetTransition>>> pathsFromEachConfiguration = new Vector<Vector<Vector<SetTransition>>>();

			for(int i=0;i<currentUncertainty.getNoOfConfiguration();i++){
				pathsFromEachConfiguration.add(moduleDefinition.getAutoFiredPaths(currentUncertainty.getConfiguration(i).getState(), 
						currentUncertainty.getConfiguration(i).getPendingSignals(), new Vector<SetTransition>()));
			}
			
			ConsoleWindow.output("Set of paths retrieved");
			
			ConsoleWindow.output("Building a set of transitions for each configuration in the uncertainty, where each transition factors in the environment's receipt of"
					+ " the travelling outputs from the Configuration, together with the outputs from all possible (including empty) prefixes of the auto-fire paths possible"
					+ " for the Configuration");
			
			/* For every Configuration in the unclear Configuration set */
			for(int i=0;i<currentUncertainty.getNoOfConfiguration();i++){
				
				Configuration currentConfiguration = currentUncertainty.getConfiguration(i);
				
				ConsoleWindow.output("Building transitions for the receipt of outputs from configuration: "+currentConfiguration.print(moduleDefinition));
				
				/* Retrieve the set of auto-fire paths for the Configuration */
				Vector<Vector<SetTransition>> pathsFromThisConfiguration=pathsFromEachConfiguration.get(i);
			
				/* For every auto-fire path in this Configuration (where j=-1 is the empty path) */
				for(int j=-1;j<pathsFromThisConfiguration.size();j++){
					
					Vector<SetTransition> currentPath=null;
					int pathLength=0;
					int loopTimes;
					
					/*If currently processing the empty path then set length of path to 0 (current path remains unused)*/
					if(j==-1){
						if(currentConfiguration.getTravellingOutputs().size()==0){
							ConsoleWindow.output("There are no travelling outputs in this configuration, so skipping"
									+ " building a transition where no auto-fires have occurred (as there are no outputs to receive)");
							continue;
						}
						ConsoleWindow.output("Building transition corresponding to no auto-fires in this configuration, and the environment simply receives the set of travelling outputs "
								+currentConfiguration.getTravellingOutputs().printStringRepresentation(moduleDefinition.getOutputNames()));
						loopTimes=1;
					}
					
					/*If processing a non empty path then retrieve the current path and its length*/
					else{
						currentPath=pathsFromThisConfiguration.get(j);
						pathLength=currentPath.size();
						loopTimes=pathLength;
						ConsoleWindow.output("Building transitions corresponding to the auto-fire in this configuration, i.e. the path: "+printAutoFirePath(currentPath,currentPath.size()-1));
					}
					
					IntSet currentEnvironmentInputsFromAccumulatedOutputs = currentConfiguration.getTravellingOutputs();
					IntSet currentPendingInputsForModule = currentConfiguration.getPendingSignals().deepCopy();
					
					/* For every prefix of the path */
					for(int k=0;k<loopTimes;k++){
						
						int resultingStateForThisSubPathConfiguration;
						
						/*If current processing the empty path then we don't deal with any subpath data, we just directly deal with
						 * the empty path corresponding to no transitions in the current Configuration*/
						if(pathLength==0){
							resultingStateForThisSubPathConfiguration=currentConfiguration.getState();
							
							ConsoleWindow.output("Resulting state for the current configuration is simply the same as before (as no auto-fire transitions). i.e: "
									+moduleDefinition.getStateName(resultingStateForThisSubPathConfiguration));
							ConsoleWindow.output("Pending signals for the current configuration is simply the same as before (as no auto-fire transitions). i.e: "
									+currentPendingInputsForModule.printStringRepresentation(moduleDefinition.getInputNames()));
							ConsoleWindow.output("Environment inputs is simply the travelling outputs from the configuration (as no auto-fire transitions). i.e: "
									+currentEnvironmentInputsFromAccumulatedOutputs.printStringRepresentation(moduleDefinition.getOutputNames()));
							
						}
						else{

							ConsoleWindow.output("Building transition for the prefix of length "+(k+1)+" for the current auto-fire path: i.e. the prefix: "
									+printAutoFirePath(currentPath,k));
							
							/* Work out the resulting state, pending signals, and travelling outputs if this path is reached by the Configuration */
							resultingStateForThisSubPathConfiguration = currentPath.get(k).getResultState();							
							currentPendingInputsForModule= currentPendingInputsForModule.setDifference
									(currentPath.get(k).getInputSet());
							currentEnvironmentInputsFromAccumulatedOutputs = 
									currentEnvironmentInputsFromAccumulatedOutputs.setUnion(currentPath.get(k).getOutputSet());
							
							ConsoleWindow.output("Resulting state for configuration is "+moduleDefinition.getStateName(resultingStateForThisSubPathConfiguration));
							ConsoleWindow.output("Pending signals for configuration is: "
									+currentPendingInputsForModule.printStringRepresentation(moduleDefinition.getInputNames()));
							ConsoleWindow.output("Environment inputs is the travelling outputs from the configuration combined with the accumulated outputs from this prefix path. i.e: "
									+currentEnvironmentInputsFromAccumulatedOutputs.printStringRepresentation(moduleDefinition.getOutputNames()));
							if(environmentDefinition.getTransitionsWithSourceAndInput(currentEnvironmentInputsFromAccumulatedOutputs, environmentStateIndex).size()>0){
								ConsoleWindow.output("Action already exists in this environment state, for this set of environment inputs, so skipping to next"
										+ "prefix of this autopath");
								continue;
							}
						}
						
						ConsoleWindow.output("Resulting travelling outputs are empty");
						
						/* The resulting unclear Configuration set if this is processed contains the above Configuration obviously */
						Uncertainty resultingUncertaintyForThisSubPath= new Uncertainty();
						Configuration resultingConfigurationForThisSubPath = new Configuration();
						resultingConfigurationForThisSubPath.setState(resultingStateForThisSubPathConfiguration);
						resultingConfigurationForThisSubPath.setPendingSignals(currentPendingInputsForModule.deepCopy());
						resultingConfigurationForThisSubPath.setTravellingOutputs(new IntSet());
						resultingUncertaintyForThisSubPath.addConfiguration(resultingConfigurationForThisSubPath);
						
						ConsoleWindow.output("Hence modified configuration is: "+resultingConfigurationForThisSubPath.print(moduleDefinition));
						ConsoleWindow.output("Modified configuration is added to the resulting uncertainty for this transition. "
								+ "Now we calculate any additional configuration in the uncertainty (which can't be ruled out based on outputs the environment has received)");
					
						/* For every Configuration in the unclear Configuration set */
						for(int l=0;l<currentUncertainty.getNoOfConfiguration();l++){
							
							Configuration currentConfiguration2 = currentUncertainty.getConfiguration(l);
							
							if(l!=i){
								ConsoleWindow.output("Checking if other configuration: "+currentConfiguration2.print(moduleDefinition)+" can contribute to the resulting uncertainty");
							}
							else{
								ConsoleWindow.output("Checking if any other possible auto-fire path from the same configuration can contribute to the resulting uncertainty");								
							}
							/* Retrieve the set of auto-fire paths for the Configuration */
							Vector<Vector<SetTransition>> pathsFromThisConfiguration2=pathsFromEachConfiguration.get(l);
						
							/* For every auto-fire path in this Configuration (where m=-1 is the empty path) */
							for(int m=-1;m<pathsFromThisConfiguration2.size();m++){
								
								Vector<SetTransition> currentPath2=null;
								int pathLength2=0;
								int loopTimes2;
								
								/*If currently processing the empty path then set length of path to 0 (current path remains unused)*/
								if(m==-1){
									loopTimes2=1;
									ConsoleWindow.output("Checking if other configuration with no auto-fires can contribute to uncertainty");
								}
								
								/*If processing a non empty path then retrieve the current path and its length*/
								else{
									currentPath2=pathsFromThisConfiguration2.get(m);
									pathLength2=currentPath2.size();
									loopTimes2=pathLength2;
									ConsoleWindow.output("Checking if other configuration's auto-fire path: "+printAutoFirePath(currentPath2,currentPath2.size()-1)+" can contribute to uncertainty");
								}
								
								IntSet currentEnvironmentInputsFromAccumulatedOutputs2 = currentConfiguration2.getTravellingOutputs();
								IntSet currentPendingInputsForModule2 = currentConfiguration2.getPendingSignals().deepCopy();
								
								/* For every prefix of the path */
								for(int n=0;n<loopTimes2;n++){
									
									/* Do not compare a subpath of a Configuration with itself absolutely */
									if(i==l && j==m && n<=k){
										ConsoleWindow.output("Comparing subpath of current configuration with prefix of itself, "
												+ "so skipping this particular part of the analysis (it definitely cannot contribute to uncertainty)");
										continue;
									}
									
									int resultingStateForThisSubPathConfiguration2;
									
									/*If current processing the empty path then we don't deal with any subpath data, we just directly deal with
									 * the empty path corresponding to no transitions in the current Configuration*/
									if(pathLength2==0){
										resultingStateForThisSubPathConfiguration2=currentConfiguration2.getState();
	
										ConsoleWindow.output("Resulting state for the other configuration would simply be the same as before (as no auto-fire transitions). i.e: "
												+moduleDefinition.getStateName(resultingStateForThisSubPathConfiguration2));
										ConsoleWindow.output("Pending signals for the other configuration would simply be the same as before (as no auto-fire transitions). i.e: "
												+currentPendingInputsForModule2.printStringRepresentation(moduleDefinition.getInputNames()));
										ConsoleWindow.output("Environment inputs would simply be the travelling outputs from the other configuration (as no auto-fire transitions). i.e: "
												+currentEnvironmentInputsFromAccumulatedOutputs2.printStringRepresentation(moduleDefinition.getOutputNames()));
										
									}
									else{		
										
										ConsoleWindow.output("Checking other configuration auto-fire prefix of length "+(n+1)+" for the current other configuration auto-fire path: i.e. the prefix: "
												+printAutoFirePath(currentPath2,n));
										
										/* Work out the resulting state, pending signals, and travelling outputs if this path is reached by the Configuration */
										resultingStateForThisSubPathConfiguration2 = currentPath2.get(n).getResultState();
										
										currentPendingInputsForModule2= currentPendingInputsForModule2.setDifference
												(currentPath2.get(n).getInputSet());
										currentEnvironmentInputsFromAccumulatedOutputs2 = 
												currentEnvironmentInputsFromAccumulatedOutputs2.setUnion(currentPath2.get(n).getOutputSet());
										
										ConsoleWindow.output("Resulting state for other configuration would be "+moduleDefinition.getStateName(resultingStateForThisSubPathConfiguration2));
										ConsoleWindow.output("Pending signals for other configuration would be: "
												+currentPendingInputsForModule2.printStringRepresentation(moduleDefinition.getInputNames()));
										ConsoleWindow.output("Environment inputs would be travelling outputs from the other configuration combined with the accumulated outputs from the other configuration's current prefix path. i.e: "
												+currentEnvironmentInputsFromAccumulatedOutputs2.printStringRepresentation(moduleDefinition.getOutputNames()));
										
									}
									
									if(currentEnvironmentInputsFromAccumulatedOutputs.subset(currentEnvironmentInputsFromAccumulatedOutputs2)){
									
										ConsoleWindow.output("Environment inputs for other Configuration's current subpath IS a superset of the current configuration's current subpath's environment inputs, "
												+ "and so there is additional uncertainty from this other configuration's current subpath and it must be added to the current transition's resulting uncertainty");
										
										Configuration resultingConfigurationForThisSubPath2 = new Configuration();
										resultingConfigurationForThisSubPath2.setState(resultingStateForThisSubPathConfiguration2);
										resultingConfigurationForThisSubPath2.setPendingSignals(currentPendingInputsForModule2.deepCopy());
										resultingConfigurationForThisSubPath2.setTravellingOutputs(currentEnvironmentInputsFromAccumulatedOutputs2.setDifference(currentEnvironmentInputsFromAccumulatedOutputs));
									
										ConsoleWindow.output("Resulting configuration from this other configuration's current subpath is: "+resultingConfigurationForThisSubPath2.print(moduleDefinition));
										ConsoleWindow.output("This other modified configuration is added to the resulting uncertainty for this transition.");
										
										resultingUncertaintyForThisSubPath.addConfiguration(resultingConfigurationForThisSubPath2);
									}
									else{
										ConsoleWindow.output("Environment inputs for other configuration's current subpath are not a superset of the current configuration's current subpath's environment inputs, "
												+ "and so there is no uncertainty resulting from this other configuration's current subpath");
									}
								}
							}
							
						}
						
						ConsoleWindow.output("Resulting uncertainty after receiving inputs "+
								currentEnvironmentInputsFromAccumulatedOutputs.printStringRepresentation(moduleDefinition.getOutputNames())
							+" is: "+resultingUncertaintyForThisSubPath.printConfigurationSet(moduleDefinition));
						
						/* Create a transition for the resulting unclear Configuration set */
						int resultingUncertaintyForThisSubPathIndex=findEnvironmentStateIndex(resultingUncertaintyForThisSubPath,moduleDefinition,environmentDefinition);
						if(resultingUncertaintyForThisSubPathIndex==-1){
							StringBuffer resultingConfigurationName = new StringBuffer("E");
							for(int l=0;l<resultingUncertaintyForThisSubPath.getNoOfConfiguration();l++){
								resultingConfigurationName.append(":"+resultingUncertaintyForThisSubPath.getConfiguration(l).print(moduleDefinition));
							}
							environmentDefinition.addStateName(resultingConfigurationName.toString());
							
							ConsoleWindow.output("Environment state corresponding to resulting state uncertainty doesn't exist. Adding new state "+resultingConfigurationName.toString());
							
							resultingUncertaintyForThisSubPathIndex=environmentDefinition.getNoOfStates()-1;
						}
						SetTransition inputTransition = new SetTransition(environmentStateIndex,
								currentEnvironmentInputsFromAccumulatedOutputs,resultingUncertaintyForThisSubPathIndex,new IntSet(),environmentDefinition);
						
						ConsoleWindow.output("Built environment input transition "+inputTransition.printStringAction()
						+" for environment state "+environmentDefinition.getStateName(environmentStateIndex));
						
						if(environmentDefinition.addTransition(inputTransition)){	
							
							ConsoleWindow.output("Environment input transition added, recursing to resulting uncertainty to continue calculating available environment actions");
							
							environmentDefinition=recurse(moduleDefinition,environmentDefinition,
									resultingUncertaintyForThisSubPath,visitedUncertainties);
							
							ConsoleWindow.output("Leaving recursion and returning to calculation of available environment input actions in state uncertainty "
									+currentUncertainty.printConfigurationSet(moduleDefinition));
							
						}			
						else{
							ConsoleWindow.output("Environment input transition already exists, so no need to add. No need to recurse to resulting state uncertainty either");
						}					
						
					}
				}
				
			}
		}
		else{
			ConsoleWindow.output("This uncertainty has already been visited and won't be recalculated");
		}
		return environmentDefinition;
	}

	/* This checks whether an uncertainty has been visited by the recursive algorithm yet, by checking
	 * the list of visited uncertainties for membership */
	private static boolean UncertaintyVisited(Uncertainty Uncertainty, Vector<Uncertainty> visitedUncertainties) {
		for(int i=0;i<visitedUncertainties.size();i++){
			if(visitedUncertainties.get(i).equals(Uncertainty)){
				return true;
			}
		}
		return false;
	}
	
	/* Simply prints out an auto-path up to a particular number of transitions. Very specific to this
	 * algorithm's ConsoleWindow.output calls, and so is in this class rather than SetNotationModule */
	private static String printAutoFirePath(Vector<SetTransition> path,int endIndex){
		StringBuffer output = new StringBuffer();
		for(int i=0;i<=endIndex;i++){
			output.append(path.get(i).printStringAction());
			if(i<path.size()-1){
				output.append(" -> ");
			}
		}
		return output.toString();
	}
	
	/* This finds the environment state index which corresponds to a given uncertainty. It does this by working out
	 * from each environment state name, what uncertainty it corresponds to, and then comparing 
	 * it against the given uncertainty */
	private static int findEnvironmentStateIndex(Uncertainty uncertaintyToFind, SetNotationModule moduleDefinition,
			SetNotationModule environmentDefinition) {

		/* Iterate through all environment states */
		for(int i=0;i<environmentDefinition.getNoOfStates();i++){

			/* Build the uncertainty object for this state */
			String[] splitUncertaintyAtColon = environmentDefinition.getStateName(i).split("\\Q:\\E");
			String[] currentConfigurationStrings = new String[splitUncertaintyAtColon.length-1];
			
			for(int j=1;j<splitUncertaintyAtColon.length;j++){
				currentConfigurationStrings[j-1]=splitUncertaintyAtColon[j].substring(1, splitUncertaintyAtColon[j].length()-1);
			}

			Uncertainty builtUncertainty = new Uncertainty();

			/* Build each configuration within the current state's uncertainty object */
			for(int j=0;j<currentConfigurationStrings.length;j++){
				Configuration builtConfiguration = new Configuration();
				String currentConfigurationString = currentConfigurationStrings[j];
				String[] semiColonSplit = currentConfigurationString.split("\\Q;\\E");
				
				/* Determine the module state name */
				String currentConfigurationStateName=semiColonSplit[0];
				int currentConfigurationStateIndex=moduleDefinition.getStateIndex(currentConfigurationStateName);
				
				/* Determine the input set */
				String currentConfigurationInputSetString = semiColonSplit[1];
				IntSet currentConfigurationInputSet = new IntSet();
				if(currentConfigurationInputSetString.length()>2){
					String currentConfigurationInputSetTrim=currentConfigurationInputSetString.substring(1,
							currentConfigurationInputSetString.length()-1);
					String[] currentConfigurationInputStrings = currentConfigurationInputSetTrim.split("\\Q,\\E");
					for(int k=0;k<currentConfigurationInputStrings.length;k++){
						currentConfigurationInputSet.add(moduleDefinition.getInputIndex(currentConfigurationInputStrings[k]));
					}
				}
				
				/* Determine the output set */
				String currentConfigurationOutputsString = semiColonSplit[2];
				IntSet currentConfigurationOutputSet = new IntSet();
				if(currentConfigurationOutputsString.length()>2){
					String currentConfigurationOutputsTrim=currentConfigurationOutputsString.substring(1,
							currentConfigurationOutputsString.length()-1);
					String[] outputsStrings = currentConfigurationOutputsTrim.split("\\Q,\\E");
					for(int k=0;k<outputsStrings.length;k++){
						currentConfigurationOutputSet.add(moduleDefinition.getOutputIndex(outputsStrings[k]));
					}
				}
				builtConfiguration.setState(currentConfigurationStateIndex);
				builtConfiguration.setPendingSignals(currentConfigurationInputSet);
				builtConfiguration.setTravellingOutputs(currentConfigurationOutputSet);
				builtUncertainty.addConfiguration(builtConfiguration);
			}
			
			/* Work out if it corresponds to the given uncertainty, and return its index if true */
			if(builtUncertainty.equals(uncertaintyToFind)){
				return i;
			}
		}
		
		/* This means that the given uncertainty does not have a corresponding environment state
		 * built just yet */
		return -1;
	}

	
}
