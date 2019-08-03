package SequentialMachineStructure;

import CommonStructures.IntSet;

/* Represents a transition in the definition of a sequential machine module. It acts as a combination of the
 * f and g functions into a single T : (Q x I \arrow Q x P[O]), as shown in Chapter 6 of the thesis. It stores the 
 * source and target states of the transition, as well as the input line and output sets of the transition. 
 * States and input names are stored as int values, corresponding to the indexes of the names of the state names 
 * and input names in the overall list of state names and input names respectively. Similarly, output sets are 
 * stored as IntSets,  where every int is the index of the output name in the overall list of output names. */
public class SeqTransition {
	
	/* The integer index of the source state's name in the module's list of state names */
	private int sourceState;
	
	/* The integer index of the input name in the module's list of input names */
	private int input;
		
	/* The integer index of the target state's name in the module's list of state names */
	private  int resultState;
	
	/* The output set, stored via the set of indexes of the output names in the module's output name list*/
	private IntSet outputSet = new IntSet();


	/* The module object that this transition is a part of */
	private NDSequentialMachine seqModule;
	
	/* Constructor for building the transition */
	public SeqTransition(int source, int in, int result, IntSet out, NDSequentialMachine seqDef){
		sourceState=source;
		input=in;
		outputSet=out;
		resultState=result;
		seqModule=seqDef;
	}
	
	/* Returns the int value of the input line */
	public int getInput(){
		return input;
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
	public NDSequentialMachine getSeqMachine(){
		return seqModule;
	}
	
	/* Returns the int value of the source state */
	public int getSourceState(){
		return sourceState;
	}
	
	/* Prints the transition in the form of an action (a,B).q' - using literal
	 * input/output/state names */
	public String printStringAction(boolean setStyle){
		StringBuffer output = new StringBuffer();
		if(setStyle){
			output.append("({"+seqModule.getInputName(input)+"},");
		}
		else{
			output.append("("+seqModule.getInputName(input)+",");
		}
		output.append(outputSet.printStringRepresentation(seqModule.getOutputNames()));
		output.append(").");
		output.append(seqModule.getStateName(resultState));
		return output.toString();
	}

	/* Compares for equality against the passed transition - this helps maintain uniqueness of
	 * actions */
	public boolean sameAs(SeqTransition transition) {
		if(sourceState==transition.getSourceState() &&
				resultState==transition.getResultState() &&
				input==transition.getInput()&&
						outputSet.equals(transition.getOutputSet())){
					return true;
				}
		return false;
	}

	/* Sets the target state to the specified state value */
	public void setTargetState(int i) {
		resultState=i;
	}

	/* Sets the source state to the specified state value */
	public void setSourceState(int i) {
		sourceState=i;
	}
	
}