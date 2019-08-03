package DISetAlgebraStructure;

/* A named module in DI-Set algebra. It corresponds to type action in the nM in the thesis */
public class NamedModule {

	/* The module state definition (list of actions). This is a deep copy of one of the constants
	 * in PartiallyVisibleNetwork, and may be modified (the actions themselves are of the form (A,B).M', where
	 * M' still points to a constant, and M' may not be modified until deep copied) */
	private Module module;
	
	/* The label of this named module */
	private String label;
	
	/* Constructs the named module */
	public NamedModule(Module mod, String lab){
		module =mod.deepCopy();
		label=lab;
	}
	
	/* Checks safety of the named module in the expected way, by filtering the entries in the bus that share
	 * the module label, and then checking whether one of the actions' input set is a superset of these entries */
	public boolean checkSafety(NamedPortSet inputSet) {
		
		
		/* If the module is not in an intermediate state and there are actually signals pending */
		if(!module.getAction(0).getIntermediate() && inputSet.getNoOfPorts()>0){

			/* Copy the input signals to this module and strip out duplicate signals */
			NamedPortSet inputSetCopy = inputSet.filterDuplicates();
			
			/* Retrieve each action */
			for(int i=0;i<module.getNoOfActions();i++){
				IOAction current = module.getAction(i);
				
				/* If the input set of this action is greater than zero then it is worth checking */
				if(current.getNoOfInputs()>0){
					
					/* Assume that this action does help satisfy the safety requirement */
					boolean thisActionSatisfies=true;
					
					/* For each input signal, check if the current action contains the input */
					for(int j=0;j<inputSetCopy.getNoOfPorts();j++){
						
						/* Get the current input signal */
						String currentPort = inputSetCopy.getPort(j).getPort();
						
						/* Assume it's not in this action */
						boolean inThisAction = false;
						
						/* Check every input of the action and record if it is found */
						for(int k=0;k<current.getNoOfInputs();k++){
							if(current.getInputPort(k).equals(currentPort)){
								inThisAction=true;
								break;
							}	
						}
						
						/* If this action does not contain the current input signal, skip checking
						 * this action further */
						if(!inThisAction){
							thisActionSatisfies=false;
							break;
						}
					}
					if(thisActionSatisfies){
						return true;
					}
				}
			}
			
			/* This means we've checked all actions and failed to find one that is a superset of the
			 * pending input signals */
			return false;
		}
		else{
			return true;
		}
	}

	/* Performs a deep copy of the named module, note that equal Strings in Java point to a
	 * fixed object in memory, so no point in "copying" the label */
	public NamedModule deepCopy() {
		return new NamedModule(module.deepCopy(),label);
	}

	/* Retrieves the label */
	public String getModuleLabel(){
		return label;
	}
	
	/* Retrieves the state "name" of the module, a.k.a. name of the constant it
	 * is defined by. This will be empty if the module is in an intermediate state (absorbed inputs
	 * but not yet produced outputs): Not to be confused with the module "label" which makes a module into
	 * a named module */
	public String getModuleName() {
		return module.getName();
	}

	/* Gets the actual "module" inside the named module object */
	public Module getModuleState() {
		return module;
	}

	/* Prints out the list of actions defined by the module */
	public String printModuleLine() {
		return module.printModuleLine();
	}

	/* Checks whether the named module is equivalent to a given named module */
	public boolean sameAs(NamedModule namedModule) {
		boolean labelSame = label.equals(namedModule.label);
		boolean defSame =module.sameAs(namedModule.module);
		return labelSame && defSame;
	}

	/* Sets the state "name" (also known as constant identifier) to the
	 * given string */
	public void setModuleName(String name) {
		module.setName(name);
	}
}
