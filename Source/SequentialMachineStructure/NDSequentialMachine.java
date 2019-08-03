package SequentialMachineStructure;

import java.util.Vector;
import CommonStructures.SetSet;

/* Encapsulates an entire (ND) sequential machine module definition. It contains lists of input, output
 * and state names, and a list of transitions. The transitions themselves utilise int values,
 * which refer to the input, output and state names via their indexes in these lists. The A function
 * is modelled by an instance of AllowableSets*/
public class NDSequentialMachine {
	
	/* The A function for this module */
	private AllowableSets AFunction = new AllowableSets();
	
	/* The list of input names */
	private Vector<String> inputNames = new Vector<String>();
	
	/* The list of output names */
	private Vector<String> outputNames = new Vector<String>();
	
	/* The list of state names */
	private Vector<String> stateNames = new Vector<String>();
	
	/* The list of transitions */
	private Vector<SeqTransition> transitions = new Vector<SeqTransition>();
	
	/* Adds an input name to the list if it is not already present */
	public boolean addInputName(String name){
		for(int i=0;i<inputNames.size();i++){
			if(inputNames.get(i).equals(name)){
				return false;
			}
		}
		inputNames.add(name);
		return true;
	}

	/* Adds an output name to the list if it is not already present */
	public boolean addOutputName(String name){
		for(int i=0;i<outputNames.size();i++){
			if(outputNames.get(i).equals(name)){
				return false;
			}
		}
		outputNames.add(name);
		return true;
	}
	
	/* Adds a state name to the list if it is not already present */
	public boolean addStateName(String name){
		for(int i=0;i<stateNames.size();i++){
			if(stateNames.get(i).equals(name)){
				return false;
			}
		}
		stateNames.add(name);
		AFunction.addState(new SetSet());
		return true;
	}
	
	/* Adds a transition to the list if it is not already present */
	public boolean addTransition(SeqTransition transition){
		if(!transitionExists(transition)){
			transitions.add(transition);
			return true;
		}
		return false;
	}
	
	/* Simply checks whether two states contain identical sets of transitions */
	public boolean duplicateStates(){
		for(int i=0;i<stateNames.size()-1;i++){
			Vector<SeqTransition> firstStateTransitions = getTransitionsWithSource(i);
			for(int j=i+1;j<stateNames.size();j++){
				Vector<SeqTransition> secondStateTransitions = getTransitionsWithSource(j);
				if(firstStateTransitions.size()==secondStateTransitions.size()){
					boolean same=true;
					for(int k=0;k<firstStateTransitions.size();k++){	
						SeqTransition currentTransition=firstStateTransitions.get(k);
						boolean inSecondSet=false;
						for(int l=0;l<secondStateTransitions.size();l++){
							if(currentTransition.sameAs(secondStateTransitions.get(l))){
								inSecondSet=true;
								break;
							}
						}
						if(!inSecondSet){
							same=false;
							break;
						}
						
					}
					if(same){
						return true;
					}	
				}		
			}
		}
		return false;
	}
	
	/* Checks whether there are any duplicate entries of transitions (recall that
	 * a transition is a 4-tuple (q,a,B,q'), so it just checks whether all components
	 * are equal) */
	public boolean duplicateTransitions(){
		for(int i=0;i<transitions.size()-1;i++){
			for(int j=i+1;j<transitions.size();j++){
				if(transitions.get(i).sameAs(transitions.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	/* Returns the A function for the module */
	public AllowableSets getAFunction(){
		return AFunction;
	}
	
	/* Get the index value of a given input name in the overall input name list */
	public int getInputIndex(String inputName){
		for(int i=0;i<inputNames.size();i++){
			if(inputNames.get(i).equals(inputName)){
				return i;
			}
		}
		return -1;
	}
	
	/* Get the input name at the specified index in the input name list */
	public String getInputName(int index){
		return inputNames.get(index);
	}
	
	/* Get the overall list of input names */
	public Vector<String> getInputNames(){
		return inputNames;
	}
	
	/* Get the number of input ports */
	public int getNoOfInputs(){
		return inputNames.size();
	}

	/* Get the number of output ports */
	public int getNoOfOutputs(){
		return outputNames.size();
	}

	/* Get the number of states */
	public int getNoOfStates(){
		return stateNames.size();
	}

	/* Get the total number of transitions */
	public int getNoOfTransitions(){
		return transitions.size();
	}
	
	/* Get the index value of a given output name in the overall output name list */
	public int getOutputIndex(String name){
		for(int i=0;i<outputNames.size();i++){
			if(outputNames.get(i).equals(name)){
				return i;
			}
		}
		return -1;
	}

	/* Get the output name at the specified index in the output name list */
	public String getOutputName(int index){
		return outputNames.get(index);
	}

	/* Get the overall list of output names */
	public Vector<String> getOutputNames(){
		return outputNames;
	}

	/* Get the index value of a given state name in the overall state name list */
	public int getStateIndex(String name){
		for(int i=0;i<stateNames.size();i++){
			if(stateNames.get(i).equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	/* Get the state name at the specified index in the state name list */
	public String getStateName(int index){
		return stateNames.get(index);
	}

	/* Get the overall list of state names */
	public Vector<String> getStateNames() {
		return stateNames;
	}

	/* Get the transition at the specified index in the transition list */
	public SeqTransition getTransition(int index) {
		return transitions.get(index);
	}
	
	/* Get the set of transitions which share a particular source state, given by
	 * its index value in the state name list */
	public Vector<SeqTransition> getTransitionsWithSource(int state){
		Vector<SeqTransition> relevantTransitions = new Vector<SeqTransition>();
		for(int i=0;i<transitions.size();i++){
			if(transitions.get(i).getSourceState()==state){
				relevantTransitions.add(transitions.get(i));
			}
		}
		return relevantTransitions;
	}
	
	/* Get the set of transitions which share a particular source state and input line, given by
	 * the state name's index value in the state name list, and the input name's index values in the
	 * input name list */
	public Vector<SeqTransition> getTransitionsWithSourceAndInput(int input, int state) {
		Vector<SeqTransition> transitionsWithState = getTransitionsWithSource(state);
		Vector<SeqTransition> relevantTransitions = new Vector<SeqTransition>();
		for(int i=0;i<transitionsWithState.size();i++){
			if(transitionsWithState.get(i).getInput()==input){
				relevantTransitions.add(transitionsWithState.get(i));
			}
		}
		return relevantTransitions;
	}

	/* Get the set of transitions which share a particular target state, given by
	 * its index value in the state name list */
	public Vector<SeqTransition> getTransitionsWithTarget(int state){
		Vector<SeqTransition> relevantTransitions = new Vector<SeqTransition>();
		for(int i=0;i<transitions.size();i++){
			if(transitions.get(i).getResultState()==state){
				relevantTransitions.add(transitions.get(i));
			}
		}
		return relevantTransitions;
	}

	/* Determines the largest entry in all A(q) of the A function */
	public int maximumConcurrentSetSize(){
		int currentMaximumSize=1;
		for(int i=0;i<stateNames.size();i++){
			for(int j=0;j<AFunction.getState(i).size();j++){
				if(AFunction.getState(i).get(j).size()>currentMaximumSize){
					currentMaximumSize=AFunction.getState(i).get(j).size();
				}
			}
		}
		return currentMaximumSize;
	}
	
	/* Prints the A function definition */
	public String printAFunction() {
		return AFunction.printStringRepresentation(stateNames, inputNames);
	}
	/* Prints the main definition */
	public String printMainOnly(boolean setForm) {
		StringBuffer output = new StringBuffer();
		for(int i=0;i<stateNames.size();i++){
			output.append(stateNames.get(i)+" = ");
			Vector<SeqTransition> transitionsWithState = getTransitionsWithSource(i);
			for(int j=0;j<transitionsWithState.size();j++){
				output.append(transitionsWithState.get(j).printStringAction(setForm));
				if(j<transitionsWithState.size()-1){
					output.append(" + ");
				}
			}
			output.append(";");
			if(i<stateNames.size()-1){
				output.append("\n");
			}
		}
		return output.toString();
	}

	/* Prints the main definition AND the A function definition */
	public String printMainAndFunction(){
		StringBuffer output = new StringBuffer();
		output.append(printMainOnly(false)+"\n");
		output.append("A Function:\n"+printAFunction());
		return output.toString();
	}
	
	/* Removes the state name at the specified index in the state name list */
	public void removeStateName(int index) {
		stateNames.remove(index);
	}

	/* Removes the transition at the specified index in the transition list */
	public void removeTransition(int index) {
		transitions.remove(index);
	}

	/* Replaces the state name at the specified index in the state name list */
	public void replaceStateName(int state, String replacementStateName) {
		stateNames.set(state, replacementStateName);
	}

	/* This checks whether the transition list contains the given transition. It helps
	 * with maintaining uniqueness */
	private boolean transitionExists(SeqTransition transition){
		for(int i=0;i<transitions.size();i++){
			if(transitions.get(i).sameAs(transition)){
				return true;
			}
		}
		return false;
	}

	/* Removes the state entry in the A function at the specified index */
	public void removeAFunctionEntry(int i) {
		AFunction.removeState(i);
	}
	
}
