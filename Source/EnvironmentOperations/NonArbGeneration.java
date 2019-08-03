package EnvironmentOperations;

import java.util.Vector;
import CommonStructures.IntSet;
import GUI.ConsoleWindow;
import GUI.EnvironmentTab;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Contains the faster algorithm for generating the environment for non-arb modules */
public class NonArbGeneration {

	/* The algorithm for generating the environment for non-arb module. It closely follows the
	 * algorithm given in the thesis. See the ConsoleWindow.output calls below for more information as to
	 * what operations are performed at each step */
	public static SetNotationModule nonArb(SetNotationModule moduleDefinition){
		SetNotationModule environmentDefinition = new SetNotationModule();
		
		for(int i=0;i<moduleDefinition.getNoOfStates();i++){
			
			ConsoleWindow.output("Creating environment state E:"+moduleDefinition.getStateName(i) 
				+" corresponding to module state "+moduleDefinition.getStateName(i));
			
			environmentDefinition.addStateName("E:"+moduleDefinition.getStateName(i));
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
		for(int i=0;i<moduleDefinition.getNoOfStates();i++){
			
			ConsoleWindow.output("Retrieving module transitions for module state "+moduleDefinition.getStateName(i));
			
			Vector<SetTransition> transitionsForState=moduleDefinition.getTransitionsWithSource(i);
			
			for(int j=0;j<transitionsForState.size();j++){
				SetTransition moduleTransition = transitionsForState.get(j);
				
				ConsoleWindow.output("Creating environment transitions which correspond to module transition "+moduleTransition.printStringAction());
				
				IntSet environmentTransitionOutput = moduleTransition.getInputSet().deepCopy();
				
				ConsoleWindow.output("Environment output set is:" +environmentTransitionOutput.printStringRepresentation(moduleDefinition.getInputNames()));
				
				IntSet environmentTransitionInput = moduleTransition.getOutputSet().deepCopy();
				
				ConsoleWindow.output("Environment input set is:" +environmentTransitionInput.printStringRepresentation(moduleDefinition.getOutputNames()));
				
				StringBuffer intermediateStateName = new StringBuffer(environmentDefinition.getStateName(i)+":");
				for(int k=0;k<environmentTransitionOutput.size();k++){
					intermediateStateName.append(
							environmentDefinition.getOutputName(environmentTransitionOutput.get(k)));
				}
				environmentDefinition.addStateName(intermediateStateName.toString());
				
				ConsoleWindow.output("Intermediate environment state name (after sending output but before receiving input) is "+intermediateStateName.toString());
				
				SetTransition environmentTransition = new SetTransition(i,new IntSet(),
						environmentDefinition.getNoOfStates()-1,environmentTransitionOutput,environmentDefinition);
				
				ConsoleWindow.output("Created environment output transition "+environmentTransition.printStringAction()+" for environment state "+environmentDefinition.getStateName(i));
				
				SetTransition environmentSubTransition = new SetTransition(
						environmentDefinition.getNoOfStates()-1,environmentTransitionInput,moduleTransition.getResultState(),new IntSet(),
						environmentDefinition);
				
				ConsoleWindow.output("Created environment input transition "+environmentSubTransition.printStringAction()+" for environment state "
						+environmentDefinition.getStateName(environmentDefinition.getNoOfStates()-1));
				
				environmentDefinition.addTransition(environmentTransition);
				environmentDefinition.addTransition(environmentSubTransition);
			}
		}
		
		if(EnvironmentTab.instance.StateRemoval.isSelected()){
			
			ConsoleWindow.output("Removing any duplicate state definitions");
			
			environmentDefinition=removeDuplicateStates(environmentDefinition);
		}
		
		ConsoleWindow.output("Tidying state names to prevent conflicts (within the environment state names, and between environment and module) "
				+ "and to remove non-alphanumeric chars (such as dashes, commas etc.)");
		
		environmentDefinition=tidyStateNames(moduleDefinition,environmentDefinition);
		return environmentDefinition;
	}

	/* Removes special chars from environmentDefinition state names to make it parseable by the software. 
	 * It then ensures that the new state names are not shared by the original module (this may happen by total freak
	 * coincidence, and can be annoying when trying to connect the environment to the module in DI-Set algebra) */
	static SetNotationModule tidyStateNames(SetNotationModule input, SetNotationModule environmentDefinition) {
		
		/* For every environment state */
		for(int i=0;i<environmentDefinition.getNoOfStates();i++){
			
			/* Strip out non-alphanumeric characters */
			String currentState = environmentDefinition.getStateName(i);
			String currentStateModified = currentState.replaceAll("\\Q:\\E","");
			currentStateModified = currentStateModified.replaceAll("\\Q{\\E","");
			currentStateModified = currentStateModified.replaceAll("\\Q}\\E","");
			currentStateModified = currentStateModified.replaceAll("\\Q,\\E","");
			currentStateModified = currentStateModified.replaceAll("\\Q;\\E","");

			environmentDefinition.replaceStateName(i, currentStateModified);
			
			/* If there is a state prior to this one in the list of environment states 
			 * that has the same name, append a bunch of "a"s to this one until it
			 * no longer matches any states prior to it */
			while(environmentDefinition.getStateIndex(currentStateModified)!=i){
				currentStateModified=currentStateModified+"a";
				environmentDefinition.replaceStateName(i, currentStateModified);
			}
			
			/* Also keep appending "a"s to it until it no longer matches a state of the
			 * original corresponding module */
			while(input.getStateIndex(currentStateModified)!=-1){
				currentStateModified=currentStateModified+"a";
			}
			environmentDefinition.replaceStateName(i, currentStateModified);
		}
		return environmentDefinition;
	}

	/* Removes duplicate state definitions from the environment definition. This closely follows the state-merge
	 * algorithm given in the thesis */
	public static SetNotationModule removeDuplicateStates(SetNotationModule environmentDefinition){
		
		/* Until we know that all states are unique, do the following */
		boolean repeatStateRemoval=true;
		while(repeatStateRemoval){
			
			/* Assume that we don't need to modify states any more */
			repeatStateRemoval=false;
			
			/* Iterate through states beginning from the end of the list, in decreasing index */
			for(int i=environmentDefinition.getNoOfStates()-1;i>=0;i--){
				
				/* Retrieve the set of transitions starting in the current state */
				Vector<SetTransition> firstStateTransitions = environmentDefinition.getTransitionsWithSource(i);
				int duplicateStateIndex=-1;
				
				/* Iterate through all states prior to this one in the list and retrieve their transitions */
				for(int j=0;j<i;j++){
					Vector<SetTransition> secondStateTransitions = environmentDefinition.getTransitionsWithSource(j);
					boolean secondStateEquivalent = true;
					
					/* Check whether both states are equivalent in terms of actions */
					if(firstStateTransitions.size()==secondStateTransitions.size()){
						for(int k=0;k<firstStateTransitions.size();k++){
							boolean transitionExistsInSecond =false;
							IntSet firstInputSet = firstStateTransitions.get(k).getInputSet();
							IntSet firstOutputSet = firstStateTransitions.get(k).getOutputSet();
							int firstResultState = firstStateTransitions.get(k).getResultState();
							for(int l=0;l<secondStateTransitions.size();l++){
								IntSet secondInputSet = secondStateTransitions.get(l).getInputSet();
								IntSet secondOutputSet = secondStateTransitions.get(l).getOutputSet();
								int secondResultState = secondStateTransitions.get(l).getResultState();
								if(firstInputSet.equals(secondInputSet) && firstOutputSet.equals(secondOutputSet) && firstResultState==secondResultState){
									transitionExistsInSecond=true;
									break;
								}
							}
							if(!transitionExistsInSecond){
								secondStateEquivalent=false;
								break;
							}
						}
					}
					else{
						secondStateEquivalent=false;
					}
					
					/* If the states contain equivalent transitions, we need to flag the prior state index,
					 * which will "replace" the later state index in all transitions */
					if(secondStateEquivalent){
						
						/* Removing a state and redirecting transitions can suddenly make state's equivalent
						 * which weren't before, so we will need to go through all this again when we have
						 * finished this loop */
						repeatStateRemoval=true;
						duplicateStateIndex=j;
						break;
					}
				}
				
				
				if(duplicateStateIndex!=-1){
					
					/* This means the ith state is equivalent to the prior jth state */

					/*First remove all transitions starting in the ith state */
					for(int j=environmentDefinition.getNoOfTransitions()-1;j>=0;j--){
						if(environmentDefinition.getTransition(j).getSourceState()==i){
							environmentDefinition.removeTransition(j);
						}
					}

					/* Redirect all transition targets that point to i to point to j instead*/
					for(int j=environmentDefinition.getNoOfTransitions()-1;j>=0;j--){
						SetTransition oldTransition = environmentDefinition.getTransition(j);
						if(oldTransition.getResultState()==i){
							environmentDefinition.removeTransition(j);
							oldTransition.setTargetState(duplicateStateIndex);
							environmentDefinition.addTransition(oldTransition);
						}
					}

					/* Remove i from state names */
					environmentDefinition.removeStateName(i);

					/*Renumber ALL transitions sources and targets to be there original value minus IFF it is larger than i,
					 * as recall that we have literally deleted a state in the list, affecting the index of all states
					 * after the ith value */
					for(int j=environmentDefinition.getNoOfTransitions()-1;j>=0;j--){
						if(environmentDefinition.getTransition(j).getResultState()>i){
							environmentDefinition.getTransition(j).setTargetState(environmentDefinition.getTransition(j).getResultState()-1);
						}
						if(environmentDefinition.getTransition(j).getSourceState()>i){
							environmentDefinition.getTransition(j).setSourceState(environmentDefinition.getTransition(j).getSourceState()-1);
						}
					}
				}
			}
		}
		
		return environmentDefinition;
	}
	
}
