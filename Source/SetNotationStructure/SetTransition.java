package SetNotationStructure;

import CommonStructures.IntSet;

/* Represents a transition in the definition of a Set Notation module. It stores the 
 * source and target states of the transition, as well as the input and output sets of the transition. 
 * States are stored as int values, corresponding to the indexes of the names of the state names in the
 * overall list of state names. Similarly, input and output sets are stored as IntSets, 
 * where every int is the index of the input or output names in the overall list of input or output names. */
public class SetTransition {
	
	/* The integer index of the source state's name in the module's list of state names */
	private int sourceState;
	
	/* The input set, stored via the set of indexes of the input names in the module's input name list*/
	private IntSet inputSet;

	/* The integer index of the target state's name in the module's list of state names */
	private int resultState;
	
	/* The output set, stored via the set of indexes of the output names in the module's output name list*/
	private IntSet outputSet;
	
	/* The module object that this transition is a part of */
	private SetNotationModule setModule;
	
	/* Constructor for building the transition */
	public SetTransition(int source, IntSet in,  int result, IntSet out, SetNotationModule set){
		sourceState=source;
		inputSet=in;
		outputSet=out;
		resultState=result;
		setModule=set;
	}
	
	/* Returns the transition's input set */
	public IntSet getInputSet(){
		return inputSet;
	}
	
	/* Returns the transition's output set */
	public IntSet getOutputSet(){
		return outputSet;
	}
	
	/* Returns the int value of the result state */
	public int getResultState(){
		return resultState;
	}
	
	/* Returns the module object that this transition is a part of */
	public SetNotationModule getSetModule(){
		return setModule;
	}
	
	/* Returns the int value of the source state */
	public int getSourceState(){
		return sourceState;
	}
	
	/* Inverts the transition - basically swaps the source/target states,
	 * and input/output sets */
	public void invert() {
		int tempSwap = sourceState;
		sourceState= resultState;
		resultState=tempSwap;
		IntSet tempSetSwap= inputSet;
		inputSet=outputSet;
		outputSet=tempSetSwap;
	}
	
	/* Prints the transition in the form of an action (A,B).q' - using literal
	 * input/output/state names */
	public String printStringAction(){
		StringBuffer output = new StringBuffer();
		output.append("("+inputSet.printStringRepresentation(setModule.getInputNames())+",");
		output.append(outputSet.printStringRepresentation(setModule.getOutputNames()));
		output.append(").");
		output.append(setModule.getStateName(resultState));
		return output.toString();
	}
	
	/* Sets the target state to the specified state value */
	public void setTargetState(int i) {
		resultState=i;
	}

	/* Sets the source state to the specified state value */
	public void setSourceState(int i) {
		sourceState=i;
	}

	/* Compares for equality against the passed transition - this helps maintain uniqueness of
	 * actions */
	public boolean sameAs(SetTransition transition) {
		if(sourceState==transition.getSourceState() &&
				resultState==transition.getResultState() &&
				inputSet.equals(transition.getInputSet())&&
						outputSet.equals(transition.getOutputSet())){
					return true;
				}
		return false;
	}
	
}