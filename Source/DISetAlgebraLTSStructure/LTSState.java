package DISetAlgebraLTSStructure;

import java.util.Vector;

import DISetAlgebraStructure.PartiallyVisibleNetwork;

/* Represents a state in the LTS. This contains a list of outgoing transitions
 * (with resulting states). This makes traversal very quick and easy. */
public class LTSState {

	/* The network term in this state of the LTS */
	private PartiallyVisibleNetwork networkTerm;
	
	/* Unique index in the list of states for the overall LTS */
	private int stateNo;
	
	/* List of outgoing transitions from this state */
	private Vector<Transition> outgoingTransitions = new Vector<Transition>();
	
	/* List of resulting states for the outgoing transitions. in one-to-one correspondence
	 * with the above list */
	private Vector<LTSState> outgoingStates = new Vector<LTSState>();
	
	/* Add state to the list of outgoing states */
	public void addOutgoingState(LTSState state) {
		outgoingStates.add(state);
	}
	
	/* Add transition to the list of outgoing transitions */
	public void addOutgoingTransition(Transition transition) {
		outgoingTransitions.add(transition);
	}
	
	/* Get the network term in this state of the LTS */
	public PartiallyVisibleNetwork getNetworkTerm(){
		return networkTerm;
	}
	
	/* Get the unique index of this state in the overall LTS list of states*/
	public int getStateNo(){
		return stateNo;
	}
	
	/* Get the number of outgoing transitions */
	public int getNoOfOutgoing(){
		return outgoingTransitions.size();
	}

	/* Get the resulting/outgoing state for the given index in the list */
	public LTSState getOutgoingState(int index){
		return outgoingStates.get(index);
	}

	/* Get the resulting/outgoing transition for the given index in the list */
	public Transition getOutgoingTransition(int index){
		return outgoingTransitions.get(index);
	}

	/* Set the network term in this state of the LTS */
	public void setNetworkTerm(PartiallyVisibleNetwork term) {
		networkTerm=term;
	}

	/* Set the unique index of this state in the overall LTS list of states*/
	public void setStateNo(int index) {
		stateNo=index;
	}
}
