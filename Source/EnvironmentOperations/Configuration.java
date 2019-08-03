package EnvironmentOperations;

import CommonStructures.IntSet;
import SetNotationStructure.SetNotationModule;

/* Corresponds to a "limited" configuration from the Environment chapter of the thesis. 
 * It contains a module state, as well as the set of input and output lines which contain signals */
public class Configuration {

	/* The set of module input lines which contains signals */
	private IntSet pendingSignals;
	
	/* The set of module output lines which contains signals */
	private IntSet travellingOutputs;
	
	/* The state of the module */
	private int state;
	
	/* Performs a deep copy of this object */
	public Configuration deepCopy() {
		Configuration copy = new Configuration();
		copy.state=state;
		copy.pendingSignals=pendingSignals.deepCopy();
		copy.travellingOutputs=travellingOutputs.deepCopy();
		return copy;
	}
	
	/* Checks for equality against another configuration by simply comparing all
	 * components */
	public boolean equals(Configuration compare){
		if(state==compare.state &&
				pendingSignals.equals(compare.pendingSignals) &&
				travellingOutputs.equals(compare.travellingOutputs)){
			return true;
		}
		else{
			return false;
		}
	}

	/* Retrieve the set of input lines which contain signals */
	public IntSet getPendingSignals(){
		return pendingSignals;
	}
	
	/* Retrieves the state of the module */
	public int getState() {
		return state;
	}
	
	/* Retrieves the set of output lines which contain signals */
	public IntSet getTravellingOutputs(){
		return travellingOutputs;
	}
	
	/* Prints the configuration */
	public String print(SetNotationModule moduleDefinition){
		return "{"+moduleDefinition.getStateName(state)+";"+pendingSignals.printStringRepresentation(moduleDefinition.getInputNames())+
				";"+travellingOutputs.printStringRepresentation(moduleDefinition.getOutputNames())+"}";
	}
	
	/* Sets the set of input lines which contains signals to the
	 * given set */
	public void setPendingSignals(IntSet signals){
		pendingSignals=signals;
	}
	
	/* Sets the state of the module to the given state index */
	public void setState(int index){
		state=index;
	}

	/* Sets the set of output lines which contains signals to the
	 * given set */
	public void setTravellingOutputs(IntSet signals){
		travellingOutputs=signals;
	}
	
}
