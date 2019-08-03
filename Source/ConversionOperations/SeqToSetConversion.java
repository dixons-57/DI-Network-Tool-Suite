package ConversionOperations;

import java.util.Vector;

import CommonStructures.IntSet;
import CommonStructures.SetSet;
import GUI.ConsoleWindow;
import GUI.ConversionTab;
import SequentialMachineStructure.NDSequentialMachine;
import SequentialMachineStructure.SeqTransition;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Includes the algorithm for converting a (ND) sequential machine definition to a Set Notation module definition according to the
 * algorithm given in the thesis. It is used by the Conversion tab. Note that this algorithm does not check any of the conditions required
 * for the (ND) sequential machine to be valid for conversion. It simply applies the algorithm under the assumption that there are no
 * problems with the input module */
public class SeqToSetConversion {

	/* 1D matrix for checking which states are reachable from the (implicit) initial state in the resulting sequential machine.
	 * It is used to remove "unreachable" states after the main algorithm has finished */
	private static boolean[] stateReachability;	

	/* Converts the given (ND) sequential machine to a Set Notation module according to the algorithm given in the thesis.
	 * It follows the algorithm very closely. See ConsoleWindow.output calls below for more information as to what operations are
	 * performed at each stage */
	public static SetNotationModule convertToSet(NDSequentialMachine sequentialDefinition){

		SetNotationModule builtSetDefinition = new SetNotationModule();

		ConsoleWindow.output("Adding list of state names, input names, and output names to Set Notation module");
		
		for(int i=0;i<sequentialDefinition.getNoOfStates();i++){
			builtSetDefinition.addStateName(sequentialDefinition.getStateName(i));
		}
		for(int i=0;i<sequentialDefinition.getNoOfInputs();i++){
			builtSetDefinition.addInputName(sequentialDefinition.getInputName(i));
		}
		for(int i=0;i<sequentialDefinition.getNoOfOutputs();i++){
			builtSetDefinition.addOutputName(sequentialDefinition.getOutputName(i));
		}
		
		ConsoleWindow.output("Adding initial list of (ND) sequential machine transitions to Set Notation module. These will be built upon"
				+ " and possibly eventually deleted by the algorithm");
		for(int i=0;i<sequentialDefinition.getNoOfTransitions();i++){
			SeqTransition seqTransition = sequentialDefinition.getTransition(i);
			builtSetDefinition.addTransition(new SetTransition(seqTransition.getSourceState(),
					new IntSet(seqTransition.getInput()),seqTransition.getResultState(),seqTransition.getOutputSet(),builtSetDefinition));
		}

		/* Determine largest "A" set */
		int biggestASet = sequentialDefinition.maximumConcurrentSetSize();

		ConsoleWindow.output("Largest A set is size "+biggestASet+
				" so we progressively build up actions with increasing input set size until reaching input size "+biggestASet);
		
		/* Build up actions in increasing size */
		for(int i=2;i<=biggestASet;i++){
			buildActionsOfSizeX(i,builtSetDefinition,sequentialDefinition);			
		}

		ConsoleWindow.output("Removing actions with null outputs");
		removeNullOutputActions(builtSetDefinition);
	
		if(ConversionTab.instance.StateRemoval.isSelected()){
			
			ConsoleWindow.output("Removing states unreachable from top-most state");
			removeUnreachableStates(builtSetDefinition);
		}
		return builtSetDefinition;
	}
	
	/* Builds up actions of size x, by retrieving A function entires that are x or larger */
	public static void buildActionsOfSizeX(int x, SetNotationModule builtSetDefinition, NDSequentialMachine sequentialDefinition){
		
		ConsoleWindow.output("Building actions with input set size "+x);
		
		for(int i=0;i<sequentialDefinition.getNoOfStates();i++){
			
			/* For every allowable set */
			for(int j=0; j<sequentialDefinition.getAFunction().getState(i).size();j++){

				/* If the set is greater than or equal to the currently valid size (x) */
				if (sequentialDefinition.getAFunction().getState(i).get(j).size()>=x){

					/* Build possible actions from it of size X*/
					buildActionsOfSizeXFromSet(sequentialDefinition.getAFunction().getState(i).get(j),i,x,builtSetDefinition);
				}
			}
		}
	}

	/* Builds up actions of size x for a given state, from a given A function entry that is size x or larger */
	private static void buildActionsOfSizeXFromSet(IntSet set, int state, int x, SetNotationModule builtSetDefinition) {

		ConsoleWindow.output("Building actions in state "+builtSetDefinition.getStateName(state)+" of input set size "+x+" from concurrent set "
			+set.printStringRepresentation(builtSetDefinition.getInputNames()));
		
		/* e.g. say we are building action of input size 3 and we are given the A function entry {a,b,c,d,e} as it is bigger than 3
		* we first retrieve all subsets of size 2 or less, {a}, {b} {c} {d} {e} {a,b} {a,c}... , so that we can
		* combined already "implemented" actions corresponding to subsets of 2 or less in order to build an action of size 3 */

		SetSet relevantSubsets = subsetsOfSizeXorLess(set,x-1);

		/* Now build actions resulting from all pairs of subsets A, B such that A u B is cardinality 3
		* and A and B are distinct */

		/* For every possible first subset */
		for(int i=0;i<relevantSubsets.size();i++){

			/* For every possible second subset */
			for(int j=0;j<relevantSubsets.size();j++){

				/* If the sets are different */
				if(i!=j){

					/* If the sum of the two sets cardinality is equal to size x */
					if(relevantSubsets.get(i).size()+relevantSubsets.get(j).size()==x){

						/* And the sets are distinct */
						if(relevantSubsets.get(i).setDifference(relevantSubsets.get(j)).equals(relevantSubsets.get(i))){
							
							ConsoleWindow.output("Building actions corresponding to processing "+
									relevantSubsets.get(i).printStringRepresentation(builtSetDefinition.getInputNames())+
									" and "+relevantSubsets.get(j).printStringRepresentation(builtSetDefinition.getInputNames()) +" in either order");
							
							/* Build transitions resulting from applying both input sets in either order */
							makeAction(relevantSubsets.get(i),relevantSubsets.get(j),state,builtSetDefinition);
							makeAction(relevantSubsets.get(j),relevantSubsets.get(i),state,builtSetDefinition);
						}
					}
				}
			}
		}
	}
	
	/* Retrieves subsets of the given set, of size x or less. It utilises the powerset method of IntSets and then
	 * simply strips out sets that are too large */
	public static SetSet subsetsOfSizeXorLess(IntSet set, int x){
		SetSet relevantSubsets = set.powerSet();

		for(int i=relevantSubsets.size()-1;i>=0;i--){
			if(relevantSubsets.get(i).size()>x){
				relevantSubsets.remove(i);
			}
		}
		return relevantSubsets;
	}

	/* Builds all possible actions resulting from applying inputSet1 in initialState, followed by inputSet2 in the target state
	 * of the effects of inputSet1 */
	private static void makeAction(IntSet inputSet1, IntSet inputSet2, int initialState, SetNotationModule builtSetDefinition){

		/* Build new input set which is the combination of both sets */
		IntSet combinedInput = inputSet1.setUnion(inputSet2);
		
		/* Retrieve all actions corresonding to inputSet1 in initialState */
		Vector<SetTransition> inputSet1Transitions = builtSetDefinition.getTransitionsWithSourceAndInput(inputSet1,initialState);
		
		/* For every "first" action */
		for(int x=0;x<inputSet1Transitions.size();x++){
			
			int inputSet1ResultState=inputSet1Transitions.get(x).getResultState();
			IntSet inputSet1Output = inputSet1Transitions.get(x).getOutputSet();
			Vector<SetTransition> inputSet2Transitions = builtSetDefinition.getTransitionsWithSourceAndInput(inputSet2,inputSet1ResultState);
			
			/* Find all actions in the target state of this action that correspond to inputSet2 */
			for(int y=0;y<inputSet2Transitions.size();y++){

				/* Retrieve each "second" action */
				int inputSet2ResultState=inputSet2Transitions.get(y).getResultState();
				IntSet inputSet2Output = inputSet2Transitions.get(y).getOutputSet();

				/* Build new output set which is a combination of the "first" and "second" actions */
				IntSet concurrentOutput = inputSet1Output.setUnion(inputSet2Output);
				
				/* Add the action to initialState, where the input set is the combination of inputSet1 and inputSet2, and the output set is the combination
				 * of the output sets of the "first" and "second" actions, and the target state is the target state of the "second" action */
				builtSetDefinition.addTransition(new SetTransition(initialState,combinedInput,inputSet2ResultState,concurrentOutput,builtSetDefinition));
				
				ConsoleWindow.output("Input "+inputSet1.printStringRepresentation(builtSetDefinition.getInputNames())+" followed by input "+
						inputSet2.printStringRepresentation(builtSetDefinition.getInputNames())+" can lead to output "+
						concurrentOutput.printStringRepresentation(builtSetDefinition.getOutputNames())+" and resulting state "+
						builtSetDefinition.getStateName(inputSet2ResultState));
			}
		}
	}

	/* Remove null actions from definitions. If actions are then empty, delete them (their functionality
	* is provided by new actions produced by the conversion algorithm)*/
	private static void removeNullOutputActions(SetNotationModule builtSetDefinition) {
		for(int i=builtSetDefinition.getNoOfTransitions()-1;i>=0;i--){
			SetTransition currentTransition = builtSetDefinition.getTransition(i);
			if(currentTransition.getOutputSet().size()==0){
				builtSetDefinition.removeTransition(i);
			}
		}
	}

	/* Removes any states which are unreachable from the "top-most" state in the definition as it appears on screen.
	 * This is optionally turned off as it is not always appropriate (explained in the thesis) */
	private static void removeUnreachableStates(SetNotationModule builtSetDefinition) {
		stateReachability = new boolean[builtSetDefinition.getNoOfStates()];
		stateReachability[0]=true;
		
		/* Assume all states are not reachable */
		for(int i=1;i<stateReachability.length;i++){
			stateReachability[i]=false;
		}
		
		/* Recursively calculate reachables */
		calculateReachableFromState(0,builtSetDefinition);

		for(int j=stateReachability.length-1;j>=0;j--){

			/* If a state is not reachable then delete all its associated data (name, outgoing transitions) */
			if(!stateReachability[j]){
				ConsoleWindow.output("State "+builtSetDefinition.getStateName(j)+" is unreachable from the top-most state so removing");
				builtSetDefinition.removeStateName(j);
				for(int k=builtSetDefinition.getNoOfTransitions()-1;k>=0;k--){
					if(builtSetDefinition.getTransition(k).getSourceState()==j){
						builtSetDefinition.removeTransition(k);
					}
					else if(builtSetDefinition.getTransition(k).getSourceState()>j){
						builtSetDefinition.getTransition(k).setSourceState(builtSetDefinition.getTransition(k).getSourceState()-1);
					}
					else if(builtSetDefinition.getTransition(k).getResultState()>j){
						builtSetDefinition.getTransition(k).setTargetState(builtSetDefinition.getTransition(k).getResultState()-1);
					}
				}
			}
		}
	}
	
	/* Recursively calculates which states are reachable from currentState, by changing the truth value in the
	 * stateReachability matrix of a particular state once it is reached by the algorithm. If the value is changed to true,
	 * recursion continues from that state, otherwise it moves back up the stack */
	private static void calculateReachableFromState(int currentState,SetNotationModule builtSetDefinition) {
		Vector<SetTransition> transitionsFromState = builtSetDefinition.getTransitionsWithSource(currentState);
		for(int i=0;i<transitionsFromState.size();i++){
			SetTransition currentAction =transitionsFromState.get(i);
			int resultState=currentAction.getResultState();
			if(stateReachability[resultState]==false){
				
				ConsoleWindow.output("State "+builtSetDefinition.getStateName(resultState)+" is reachable from the top-most state");
				
				stateReachability[resultState]=true;
				calculateReachableFromState(resultState,builtSetDefinition);
			}
		}
	}
}
