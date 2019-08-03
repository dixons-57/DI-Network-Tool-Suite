package DISetAlgebraOperations;

import java.util.Vector;

import javax.swing.JOptionPane;

import DISetAlgebraLTSStructure.LTSDefinition;
import DISetAlgebraLTSStructure.LTSState;
import DISetAlgebraLTSStructure.Transition;
import DISetAlgebraStructure.PartiallyVisibleNetwork;
import GUI.ConsoleWindow;

/* This generates an LTS starting from a given network term, as shown in the DI-Set Algebra tab's LTS screen.*/
public class LTSGeneration {

	/* Computes the LTS of the given network term, with infinite growth detection */
	public static LTSDefinition computeLTS(PartiallyVisibleNetwork networkDefinition, boolean infiniteDetection) throws Exception{
		LTSDefinition definition = new LTSDefinition();
		LTSState startingState= new LTSState();
		startingState.setNetworkTerm(networkDefinition);
		startingState.setStateNo(0);
		definition.addState(startingState);
		if(infiniteDetection){
			ConsoleWindow.output("Infinite growth detection enabled. Each state will be checked against existing states before being added");	
		}
		else{
			ConsoleWindow.output("Infinite growth detection NOT enabled. System will become unresponsive if infinite growth is present");
		}

		ConsoleWindow.output("Added starting state 0: "+startingState.getNetworkTerm().printNetworkWithoutName());
		ConsoleWindow.output("Beginning recursion from state 0");

		recurseLTS(definition,startingState,infiniteDetection);
		return definition;
	}

	/* The recursive function for generating the LTS from a given state. It is called from each state which is created and only called if the state 
	 * has just been created/is new, preventing infinite recursion */
	public static void recurseLTS(LTSDefinition definition, LTSState currentState, boolean infiniteCheck) throws Exception{

		ConsoleWindow.output("Calculating outgoing transitions from state "+currentState.getStateNo());

		/* Counts no. of transitions from currentState and adds currentState to end state if none are available. */
		Vector<Transition> transitionsFromThisNetworkState = CalculateTransitions.calculateTransitions(currentState.getNetworkTerm());
		if(transitionsFromThisNetworkState.size()==0){
			ConsoleWindow.output("No outgoing transitions from this state. State "+currentState.getStateNo()+" added to list of end states");
			definition.addEndState(currentState);
		}
		else{
			ConsoleWindow.output(transitionsFromThisNetworkState.size()+" transitions in total exist from this state: "+currentState.getStateNo());

			/* Iterates through any transitions from currentState */
			for(int i=0;i<transitionsFromThisNetworkState.size();i++){

				/* Checks to see if the target state of the transition exists in the LTS already */
				Transition currentTransition=transitionsFromThisNetworkState.get(i);

				ConsoleWindow.output("Building outgoing transition "+currentTransition.printArrow()+" from state "+currentState.getStateNo());
				
				int index = definition.getIndexIfExists(currentTransition.getTargetStateTerm());
				currentState.addOutgoingTransition(currentTransition);

				/* If the target state of the transition does not exist in the LTS */
				if(index==-1){

					/* Create a fresh LTS state and add it to the master list of states */
					LTSState freshState = new LTSState();
					freshState.setNetworkTerm(currentTransition.getTargetStateTerm());
					freshState.setStateNo(definition.getNoOfStates());
					definition.addState(freshState);

					/* Link the currentState to it */
					currentState.addOutgoingState(freshState);
					
					ConsoleWindow.output("Target state for this transition does not exist in LTS. Creating new node "+
							freshState.getStateNo()+": "+freshState.getNetworkTerm().printNetworkWithoutName());

					/* If infinite growth detection is enabled */
					if(infiniteCheck){	

						ConsoleWindow.output("Performing infinite growth check on new state");

						/* Iterate through all existing states */
						for(int k=0;k<definition.getNoOfStates()-1;k++){

							/* If the new state is a "super state" of the current existing state */
							LTSState stateToCompare = definition.getState(k);

							/* If the new state is a "super state" of said existing state */
							if(isSuperState(freshState,stateToCompare)){
								ConsoleWindow.output("New state is a \"super state\" of the existing state "+
										stateToCompare.getStateNo()+": "+stateToCompare.getNetworkTerm().printNetworkWithoutName()+
										" (i.e. the new state contains identical states for all modules, and the new state's bus contents is a strict superset)");

								/* Check if there is also a path of transitions from the existing state to the new state */
								boolean infiniteBehaviour =pathFromStateToState(stateToCompare, freshState, new Vector<Integer>());
								
								/* If so then the LTS can grow infinitely and we stop! */
								if(infiniteBehaviour){
									ConsoleWindow.output("A path also exists from the existing state to the new state, so this definitely represents infinite growth");
									JOptionPane.showMessageDialog(null, "Infinite number of states detected. LTS generation halted. Check console for more details");
									throw new Exception();
								}
								else{
									ConsoleWindow.output("A path does not exist from the existing state to the new state however, so this does not represent infinite growth");
								}
							}
						}

						ConsoleWindow.output("No infinite growth behaviour so far");
					}

					ConsoleWindow.output("Recursing to state "+freshState.getStateNo()+": "+freshState.getNetworkTerm().printNetworkWithoutName());

					/* Recurse through the new state and continue generating the LTS */
					recurseLTS(definition,freshState,infiniteCheck);

					ConsoleWindow.output("Leaving recursion and returning to examining of state "+currentState.getStateNo());
				}

				/* If the target state does exist already, simply link the currentState to it via its target state list */
				else{
					ConsoleWindow.output("Resulting state for this transition already exists in LTS. State is "+
							definition.getState(index).getStateNo()+": "+definition.getState(index).getNetworkTerm().printNetworkWithoutName());

					currentState.addOutgoingState(definition.getState(index));
				}
			}

			ConsoleWindow.output("No more outgoing transitions to process from state "+currentState.getStateNo());
		}
	}
	
	/* Checks if a given state2 in the LTS has an identical network state
	 * to a given state1, except that state1's bus is a strict superset of state2's bus */
	public static boolean isSuperState(LTSState state1, LTSState state2){
		if(state1.getNetworkTerm().isSuperNetwork(state2.getNetworkTerm())){
			return true;
		}
		return false;
	}

	/* Checks if there is a path of transitions in the LTS from startState to endState, 
	 * by utilising recursion and using visitedStates to make sure that the same state is not checked twice. */
	public static boolean pathFromStateToState(LTSState startState, LTSState endState, Vector<Integer> visitedStates){

		/* If the state identifiers are the same, then we have
		 * reached the target state and there is definitely a path */
		if(startState.getStateNo()==endState.getStateNo()){
			return true;
		}

		/* Otherwise */

		/* startState has now been visited */
		visitedStates.addElement(startState.getStateNo());

		/* For every state immediately reachable by startState */
		for(int i=0;i<startState.getNoOfOutgoing();i++){

			/* Retrieve the state */
			LTSState target = startState.getOutgoingState(i);

			/* If it hasn't been visited yet */
			if(!visited(target.getStateNo(), visitedStates)){

				/* If there is a path from there to endState, then there
				 * is a path from here to the endState, and we can return true */
				if(pathFromStateToState(target,endState,visitedStates)){
					return true;
				}
			}

		}

		/* If we reach this point then there is not a path from startState to endState */
		return false;

	}

	/* Checks whether a given state has been visited by checking the overall list of visited states */
	public static boolean visited(int stateToCheck, Vector<Integer> visitedStates){
		for(int i=0;i<visitedStates.size();i++){
			if(visitedStates.get(i).intValue()==stateToCheck){
				return true;
			}
		}
		return false;
	}
}
