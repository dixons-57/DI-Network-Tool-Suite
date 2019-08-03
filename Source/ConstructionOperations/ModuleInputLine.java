package ConstructionOperations;

/* Represents the input line of a module in the construction generated in the Construction tab. */
public class ModuleInputLine {

	/* The name of the output line */
	private String name;
	
	/* The index of the input line in the list of input lines of the module*/
	private int inputIndexInModule;	
	
	/* The actual module that this is line belong to */
	private ConstructionModule parentModule;
	
	/* Constructs the input line */
	public ModuleInputLine(String name, ConstructionModule module, int index){
		this.name=name;
		parentModule=module;
		this.inputIndexInModule=index;
	}
	
	/* Constructs the input line without storing its module */
	public ModuleInputLine(String name, int index){
		this.name=name;
		this.inputIndexInModule=index;
	}
	
	/* Gets the index of the input line in the list of input lines of the module */
	public int getIndex(){
		return inputIndexInModule;
	}
	
	/* Get the module that this line belongs to */
	public ConstructionModule getModule(){
		return parentModule;
	}
	
	/* Get the name of this input line */
	public String getName(){
		return name;
	}
	
	/* Prints the name of the module which this belongs to and the input line's name */
	public String print(){
		return parentModule.getIdentifyingName() +"-  {"+name+"}";
	}
	
	/* Set the module that this input line belongs to */
	public void setModule(ConstructionModule module){
		parentModule=module;
	}
}
