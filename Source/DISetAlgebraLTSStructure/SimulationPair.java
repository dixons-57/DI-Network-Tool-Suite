package DISetAlgebraLTSStructure;

/* Represents a state pair.  It contains two LTS states, 
 * one from each of the two LTSs linked via the simulation relation  */
public class SimulationPair {

	/* LTS State from the left network (Network 1 in the GUI) */
	private LTSState leftState;
	
	/* LTS State from the right network (Network 2 in the GUI) */
	private LTSState rightState;
	
	/* Constructs the state pair object from the two LTS states */
	public SimulationPair(LTSState left, LTSState right) {
		leftState=left;
		rightState=right;
	}

	/* Returns the state for the left network (Network 1 in the GUI) */
	public LTSState getLeftState(){
		return leftState;
	}
	
	/* Returns the state for the right network (Network 2 in the GUI) */
	public LTSState getRightState(){
		return rightState;
	}
	
	/* Prints the pair */
	public String printPair() {
		return "("+leftState.getStateNo() + "," + rightState.getStateNo()+")";
	}
	
}
