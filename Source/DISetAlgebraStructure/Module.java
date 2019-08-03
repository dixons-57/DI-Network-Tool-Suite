package DISetAlgebraStructure;

import java.util.Vector;

/* A ``module" in DI-Set algebra (i.e. a Set Notation module's single state definition in CCS-like form). 
 * It corresponds to type M in the BNF in the thesis */
public class Module {

	/* The list of actions of the module */
	private Vector<IOAction> actions = new Vector<IOAction>();
	
	/* The state constant name that the current list of actions represents, this is set to empty when the module enters an intermediate
	 * state (represented by a bullet symbol in the algebra). Not to be confused with a named module's "label" */
	private String stateName;
	
	/* Constructs the module object and assigns it the given state constant name */
	public Module(String name) {
		stateName=name;
	}

	/* Constructs the module object and assigns it the given state constant name, and given
	 * list of actions */
	public Module(String name, Vector<IOAction> listOfActions) {
		stateName=name;
		actions=listOfActions;
	}
	
	/* Adds an action to the list of actions */
	public void addAction(IOAction newAction){
		actions.add(newAction);
	}
	
	/* Clears the list of actions. This happens when the ``module" evolves into other ``modules"
	 * as a result of transitions */
	public void clearActions() {
		actions.clear();
	}
	
	/* Returns a deep copy of this object */
	public Module deepCopy(){
		Module copy = new Module(stateName);
		for(int i=0;i<actions.size();i++){
			copy.actions.add(actions.get(i).copy());
		}
		return copy;
	}

	/* Gets the action at the given index in the list */
	public IOAction getAction(int index){
		return actions.get(index);
	}
	
	/* Gets the state constant name */
	public String getName(){
		return stateName;
	}

	/* Gets the number of actions */
	public int getNoOfActions(){
		return actions.size();
	}

	/* Prints out the ``module" (i.e. the list of actions, remember that this ``module"
	 * is a single state definition of a Set Notation module) */
	public String printModuleLine() {
		StringBuffer output = new StringBuffer();
		if(!stateName.equals("")){
			output.append(stateName+" = ");
		}
		for(int i=0;i<actions.size();i++){
			output.append(actions.get(i).printAction());
			if(i<actions.size()-1){
				output.append(" + ");
			}
		}
		return output.toString();
	}
	
	/* Assumes name equivalence is sufficient to positively verify, as constant names
	 * are unique. If the names are non-equal or empty, then actions are compared
	 * absolutely */
	public boolean sameAs(Module secondModule) {
		if(!stateName.equals("") && stateName.equals(secondModule.stateName)){

			return true;
		}
		if(actions.size()!=secondModule.actions.size()){
			return false;
		}
		for(int i=0;i<actions.size();i++){
			if(!actions.get(i).sameAs(secondModule.actions.get(i))){
				return false;
			}
		}
		return true;
	}

	/* Sets the state constant name */
	public void setName(String replacementName) {
		stateName=replacementName;
	}
	
}
