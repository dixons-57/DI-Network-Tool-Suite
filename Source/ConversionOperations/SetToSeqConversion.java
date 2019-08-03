package ConversionOperations;

import java.util.Vector;

import CommonStructures.IntSet;
import CommonStructures.SetSet;
import GUI.ConsoleWindow;
import SequentialMachineStructure.NDSequentialMachine;
import SequentialMachineStructure.SeqTransition;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Includes the algorithm for converting a Set Notation module definition to a (ND) sequential machine definition according to the
 * algorithm given in the thesis. It is used by the Conversion tab */
public class SetToSeqConversion {

	/* Checks whether a module can be converted to a sequential machine, or whether it must be converted to a (ND) sequential machine
	 * according to the conditions given in the thesis */
	public static int convertibleToSeq(SetNotationModule setDefinition){

		for(int i=0;i<setDefinition.getNoOfTransitions();i++){
			SetTransition currentTransition = setDefinition.getTransition(i);
			if(setDefinition.availablePermutations(currentTransition.getInputSet(),currentTransition.getSourceState())< 
					setDefinition.occurrences(currentTransition.getInputSet(),currentTransition.getSourceState())){
				ConsoleWindow.output("The number of available permutations/sequences for the input set "+
						currentTransition.getInputSet().printStringRepresentation(setDefinition.getInputNames())+" in state "+
						setDefinition.getStateName(currentTransition.getSourceState())
						+" is less than the number of occurrences of the set in state "+
						setDefinition.getStateName(currentTransition.getSourceState()) +
						", so it cannot be realised as a sequential machine");
				return 2;
			}
		}	
		return 1;
	}		
	
	/* Converts any given Set Notation module to a (ND if required) sequential machine according to the algorithm given in the thesis.
	 * It follows the algorithm very closely. See ConsoleWindow.output calls below for more information as to what operations are
	 * performed at each stage */
	public static NDSequentialMachine convertAnyToSeq(SetNotationModule setDefinition) {
		NDSequentialMachine builtSeqDefinition = new NDSequentialMachine();

		ConsoleWindow.output("Adding list of state names, input names, and output names to sequential definition");

		for(int i=0;i<setDefinition.getNoOfInputs();i++){
			builtSeqDefinition.addInputName(setDefinition.getInputName(i));
		}
		for(int i=0;i<setDefinition.getNoOfOutputs();i++){
			builtSeqDefinition.addOutputName(setDefinition.getOutputName(i));
		}
		for(int i=0;i<setDefinition.getNoOfStates();i++){
			builtSeqDefinition.addStateName(setDefinition.getStateName(i));
		}
		for(int i=0;i<setDefinition.getNoOfStates();i++){

			ConsoleWindow.output("Building (ND)-sequential machine state "+setDefinition.getStateName(i));

			int sourceState=i;
			Vector<IntSet> unavailablePermutations = new Vector<IntSet>();
			Vector<Vector<SetTransition>> aggregatedByInputSetsInIncreasingSize = setDefinition.aggregateTransitionsByInputSet(sourceState);

			ConsoleWindow.output("Aggregating transitions for set module state "+setDefinition.getStateName(i)+" by their input set, in increasing size");

			for(int j=0;j<aggregatedByInputSetsInIncreasingSize.size();j++){
				Vector<SetTransition> transitionsForInputSet=aggregatedByInputSetsInIncreasingSize.get(j);

				ConsoleWindow.output("Building (ND)-sequential machine transitions for input set "+
						transitionsForInputSet.get(0).getInputSet().printStringRepresentation(setDefinition.getInputNames())+" in state "+setDefinition.getStateName(i));

				if(!builtSeqDefinition.getAFunction().contains(sourceState,transitionsForInputSet.get(0).getInputSet())){
					builtSeqDefinition.getAFunction().addSetToState(transitionsForInputSet.get(0).getInputSet(), sourceState);
					ConsoleWindow.output("A function for state "+builtSeqDefinition.getStateName(i)+" doesn't contain a set which contains "+
							transitionsForInputSet.get(0).getInputSet().printStringRepresentation(setDefinition.getInputNames())+", so the input set is added");
				}

				Vector<IntSet> availablePermutationsForInputSet = transitionsForInputSet.get(0).getInputSet().permutations();

				ConsoleWindow.output("Calculated list of all permutations for input set "+
						transitionsForInputSet.get(0).getInputSet().printStringRepresentation(setDefinition.getInputNames()));

				for(int k=availablePermutationsForInputSet.size()-1;k>=0;k--){
					for(int l=0;l<unavailablePermutations.size();l++){
						if(unavailablePermutations.get(l).prefix(availablePermutationsForInputSet.get(k))){
							ConsoleWindow.output("Permutation "+availablePermutationsForInputSet.get(k).printStringRepresentation(setDefinition.getInputNames())
									+" is unavailable as used permutation "+unavailablePermutations.get(l).printStringRepresentation(setDefinition.getInputNames())
									+" is a prefix, so it is removed from the list");
							availablePermutationsForInputSet.remove(k);
							break;
						}
					}
				}

				ConsoleWindow.output("Finished calculating list of available permutations for processing input set "+
						transitionsForInputSet.get(0).getInputSet().printStringRepresentation(setDefinition.getInputNames())+" in state "+setDefinition.getStateName(i));

				for(int k=0;k<availablePermutationsForInputSet.size();k++){
					ConsoleWindow.output("Permutation available: "+availablePermutationsForInputSet.get(k).printStringRepresentation(setDefinition.getInputNames()));
				}

				unavailablePermutations.addAll(availablePermutationsForInputSet);

				for(int k=transitionsForInputSet.size()-1;k>=0;k--){

					SetTransition currentTransition = transitionsForInputSet.remove(k);

					ConsoleWindow.output("Building set of sequential machine transitions for set transition: "+currentTransition.printStringAction());

					Vector<IntSet> permutationsToUse;
					if(k==0){						
						ConsoleWindow.output("This is the last transition for this input set in this state, so utilising all remaining available permutations");
						permutationsToUse=availablePermutationsForInputSet;

						for(int l=0;l<availablePermutationsForInputSet.size();l++){
							ConsoleWindow.output("Utilising permutation: "+availablePermutationsForInputSet.get(l).printStringRepresentation(setDefinition.getInputNames()));
						}

					}
					else{
						permutationsToUse=new Vector<IntSet>();
						permutationsToUse.add(availablePermutationsForInputSet.get(0));

						ConsoleWindow.output("Utilising permutation: "+availablePermutationsForInputSet.get(0).printStringRepresentation(setDefinition.getInputNames()));

						if(availablePermutationsForInputSet.size()>1){
							availablePermutationsForInputSet.remove(0);
							ConsoleWindow.output("Removing permutation from available list of permutations, so it won't be reused for more transitions");

						}
						else{
							ConsoleWindow.output("This is the last remaining permutation available, so it will be left and "
									+ "utilised for subsequent transitions for this input set in this state (resulting in a ND-sequential machine)");
						}
					}

					for(int l=0;l<permutationsToUse.size();l++){
						IntSet permutationToBuild = permutationsToUse.get(l);

						ConsoleWindow.output("Building sequential machine transitions for permutation: "+
								permutationToBuild.printStringRepresentation(setDefinition.getInputNames()));

						if(permutationToBuild.size()==1){

							ConsoleWindow.output("Permutation is of length 1, so the original transition "+currentTransition.printStringAction()
							+" is simply converted to sequential machine form");

							SeqTransition allInOneTransition = new SeqTransition(i,currentTransition.getInputSet().get(0),
									currentTransition.getResultState(),currentTransition.getOutputSet().deepCopy(),builtSeqDefinition);
							builtSeqDefinition.addTransition(allInOneTransition);
						}
						else{			
							ConsoleWindow.output("Building first transition which processes first (0th) input of the permutation: "+setDefinition.getInputName(permutationToBuild.get(0)));
							String currentResultStateString = setDefinition.getStateName(sourceState)+"-"+permutationToBuild.get(0);
							int currentResultStateIndex = builtSeqDefinition.getStateIndex(currentResultStateString);
							if(currentResultStateIndex==-1){
								ConsoleWindow.output("Sequential machine state corresponding to signalling "+setDefinition.getInputName(permutationToBuild.get(0))+
										" in state "+builtSeqDefinition.getStateName(i)+" doesn't exist, so it is created for use as the resulting state");
								builtSeqDefinition.addStateName(currentResultStateString);
								currentResultStateIndex=builtSeqDefinition.getNoOfStates()-1;
							}
							else{
								ConsoleWindow.output("Sequential machine state corresponding to signalling "+setDefinition.getInputName(permutationToBuild.get(0))+
										" in state "+builtSeqDefinition.getStateName(sourceState)+" already exists, so it is used as the resulting state");
							}
							SeqTransition startTransition = new SeqTransition(sourceState,permutationToBuild.get(0),currentResultStateIndex,new IntSet(),builtSeqDefinition);
							if(builtSeqDefinition.addTransition(startTransition)){
								ConsoleWindow.output("Adding transition: "+startTransition.printStringAction(false)+" to state "+builtSeqDefinition.getStateName(sourceState));
							}
							else{
								ConsoleWindow.output("Transition: "+startTransition.printStringAction(false)+" in state "+setDefinition.getStateName(sourceState) +
										" already exists so it doesn't need to be added");
							}

							int currentSourceStateIndex;
							for(int m=1;m<permutationToBuild.size()-1;m++){

								ConsoleWindow.output("Building transition which processes "+m+"th input of the permutation: "+setDefinition.getInputName(permutationToBuild.get(m)));

								currentSourceStateIndex = currentResultStateIndex;
								currentResultStateString = currentResultStateString+","+permutationToBuild.get(m);
								currentResultStateIndex = builtSeqDefinition.getStateIndex(currentResultStateString);
								if(currentResultStateIndex==-1){

									ConsoleWindow.output("Sequential machine state corresponding to signalling "+setDefinition.getInputName(permutationToBuild.get(m))+
											" in state "+builtSeqDefinition.getStateName(currentSourceStateIndex)+" doesn't exist, so it is created for use as the resulting state");

									builtSeqDefinition.addStateName(currentResultStateString);
									currentResultStateIndex=builtSeqDefinition.getNoOfStates()-1;
								}
								else{
									ConsoleWindow.output("Sequential machine state corresponding to signalling "+setDefinition.getInputName(permutationToBuild.get(m))+
											" in state "+builtSeqDefinition.getStateName(currentSourceStateIndex)+" already exists, so it is used as the resulting state");
								}
								SeqTransition currentIntermediateTransition = new SeqTransition(currentSourceStateIndex,
										permutationToBuild.get(m),currentResultStateIndex,new IntSet(),builtSeqDefinition);
								if(builtSeqDefinition.addTransition(currentIntermediateTransition)){
									ConsoleWindow.output("Adding transition: "+currentIntermediateTransition.printStringAction(false)+" to state "+
											builtSeqDefinition.getStateName(currentSourceStateIndex));
								}
								else{
									ConsoleWindow.output("Transition: "+currentIntermediateTransition.printStringAction(false)+" in state "+
											builtSeqDefinition.getStateName(currentSourceStateIndex) +
											" already exists so it doesn't need to be added");
								}
								if(!builtSeqDefinition.getAFunction().contains(currentSourceStateIndex,permutationToBuild.getSubSet(m,permutationToBuild.size()-1))){
									builtSeqDefinition.getAFunction().addSetToState(permutationToBuild.getSubSet(m,permutationToBuild.size()-1), currentSourceStateIndex);
									ConsoleWindow.output("A function for state "+builtSeqDefinition.getStateName(currentSourceStateIndex)+" doesn't contain a set which contains "+
											permutationToBuild.getSubSet(m,permutationToBuild.size()-1).printStringRepresentation(setDefinition.getInputNames())+
											" (the current remaining signals of the permutation), "
											+ "so the input set is added");
								}
							}
							currentSourceStateIndex=currentResultStateIndex;

							ConsoleWindow.output("Building final transition which processes final ("+(permutationToBuild.size()-1)+"th) input of the permutation: "+
									setDefinition.getInputName(permutationToBuild.get(permutationToBuild.size()-1))+
									" and produces the set transition's output ("+currentTransition.getOutputSet().printStringRepresentation(setDefinition.getOutputNames())+")");

							SeqTransition finalTransition = new SeqTransition(currentSourceStateIndex,permutationToBuild.get(permutationToBuild.size()-1),
									currentTransition.getResultState(),currentTransition.getOutputSet(),builtSeqDefinition);

							if(builtSeqDefinition.addTransition(finalTransition)){
								ConsoleWindow.output("Adding transition: "+finalTransition.printStringAction(false)+" to state "+builtSeqDefinition.getStateName(currentSourceStateIndex));
							}
							else{
								ConsoleWindow.output("Transition: "+finalTransition.printStringAction(false)+" in state "+builtSeqDefinition.getStateName(currentSourceStateIndex) +
										" already exists so it doesn't need to be added");
							}
							if(!builtSeqDefinition.getAFunction().contains(currentSourceStateIndex,new IntSet(permutationToBuild.get(permutationToBuild.size()-1)))){
								builtSeqDefinition.getAFunction().addSetToState(new IntSet(permutationToBuild.get(permutationToBuild.size()-1)), currentSourceStateIndex);
								ConsoleWindow.output("A function for state "+builtSeqDefinition.getStateName(currentSourceStateIndex)+" doesn't contain a set which contains "+
										setDefinition.getInputName(permutationToBuild.get(permutationToBuild.size()-1))+
										" (the final signal of the permutation), "
										+ "so the input is added as a singleton set");
							}
						}
					}
				}
			}
		}
		ConsoleWindow.output("Removing any duplicate state definitions");

		builtSeqDefinition=removeDuplicateStates(builtSeqDefinition);

		ConsoleWindow.output("Tidying state names to prevent conflicts and to remove non-alphanumeric chars (such as dashes, commas etc.)");

		builtSeqDefinition=tidyStateNames(builtSeqDefinition);

		ConsoleWindow.output("Removing redundant sets from A function (a set is redundant if a superset exists in the same state) ");

		builtSeqDefinition=tidyAFunction(builtSeqDefinition);		
		return builtSeqDefinition;
	}
	
	/* Removes duplicate state definitions from the (ND) sequential machine definition. This closely follows the state-merge
	 * algorithm given in the thesis, and is similar to that given in NonArbGeneration, but is modified to be (ND) sequential machine
	 * friendly */
	public static NDSequentialMachine removeDuplicateStates(NDSequentialMachine builtSeqDefinition){
		
		/* Until we know that all states are unique, do the following */
		boolean repeatStateRemoval=true;
		while(repeatStateRemoval){
			
			/* Assume that we don't need to modify states any more */
			repeatStateRemoval=false;
			
			/* Iterate through states beginning from the end of the list, in decreasing index */
			for(int i=builtSeqDefinition.getNoOfStates()-1;i>=0;i--){
				
				/* Retrieve the set of transitions starting in the current state */
				Vector<SeqTransition> firstStateTransitions = builtSeqDefinition.getTransitionsWithSource(i);
				int duplicateStateIndex=-1;
				
				/* Iterate through all states prior to this one in the list and retrieve their transitions */
				for(int j=0;j<i;j++){
					Vector<SeqTransition> secondStateTransitions = builtSeqDefinition.getTransitionsWithSource(j);
					boolean secondStateEquivalent = true;
					
					/* Check whether both states are equivalent in terms of actions */
					if(firstStateTransitions.size()==secondStateTransitions.size()){
						for(int k=0;k<firstStateTransitions.size();k++){
							boolean transitionExistsInSecond =false;
							int firstInput = firstStateTransitions.get(k).getInput();
							IntSet firstOutputSet = firstStateTransitions.get(k).getOutputSet();
							int firstResultState = firstStateTransitions.get(k).getResultState();
							for(int l=0;l<secondStateTransitions.size();l++){
								int secondInput = secondStateTransitions.get(l).getInput();
								IntSet secondOutputSet = secondStateTransitions.get(l).getOutputSet();
								int secondResultState = secondStateTransitions.get(l).getResultState();
								if(firstInput==secondInput && firstOutputSet.equals(secondOutputSet) && firstResultState==secondResultState){
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
					for(int j=builtSeqDefinition.getNoOfTransitions()-1;j>=0;j--){
						if(builtSeqDefinition.getTransition(j).getSourceState()==i){
							builtSeqDefinition.removeTransition(j);
						}
					}

					/* Redirect all transition targets that point to i to point to j instead*/
					for(int j=builtSeqDefinition.getNoOfTransitions()-1;j>=0;j--){
						SeqTransition oldTransition = builtSeqDefinition.getTransition(j);
						if(oldTransition.getResultState()==i){
							builtSeqDefinition.removeTransition(j);
							oldTransition.setTargetState(duplicateStateIndex);
							builtSeqDefinition.addTransition(oldTransition);
						}
					}

					/* Remove i from state names */
					builtSeqDefinition.removeStateName(i);
					builtSeqDefinition.removeAFunctionEntry(i);
					
					/*Renumber ALL transitions sources and targets to be there original value minus IFF it is larger than i,
					 * as recall that we have literally deleted a state in the list, affecting the index of all states
					 * after the ith value */
					for(int j=builtSeqDefinition.getNoOfTransitions()-1;j>=0;j--){
						if(builtSeqDefinition.getTransition(j).getResultState()>i){
							builtSeqDefinition.getTransition(j).setTargetState(builtSeqDefinition.getTransition(j).getResultState()-1);
						}
						if(builtSeqDefinition.getTransition(j).getSourceState()>i){
							builtSeqDefinition.getTransition(j).setSourceState(builtSeqDefinition.getTransition(j).getSourceState()-1);
						}
					}
				}
			}
		}
		
		return builtSeqDefinition;
	}

	/* Tidies the A function for the new (ND) sequential machine. It basically strips out any sets which are subsets
	 * of another set in the same A(q). This is because the definition of the A function necessarily allows any subset
	 * of any defined set to be signalled concurrently. Hence smaller sets which are subsets of larger sets are redundant. */
	static NDSequentialMachine tidyAFunction(NDSequentialMachine builtSeqDefinition){
		for(int i=0;i<builtSeqDefinition.getNoOfStates();i++){
			SetSet AFunctionState = builtSeqDefinition.getAFunction().getState(i);
			for(int j=AFunctionState.size()-1;j>=0;j--){
				IntSet firstAFunctionSet=AFunctionState.get(j);
				for(int k=0;k<AFunctionState.size();k++){
					IntSet secondAFunctionSet=AFunctionState.get(k);
					if(j!=k){
						if(firstAFunctionSet.subset(secondAFunctionSet)){
							AFunctionState.remove(j);
							break;
						}
					}
				}
			}
		}
		return builtSeqDefinition;
	}

	/* Removes special chars from sequential machine state names to make it parseable by the software. */
	static NDSequentialMachine tidyStateNames(NDSequentialMachine builtSeqDefinition) {
		
		/* For every state */
		for(int i=0;i<builtSeqDefinition.getNoOfStates();i++){
			
			/* Strip out non-alphanumeric characters */
			String currentStateString = builtSeqDefinition.getStateName(i);
			String currentStateModified = currentStateString.replaceAll("\\Q-\\E","");
			currentStateModified = currentStateModified.replaceAll("\\Q,\\E","");

			builtSeqDefinition.replaceStateName(i, currentStateModified);
			
			/* If there is a state prior to this one in the list of environment states 
			 * that has the same name, append a bunch of "a"s to this one until it
			 * no longer matches any states prior to it */
			while(builtSeqDefinition.getStateIndex(currentStateModified)!=i){
				currentStateModified=currentStateModified+"a";
				builtSeqDefinition.replaceStateName(i, currentStateModified);
			}
		}
		return builtSeqDefinition;
	}
}
