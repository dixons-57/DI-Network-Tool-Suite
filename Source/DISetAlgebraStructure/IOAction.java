package DISetAlgebraStructure;

/* An action in DI-Set algebra. It corresponds to type action in the BNF in the thesis */
public class IOAction {

	/* The set of input ports of the action */
	private PortSet inputSet = new PortSet();
	
	/* This records whether an action is in an intermediate state, represented
	 * by the bullet in the DI-Set algebra. This occurs after the input set has
	 * been discarded by an input transition, but the output set has not yet been produced. */
	private boolean intermediate = false;
	
	/* The set of output ports of the action */
	private PortSet outputSet = new PortSet();
	
	/* The resulting "module" after processing this action - this links to a
	 * object in the list of the top-level term's module constants */
	private Module resultModule;
	
	
	/* Constructs the action */
	public IOAction(PortSet inputSet2, PortSet outputSet2, Module state) {
		inputSet=inputSet2;
		outputSet=outputSet2;
		resultModule=state;
	}
	
	/* Clears the input set of the action */
	public void clearInputs() {
		inputSet.clear();
	}

	/* Returns a copy of the action. Input and output sets are deep copied (as they
	 * may be modified), the resulting module state is not deep copied as recall that it
	 * points to a global instance (which will in turn be deep copied only when it needs to
	 * be modified) */
	public IOAction copy(){
		return new IOAction(inputSet.deepCopy(),outputSet.deepCopy(),resultModule);
	}

	/* Returns the input port at the specified index in the set */
	public String getInputPort(int k) {
		return inputSet.getPort(k);
	}
	
	/* Returns the input set of the action */
	public PortSet getInputSet() {
		return inputSet;
	}

	/* Returns the number of inputs in the input set */
	public int getNoOfInputs() {
		return inputSet.getNoOfPorts();
	}

	/* Returns the number of outputs in the output set */
	public int getNoOfOutputs() {
		return outputSet.getNoOfPorts();
	}
	
	/* Returns the output set of the action */
	public PortSet getOutputSet() {
		return outputSet;
	}

	/* Returns a copy of the resultModule module when retrieving the resultModule, as we don't
	 * want to (in future) modify the actual fixed constant definition that the stored
	 * module object represents */
	public Module getResult(){
		return resultModule.deepCopy();
	}

	/* Prints the action in CCS-like form */
	public String printAction() {
		if(!intermediate){
			return "("+inputSet.printPorts()+","+outputSet.printPorts()+")."+resultModule.getName();
		}
		else{
			return "(\u2022,"+outputSet.printPorts()+")."+resultModule.getName();
		}
	}

	/* Checks if two actions are the same. As resultModule is always a unique global constant, 
	 * it suffices to check that the "names" of the constant are equivalent (not to be compared with a label,
	 * in the case of instantiating a constant). Input and output sets must be compared in full */
	public boolean sameAs(IOAction ioAction) {
		if(!(!resultModule.getName().equals("") && resultModule.getName().equals(ioAction.resultModule.getName()))){
			return false;
		}
		if(!inputSet.sameAs(ioAction.inputSet)){
			return false;
		}
		if(!outputSet.sameAs(ioAction.outputSet)){
			return false;
		}
		return true;
	}

	/* Sets the result of the action as the given module constant */
	public void setResult(Module module) {
		resultModule=module;
	}

	/* Sets the module this action to be part of an intermediate state.
	 * This makes the bullet symbol appear when displaying this action */
	public void setIntermediate() {
		intermediate=true;
	}

	/* Returns whether the module is in an intermediate state */
	public boolean getIntermediate() {
		return intermediate;
	}
}
