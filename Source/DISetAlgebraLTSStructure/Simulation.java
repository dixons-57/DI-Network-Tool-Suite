package DISetAlgebraLTSStructure;

import java.util.Vector;

/* Encapsulates a simulation relation. It contains a bunch of SimulationPair instances, representing
 * a state pair. A state pair is two LTS states, one from each of the two LTSs linked via the relation */
public class Simulation {

	/* A list of state pairs which defins the relation */
	private Vector<SimulationPair> statePairs = new Vector<SimulationPair>();

	/* Add a state pair to the list of pairs */
	public void addStatePair(SimulationPair pair) {
		statePairs.add(pair);
	}
	
	/* Checks for duplicates in the list of state pairs */
	public boolean checkDuplicates() {
		for(int i=0;i<statePairs.size()-1;i++){
			SimulationPair firstPair = statePairs.get(i);
			for(int j=i+1;j<statePairs.size();j++){
				SimulationPair secondPair=statePairs.get(j);
				if(firstPair.getLeftState().getStateNo()==secondPair.getLeftState().getStateNo() &&
						firstPair.getRightState().getStateNo()==secondPair.getRightState().getStateNo() ){
					return true;
				}
			}
		}
		return false;
	}

	/* Return the number of state pairs in the relation */
	public int getNoOfPairs(){
		return statePairs.size();
	}

	/* Return the state pair at the given index */
	public SimulationPair getPair(int index){
		return statePairs.get(index);
	}
	
	/* This checks whether a given state pair exists 
	 * in the relation, represented by its two state 
	 * index values from their respective LTSs */
	public boolean pairPresent(int leftStateNo, int rightStateNo){
		for(int i=0;i<statePairs.size();i++){
			if(statePairs.get(i).getLeftState().getStateNo()==leftStateNo &&
					statePairs.get(i).getRightState().getStateNo()==rightStateNo){
				return true;
			}
		}
			return false;
	}
	
	/* Prints the relation */
	public String printDefinition() {
		StringBuffer output = new StringBuffer();
		for(int i=0;i<statePairs.size();i++){
			output.append(statePairs.get(i).printPair()+";\n");
		}
		return output.toString();
	}
	
}
