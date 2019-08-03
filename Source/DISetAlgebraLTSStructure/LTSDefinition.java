package DISetAlgebraLTSStructure;

import java.util.Vector;

import DISetAlgebraStructure.PartiallyVisibleNetwork;

/* Encapsulates an entire LTS. It contains a list of LTS states
 * and also makes special note of ones which have no outgoing transitions 
 * (known as "end" states). Each state object (LTSState instance) contains its own
 * outgoing transitions (with resulting states). This makes traversal very quick and easy. */
public class LTSDefinition {

	/* List of LTS states */
	private Vector<LTSState> allStates = new Vector<LTSState>();
	
	/* List of "end" states (no outgoing transitions) */
	private Vector<LTSState> endStates = new Vector<LTSState>();

	/* Add state to end states */
	public void addEndState(LTSState state) {
		endStates.add(state);
	}
	
	/* Add to list of states */
	public void addState(LTSState state) {
		allStates.add(state);
	}
	
	/* Checks if the given network term has a corresponding state in the LTS */
	public int getIndexIfExists(PartiallyVisibleNetwork state){
		for(int i=0;i<allStates.size();i++){
			if(allStates.get(i).getNetworkTerm().sameAs(state)){
				return i;
			}
		}
		return -1;
	}

	/* Get the state at the given index in the list */
	public LTSState getState(int index){
		return allStates.get(index);
	}
	
	/* Returns the number of states currently in the LTS (which may change
	 * as the LTS is generated - this is not necessarily the "final" value*/
	public int getNoOfStates(){
		return allStates.size();
	}
	
	/* Print the list of states and end states */
	public String printAll(){
		return printStates()+"\n\n\n"+printEndStates();
	}

	/* Print the list of end states */
	public String printEndStates(){
		StringBuffer output = new StringBuffer();
		output.append("End States:\n\n");
		if(endStates.size()==0){
			output.append("Empty: network is non-deadlocking\n");
		}
		for(int i=0;i<endStates.size();i++){
			output.append(endStates.get(i).getStateNo()+": "+endStates.get(i).getNetworkTerm().printNetwork());
			output.append("\n");
		}
		return output.toString();
	}

	/* Print the list of states */
	public String printStates(){
		StringBuffer output = new StringBuffer();
		output.append("States:\n\n");
		for(int i=0;i<allStates.size();i++){
			output.append(i+": "+allStates.get(i).getNetworkTerm().printNetwork());
			output.append("\n");
			for(int j=0;j<allStates.get(i).getNoOfOutgoing();j++){
				output.append("       "+allStates.get(i).getOutgoingTransition(j).printArrow());			
				output.append("  "+allStates.get(i).getOutgoingState(j).getStateNo()+"\n");
			}
		}
		return output.toString();
	}
}
