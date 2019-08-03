package ConstructionOperations;

/* Represents a wire in the construction generated in the Construction tab. */
public class Wire{
	
	/* The output line at the start of this wire */
	private ModuleOutputLine startPort; 	
	
	/* The input line at the end of this wire */
	private ModuleInputLine endPort;
	
	/* Constructs this wire */
	public Wire(ModuleOutputLine start, ModuleInputLine end){
		this.startPort=start;
		this.endPort=end;
	}
	
	/* Get the input line at the end of this wire */
	public ModuleInputLine getInPort(){
		return endPort;
	}
	
	/* Get the output line at the start of this wire */
	public ModuleOutputLine getOutPort(){
		return startPort;
	}
	
	/* This inverts the wire, by swapping the output port and input port. It
	 * assumes that the modules in the network have already been inverted prior to
	 * the inversion of the wires. Hence the index of the new output port of the module
	 * at the beginning of the wire is the same value as the index of the old input port 
	 * of the module that was previously at the end of the wire, and vice versa */ 
	public void invertWire(){
		ConstructionModule oldStartModule = startPort.getModule();
		int oldStartIndex = startPort.getIndex();
		ConstructionModule oldEndModule = endPort.getModule();
		int oldEndIndex = endPort.getIndex();
		startPort=oldEndModule.getOutput(oldEndIndex);
		endPort=oldStartModule.getInput(oldStartIndex);
	}
	
	/* Print this wire */
	public String print(){
		return startPort.getModule().getModuleIndexInCircuitList()+": "+startPort.print()
		+"    ->    "+endPort.getModule().getModuleIndexInCircuitList() +": "+endPort.print();
	}
	
}
