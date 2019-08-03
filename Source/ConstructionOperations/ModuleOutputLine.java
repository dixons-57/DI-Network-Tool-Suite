package ConstructionOperations;

/* Represents the output line of a module in the construction generated in the Construction tab. */
public class ModuleOutputLine {

	/* The name of the output line */
	private String name;
	
	/* The index of the output line in the list of output lines of the module*/
	private int outputIndexInModule;
	
	/* The actual module that this is line belong to */
	private ConstructionModule parentModule;
	
	/* Constructs the output line */
	public ModuleOutputLine(String name, ConstructionModule module, int index){
		this.name=name;
		parentModule=module;
		this.outputIndexInModule=index;
	}
	
	/* Constructs the output line without storing its module */
	public ModuleOutputLine(String name, int index){
		this.name=name;
		this.outputIndexInModule=index;
	}
	
	/* Gets the index of the output line in the list of output lines of the module */
	public int getIndex(){
		return outputIndexInModule;
	}
	
	/* Get the module that this line belongs to */
	public ConstructionModule getModule(){
		return parentModule;
	}

	/* Get the name of this output line */
	public String getName(){
		return name;
	}
	
	/* Prints the name of the module which this belongs to and the output line's name */
	public String print(){
		return parentModule.getIdentifyingName() +"-  {"+name+"}";
	}
	
	/* Set the module that this output line belongs to */
	public void setModule(ConstructionModule module){
		parentModule=module;
	}
	
}
