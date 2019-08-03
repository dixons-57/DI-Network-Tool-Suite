package SetNotationStructure;

import java.util.Vector;
import CommonStructures.IntSet;

/* Encapsulates an entire Set Notation module definition. It contains lists of input, output
 * and state names, and a list of transitions. The transitions themselves utilise int values,
 * which refer to the input, output and state names via their indexes in these lists. */
public class SetNotationModule {

	/* The list of input names */
	private Vector<String> inputNames = new Vector<String>();
	
	/* The list of output names */
	private Vector<String> outputNames = new Vector<String>();
	
	/* The list of state names */
	private Vector<String> stateNames = new Vector<String>();
	
	/* The list of transitions */
	private Vector<SetTransition> transitions = new Vector<SetTransition>();

	/* Return a deep copy of this definition */
	public SetNotationModule deepCopy(){
		SetNotationModule copy= new SetNotationModule();
		for(int i=0;i<stateNames.size();i++){
			copy.addStateName(stateNames.get(i));
		}
		for(int i=0;i<inputNames.size();i++){
			copy.addInputName(inputNames.get(i));
		}
		for(int i=0;i<outputNames.size();i++){
			copy.addOutputName(outputNames.get(i));
		}
		for(int i=0;i<transitions.size();i++){
			SetTransition current=transitions.get(i);
			copy.addTransition(new SetTransition(current.getSourceState(),
					current.getInputSet().deepCopy(),current.getResultState(),current.getOutputSet().deepCopy(),copy));
		}
		return copy;
	}
	
	/* Adds an input name to the list if it is not already present */
	public boolean addInputName(String name){
		for(int i=0;i<inputNames.size();i++){
			if(inputNames.get(i).equals(name)){
				return false;
			}
		}
		inputNames.add(name);
		return true;
	}

	/* Adds an output name to the list if it is not already present */
	public boolean addOutputName(String name){
		for(int i=0;i<outputNames.size();i++){
			if(outputNames.get(i).equals(name)){
				return false;
			}
		}
		outputNames.add(name);
		return true;
	}

	/* Adds a state name to the list if it is not already present */
	public boolean addStateName(String name){
		for(int i=0;i<stateNames.size();i++){
			if(stateNames.get(i).equals(name)){
				return false;
			}
		}
		stateNames.add(name);
		return true;
	}

	/* Adds a transition to the list if it is not already present */
	public boolean addTransition(SetTransition transition){
		if(!transitionExists(transition)){
			transitions.add(transition);
			return true;
		}
		return false;
	}

	/* This returns a list of sub-lists of transitions. Each sub-list contains transitions which share
	 * the same source state. A sub-list exists for every state of the module */
	public Vector<Vector<SetTransition>> aggregateTransitionsByInputSet(int state){
		Vector<Vector<SetTransition>> transitionsByInputSet = new Vector<Vector<SetTransition>>();
		Vector<IntSet> inputSetsAlreadySeen = new Vector<IntSet>();
		Vector<SetTransition> transitionsForState = getTransitionsWithSource(state);

		for(int i=0;i<transitionsForState.size();i++){
			IntSet currentInputSet = transitionsForState.get(i).getInputSet();
			boolean freshInputSetSetNeeded=true;
			for(int j=0;j<inputSetsAlreadySeen.size();j++){
				if(inputSetsAlreadySeen.get(j).equals(currentInputSet)){
					freshInputSetSetNeeded=false;
					transitionsByInputSet.get(j).add(transitionsForState.get(i));
					break;
				}
			}
			if(freshInputSetSetNeeded){
				int indexPositionToAdd=-1;
				for(int j=0;j<transitionsByInputSet.size();j++){
					if(transitionsByInputSet.get(j).get(0).getInputSet().size()>currentInputSet.size()){
						indexPositionToAdd=j;
						break;
					}
				}
				Vector<SetTransition> freshInputSetSet = new Vector<SetTransition>();
				freshInputSetSet.add(transitionsForState.get(i));
				if(indexPositionToAdd==-1){
					indexPositionToAdd=transitionsByInputSet.size()-1;
					transitionsByInputSet.add(freshInputSetSet);
					inputSetsAlreadySeen.add(currentInputSet);
				}
				else{
					transitionsByInputSet.add(indexPositionToAdd,freshInputSetSet);
					inputSetsAlreadySeen.add(indexPositionToAdd,currentInputSet);
				}
			}
		}
		return transitionsByInputSet;
	}

	/* This checks that all states are the source state of at least one transition */
	public boolean allStatesHaveTransitions(){
		for(int i=0;i<stateNames.size();i++){
			if(getTransitionsWithSource(i).size()==0){
				return false;
			}
		}
		return true;
	}

	/* This checks whether a module is auto-clashing. */
	public boolean autoClashing(){

		/* For every state retrieve the transitions */
		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByState = getTransitionsWithSource(i);

			/* For every transition */
			for(int j=0;j<transitionsByState.size();j++){
				
				/* Retrieve the input set */
				IntSet inputs = transitionsByState.get(j).getInputSet();
				
				/* Find all autopaths which are fired as a result of sending this input set
				 * in this state */
				Vector<Vector<SetTransition>> pathsFromHere = getAutoFiredPaths(i,inputs,new Vector<SetTransition>());
				
				/* For each autopath */
				for(int k=0;k<pathsFromHere.size();k++){
					
					/* Check that there is not an auto-clash on this path */
					if(checkAutoPathClash(pathsFromHere.get(k), new IntSet())){
						return true;
					}
				}
			}
		}
		return false;
	}

	/* This checks whether a module is auto-firing */
	public boolean autoFiring(){

		/* For every state retrieve the transitions */
		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByState = getTransitionsWithSource(i);

			/* For every transition */
			for(int j=0;j<transitionsByState.size();j++){
				
				/* Retrieve the input set */
				IntSet inputs = transitionsByState.get(j).getInputSet();
				
				/* Find all autopaths which are fired as a result of sending this input set
				 * in this state */
				Vector<Vector<SetTransition>> pathsFromHere = getAutoFiredPaths(i,inputs,new Vector<SetTransition>());
				
				/* For each autopath */
				for(int k=0;k<pathsFromHere.size();k++){
					
					/* If the length of the path is longer than 1, then it is auto-firing by definition */
					if(pathsFromHere.get(k).size()>1){
						return true;
					}
				}
			}
		}
		return false;
	}

	/* This checks how many permutations/sequences are "available" to sequentially-implement a given action in a given state, 
	 * after ruling out permutations due to smaller input sets which are a subset. It is used by SetToSeqConversion */
	public int availablePermutations(IntSet inputs, int state){
		
		/* Retrieve all lists of sublists of transitions for the given state, where a sublist of transitions
		 * all share the same input set */
		Vector<Vector<SetTransition>> aggregatedByInputSets = aggregateTransitionsByInputSet(state);
		
		/* Retrieve all sequences/permutations of the given input set */
		Vector<IntSet> availablePermutations=inputs.permutations();
		
		/* For each sublist of transitions */
		for(int i=0;i<aggregatedByInputSets.size();i++){
			
			/* Retrieve the input set shared by this sublist of transitions */
			IntSet currentInputSet = aggregatedByInputSets.get(i).get(0).getInputSet();
			
			/* If this input set is a proper subset of the given input set, it is noteworthy
			 * and will affect the number of available permutations */
			if(currentInputSet.properSubset(inputs)){
				
				/* Retrieve all sequences/permutations of the smaller input set */
				Vector<IntSet> permutationsForSmallerSet = currentInputSet.permutations();
				
				/* For every sequence/permutation of the smaller set */
				for(int j=0;j<permutationsForSmallerSet.size();j++){
					IntSet currentPermutation = permutationsForSmallerSet.get(j);
					
					/* Iterate through each currently "available" sequences/permutations of the larger
					 * set, and remove it if the current smaller permutation is a prefix of the larger permutation */
					for(int k=availablePermutations.size()-1;k>=0;k--){
						if(currentPermutation.prefix(availablePermutations.get(k))){
							availablePermutations.remove(k);
						}
					}
				}
			}
		}
		return availablePermutations.size();
	}

	/* Checks whether a module is arb in the expected way by comparing input sets of transitions */
	public boolean checkArb() {
		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByState = getTransitionsWithSource(i);
			for(int j=0;j<transitionsByState.size();j++){
				IntSet inputs = transitionsByState.get(j).getInputSet();
				for(int k=0;k<transitionsByState.size();k++){
					if(j!=k){
						if(inputs.subset(transitionsByState.get(k).getInputSet())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/* Checks whether the given autopath can lead to an output clash with whatever given output lines
	 * already contain signals. It does this by accumulating outputs from each transition and checking
	 * whether any are already present in the accumulated set at each stage */
	public boolean checkAutoPathClash(Vector<SetTransition> path, IntSet currentTravellingOutputs){
		IntSet accumulatedOutputs = currentTravellingOutputs.deepCopy();
		for(int i=0;i<path.size();i++){
			IntSet currentOutputs = path.get(i).getOutputSet();
			for(int j=0;j<currentOutputs.size();j++){
				if(accumulatedOutputs.contains(currentOutputs.get(j))!=-1){
					return true;
				}
				else{
					accumulatedOutputs.add(currentOutputs.get(j));
				}
			}
		}
		return false;
	}

	/* Checks whether processing a given autopath can result in non-safety. It simply iterates through
	 * each transition, removing inputs which are pending at each point (as they have been
	 * absorbed by the module) and then checking that safety holds for the current state
	 * It ignores the "starting" state, and the checks begin in the state resulting from processing the first action.
	 * The safelyDefined method can be used to check the state prior to processing the autopath */
	public boolean checkAutoPathSafe(Vector<SetTransition> path, IntSet startingInputs){
		IntSet pendingInputs = startingInputs.deepCopy();
		for(int i=0;i<path.size();i++){
			SetTransition currentTransition=path.get(i);
			pendingInputs=pendingInputs.setDifference(currentTransition.getInputSet());
			if(!safelyDefined(pendingInputs,currentTransition.getResultState())){
				return false;
			}
		}
		return true;
	}

	/* Checks whether a module is b-arb in the expected way by comparing output sets of transitions */
	public boolean checkBarb(){
		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByTargetState = getTransitionsWithTarget(i);
			for(int j=0;j<transitionsByTargetState.size();j++){
				IntSet outputs = transitionsByTargetState.get(j).getOutputSet();
				for(int k=0;k<transitionsByTargetState.size();k++){
					if(j!=k){
						if(outputs.subset(transitionsByTargetState.get(k).getOutputSet())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/* Checks whether a module is eq-arb. If the module is arb then the algorithm assumes
	 * it is eq-arb, until it finds an instance where one input set is a proper subset of
	 * another in the same state */
	public boolean checkEqArb() {

		/* If module is non-arb then immediately say false */
		if(!checkArb()){
			return false;
		}

		/* Otherwise assume eq-arb and then attempt to falsify by finding a proper subset */
		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByState = getTransitionsWithSource(i);
			for(int j=0;j<transitionsByState.size();j++){
				IntSet inputs = transitionsByState.get(j).getInputSet();
				for(int k=0;k<transitionsByState.size();k++){
					if(j!=k){
						if(inputs.properSubset(transitionsByState.get(k).getInputSet())){
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	/* Checks whether a module is 1-step consistent in the expected way. It first searches for
	 * an action which has inclusivity between input sets (i.e. arbitration), and then checks
	 * for inclusivity between output sets for this action. */
	public boolean checkOneStepConsistent() {

		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByState = getTransitionsWithSource(i);

			for(int j=0;j<transitionsByState.size();j++){
				IntSet firstInputSet = transitionsByState.get(j).getInputSet();
				IntSet firstOutputSet = transitionsByState.get(j).getOutputSet();
				for(int k=0;k<transitionsByState.size();k++){
					if(j!=k){
						IntSet secondInputSet=transitionsByState.get(k).getInputSet();
						IntSet secondOutputSet=transitionsByState.get(k).getOutputSet();
						if(firstInputSet.subset(secondInputSet)){
							if(firstOutputSet.subset(secondOutputSet) || secondOutputSet.subset(firstOutputSet)){
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/* Checks whether a module is stable in the expected way, by comparing transitions and checking
	 * for subsets, then checking whether set differences between the subsets are defined in 
	 * the target states of the smaller transitions (thus ensuring safety) */
	public boolean checkStability() {

		for(int i=0;i<stateNames.size();i++){
			Vector<SetTransition> transitionsByState = getTransitionsWithSource(i);

			for(int j=0;j<transitionsByState.size();j++){
				SetTransition firstTransition = transitionsByState.get(j);

				for(int k=0;k<transitionsByState.size();k++){
					if(j!=k){
						SetTransition secondTransition = transitionsByState.get(k);
						if(firstTransition.getInputSet().subset(secondTransition.getInputSet())){
							IntSet setDifference = secondTransition.getInputSet().setDifference(firstTransition.getInputSet());
							if(!safelyDefined(setDifference,firstTransition.getResultState())){
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	} 

	/* Simply checks whether two states contain identical sets of transitions */
	public boolean duplicateStates(){
		for(int i=0;i<stateNames.size()-1;i++){
			Vector<SetTransition> firstStateTransitions = getTransitionsWithSource(i);
			for(int j=i+1;j<stateNames.size();j++){
				Vector<SetTransition> secondStateTransitions = getTransitionsWithSource(j);
				if(firstStateTransitions.size()==secondStateTransitions.size()){
					boolean same=true;
					for(int k=0;k<firstStateTransitions.size();k++){
						SetTransition currentTransition=firstStateTransitions.get(k);
						boolean inSecondSet=false;
						for(int l=0;l<secondStateTransitions.size();l++){
							if(currentTransition.sameAs(secondStateTransitions.get(l))){
								inSecondSet=true;
								break;
							}
						}
						if(!inSecondSet){
							same=false;
							break;
						}

					}
					if(same){
						return true;
					}
				}
			}
		}
		return false;
	}

	/* Checks whether there are any duplicate entries of transitions (recall that
	 * a transition is a 4-tuple (q,A,B,q'), so it just checks whether all components
	 * are equal) */
	public boolean duplicateTransitions(){
		for(int i=0;i<transitions.size()-1;i++){
			for(int j=i+1;j<transitions.size();j++){
				if(transitions.get(i).sameAs(transitions.get(j))){
					return true;
				}
			}
		}
		return false;
	}

	/* Retrieves the set of autopaths for a given state and set of pending input signals. The algorithm
	 * works by checking to see what transitions are enabled in each state, then branching and recursing 
	 * until no more transitions can be processed. Effectively an exhaustive "depth" first search */
	public Vector<Vector<SetTransition>> getAutoFiredPaths(int state, IntSet pendingInputs, Vector<SetTransition> pathHere){
		
		/* Work out which transitions may fire in this state */
		Vector<SetTransition> transitionsFromHere = getTransitionsWithSource(state);
		
		/* This is a global list of paths to return up the stack */
		Vector<Vector<SetTransition>> pathsToReturn = new Vector<Vector<SetTransition>>();

		/* Assume that this path terminates here */
		boolean thisPathTerminatesHere = true;
		
		/* For every transition which may occur from here */
		for(int i=0;i<transitionsFromHere.size();i++){
			SetTransition currentTransition = transitionsFromHere.get(i);
			
			/* Check if it is "enabled" */
			if(currentTransition.getInputSet().subset(pendingInputs)){
				
				/* If it is, the path clearly doesn't terminate here, and continues to the next state */
				thisPathTerminatesHere=false;
				
				/* Make a copy of the path that has led to this state, and add the new transition to the end
				 * of it */
				Vector<SetTransition> pathHereAndNewTransition = new Vector<SetTransition>();
				for(int j=0;j<pathHere.size();j++){
					pathHereAndNewTransition.add(pathHere.get(j));
				}				
				pathHereAndNewTransition.add(currentTransition);
				
				/* Work out the now remaining pending inputs (after processing this transition) */
				IntSet remainingPendingInputs = pendingInputs.setDifference(currentTransition.getInputSet());
				
				/* Recurse, and see what options there are for the path to continue. It may possibly branch
				 * and return multiple paths */
				Vector<Vector<SetTransition>> pathsHereAndOnwards = getAutoFiredPaths
						(currentTransition.getResultState(),remainingPendingInputs,pathHereAndNewTransition);
				
				/* These "continued" paths are sent back up the stack */
				for(int j=0;j<pathsHereAndOnwards.size();j++){
					pathsToReturn.add(pathsHereAndOnwards.get(j));
				}
			}
		}
		
		if(thisPathTerminatesHere){
			pathsToReturn.add(pathHere);
		}
		return pathsToReturn;
	}

	/* Get the index value of a given input name in the overall input name list */
	public int getInputIndex(String name){
		for(int i=0;i<inputNames.size();i++){
			if(inputNames.get(i).equals(name)){
				return i;
			}
		}
		return -1;
	}

	/* Get the input name at the specified index in the input name list */
	public String getInputName(int index) {
		return inputNames.get(index);
	}

	/* Get the overall list of input names */
	public Vector<String> getInputNames(){
		return inputNames;
	}

	/* Get the number of input ports */
	public int getNoOfInputs(){
		return inputNames.size();
	}

	/* Get the number of output ports */
	public int getNoOfOutputs(){
		return outputNames.size();
	}

	/* Get the number of states */
	public int getNoOfStates(){
		return stateNames.size();
	}

	/* Get the total number of transitions */
	public int getNoOfTransitions(){
		return transitions.size();
	}

	/* Get the index value of a given output name in the overall output name list */
	public int getOutputIndex(String name){
		for(int i=0;i<outputNames.size();i++){
			if(outputNames.get(i).equals(name)){
				return i;
			}
		}
		return -1;
	}

	/* Get the output name at the specified index in the output name list */
	public String getOutputName(int index){
		return outputNames.get(index);
	}

	/* Get the overall list of output names */
	public Vector<String> getOutputNames(){
		return outputNames;
	}

	/* Get the index value of a given state name in the overall state name list */
	public int getStateIndex(String name){
		for(int i=0;i<stateNames.size();i++){
			if(stateNames.get(i).equals(name)){
				return i;
			}
		}
		return -1;
	}

	/* Get the state name at the specified index in the state name list */
	public String getStateName(int index){
		return stateNames.get(index);
	}

	/* Get the overall list of state names */
	public Vector<String> getStateNames() {
		return stateNames;
	}

	/* Get the transition at the specified index in the transition list */
	public SetTransition getTransition(int index) {
		return transitions.get(index);
	}

	/* Get the set of transitions which share a particular source state, given by
	 * its index value in the state name list */
	public Vector<SetTransition> getTransitionsWithSource(int state){
		Vector<SetTransition> transitionsForState = new Vector<SetTransition>();
		for(int i=0;i<transitions.size();i++){
			if(transitions.get(i).getSourceState()==state){
				transitionsForState.add(transitions.get(i));
			}
		}
		return transitionsForState;
	}

	/* Get the set of transitions which share a particular source state and input set, given by
	 * the state name's index value in the state name list, and the set of index values for the
	 * input name list */
	public Vector<SetTransition> getTransitionsWithSourceAndInput(IntSet inputs, int state) {
		Vector<SetTransition> transitionsForState = getTransitionsWithSource(state);
		Vector<SetTransition> transitionsForStateAndInput = new Vector<SetTransition>();
		for(int i=0;i<transitionsForState.size();i++){
			if(transitionsForState.get(i).getInputSet().equals(inputs)){
				transitionsForStateAndInput.add(transitionsForState.get(i));
			}
		}
		return transitionsForStateAndInput;
	}

	/* Get the set of transitions which share a particular target state, given by
	 * its index value in the state name list */
	public Vector<SetTransition> getTransitionsWithTarget(int state){
		Vector<SetTransition> relevantTransitions = new Vector<SetTransition>();
		for(int i=0;i<transitions.size();i++){
			if(transitions.get(i).getResultState()==state){
				relevantTransitions.add(transitions.get(i));
			}
		}
		return relevantTransitions;
	}

	/* Inverts the module by swapping input and output names, and inverting all transitions */
	public void invertModule() {		
		Vector<String> tempSwap = inputNames;
		inputNames=outputNames;
		outputNames=tempSwap;
		for(int i=0;i<transitions.size();i++){
			transitions.get(i).invert();
		}
	}

	/* Retrieves the number of transitions which share a particular state and input set. 
	 * Similar to the getTransitionsWithSourceAndInput method above, but does not actually
	 * retrieve the set of transitions, and is so more efficient for several uses */
	public int occurrences(IntSet set, int state){
		int occurrenceCount=0;
		Vector<SetTransition> transitionsWithState = getTransitionsWithSource(state);
		for(int i=0;i<transitionsWithState.size();i++){
			if(transitionsWithState.get(i).getInputSet().equals(set)){
				occurrenceCount++;
			}
		}
		return occurrenceCount;
	}

	/* Prints the module definition */
	public String printModule(){
		StringBuffer output = new StringBuffer();
		for(int i=0;i<stateNames.size();i++){
			output.append(stateNames.get(i)+" = ");
			Vector<SetTransition> transitionsWithState = getTransitionsWithSource(i);
			for(int j=0;j<transitionsWithState.size();j++){
				output.append(transitionsWithState.get(j).printStringAction());
				if(j<transitionsWithState.size()-1){
					output.append(" + ");
				}
			}
			output.append(";");
			if(i<stateNames.size()-1){
				output.append("\n");
			}
		}
		return output.toString();
	}

	/* Removes the state name at the specified index in the state name list */
	public void removeStateName(int index) {
		stateNames.remove(index);
	}

	/* Removes the transition at the specified index in the transition list */
	public void removeTransition(int index) {
		transitions.remove(index);
	}

	/* Replaces the state name at the specified index in the state name list */
	public void replaceStateName(int state, String replacementStateName) {
		stateNames.set(state, replacementStateName);
	}

	/* Checks whether the given input set is "safely defined" in the given state.
	 * This simply checks if there is a transition in the given state such that the
	 * input set is a superset of the given input set */
	public boolean safelyDefined(IntSet pendingInputs, int state){
		Vector<SetTransition> transitionsByState =getTransitionsWithSource(state);
		for(int i=0;i<transitionsByState.size();i++){
			if(pendingInputs.subset(transitionsByState.get(i).getInputSet())){
				return true;
			}
		}
		return false;
	}

	/* This checks whether the transition list contains the given transition. It helps
	 * with maintaining uniqueness */
	private boolean transitionExists(SetTransition transition){
		Vector<SetTransition> transitionsWithSameState =getTransitionsWithSource(transition.getSourceState());
		for(int i=0;i<transitionsWithSameState.size();i++){
			if(transitionsWithSameState.get(i).sameAs(transition)){
				return true;
			}
		}
		return false;
	}

	/* This checks whether the given input set in the given state, assuming that the given output set
	 * current has signals on them, can result in a violation of safety for the module, or a clash
	 * on any output lines due to any possible autopaths */
	public int violateSafetyOrClash(int state, IntSet inputSet, IntSet currentTravellingOutputs) {
		
		if(!safelyDefined(inputSet,state)){
			return 1;
		}
		
		Vector<Vector<SetTransition>> autoPaths= getAutoFiredPaths(state,inputSet,new Vector<SetTransition>());
		for(int i=0;i<autoPaths.size();i++){
			if(!checkAutoPathSafe(autoPaths.get(i),inputSet)){
				return 1;
			}
			if(checkAutoPathClash(autoPaths.get(i),currentTravellingOutputs)){
				return 2;
			}
		}

		return 0;
	}

}
