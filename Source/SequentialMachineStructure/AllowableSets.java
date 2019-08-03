package SequentialMachineStructure;

import java.util.Vector;

import CommonStructures.IntSet;
import CommonStructures.SetSet;

/* Encapsulates an entire A function definition. It contains a list, of sets of integer sets, where an
 * integer corresponds to an input name, and there exists a set of integer sets for each state of the
 * module that this definition belongs to. Each set of integer sets (SetSet) therefore corresponds to a single
 * A(q) for each q. */
public class AllowableSets {

	/* Contains one SetSet (set of integer sets) for each state of the module */
	private Vector<SetSet> setsByState = new Vector<SetSet>();
	
	/* Adds a given concurrent set to the specified state's A(q) set */
	public void addSetToState(IntSet set, int state){
		setsByState.get(state).add(set);
	}
	
	/* Adds a new state's A(q) entry to the definition */
	public void addState(SetSet state){
		setsByState.add(state);
	}
	
	/* Checks whether a given state contains a given concurrent set in its A(q) */
	public boolean contains(int source, IntSet inputSet) {
		for(int i=0;i<setsByState.get(source).size();i++){
			if(inputSet.subset(setsByState.get(source).get(i))){
				return true;
			}
		}
		return false;
	}
	
	/* Returns the set of concurrent sets (i.e. the A(q) entry) for a given state */
	public SetSet getState(int state){
		return setsByState.get(state);
	}
	
	/* Calculates the maximum concurrent set size among all A(q). Hence it finds the cardinality
	 * of the largest IntSet within the list of SetSets */
	public int maximumConcurrentSetSize(){
		int currentMaximumFound=1;
		for(int i=0;i<setsByState.size();i++){
			for(int j=0;j<setsByState.get(i).size();j++){
				if(setsByState.get(i).get(j).size()>currentMaximumFound){
					currentMaximumFound=setsByState.get(i).get(j).size();
				}
			}
		}
		return currentMaximumFound;
	}
	
	/* Prints out the A function definition */
	public String printStringRepresentation(Vector<String> states, Vector<String> inputs){
		StringBuffer output = new StringBuffer();
		for(int i=0;i<setsByState.size();i++){
			output.append(states.get(i)+" = ");
			output.append(setsByState.get(i).printStringRepresentation(inputs));
			output.append(";");
			if(i<setsByState.size()-1){
				output.append("\n");
			}
		}
		return output.toString();
	}
	
	/* Removes a given concurrent set from the given state's A(q) entry */
	public boolean removeSetFromState(IntSet set, int state){
		return setsByState.get(state).remove(set);
	}
	
	/* Removes a given state's A(q) entry */
	public void removeState(int i){
		setsByState.remove(i);
	}

	/* Replaces the A(q) entry for a given state with a given SetSet */
	public void replaceState(int i, SetSet state){
		setsByState.set(i, state);
	}
}
