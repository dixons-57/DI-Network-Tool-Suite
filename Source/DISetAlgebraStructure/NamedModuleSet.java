package DISetAlgebraStructure;

import java.util.Vector;

/* A set of named modules in DI-Set algebra. It corresponds to type P in the BNF in the thesis */
public class NamedModuleSet {

	/* The set of named modules */
	private Vector<NamedModule> moduleSet = new Vector<NamedModule>();

	/* Adds a named module to the set */
	public void addModule(NamedModule namedModule) {
		moduleSet.add(namedModule);
	}

	/* Performs a deep copy of the named module set */
	public NamedModuleSet deepCopy() {
		NamedModuleSet copy = new NamedModuleSet();
		for(int i=0;i<moduleSet.size();i++){
			copy.addModule(moduleSet.get(i).deepCopy());
		}
		return copy;
	}

	/* Returns the named module at the specified index */
	public NamedModule getModule(int i){
		return moduleSet.get(i);
	}
	
	/* Retrieves the number of named modules in the set */
	public int getNoOfModules() {
		return moduleSet.size();
	}

	/* Prints out the whole set of named modules */
	public String printModuleSet() {
		StringBuffer output = new StringBuffer();
		for(int i=0;i<moduleSet.size();i++){
			output.append("(");
			if(!moduleSet.get(i).getModuleName().equals("")){
				output.append(moduleSet.get(i).getModuleName());
			}
			else{
				output.append(moduleSet.get(i).printModuleLine());
			}
			output.append("):");
			
			if(!moduleSet.get(i).getModuleLabel().equals("")){
				output.append(moduleSet.get(i).getModuleLabel());
			}
			else{
				output.append("\"\"");
			}
			output.append(" ");
			if(i<moduleSet.size()-1){
				output.append("|");
			}
		}
		return output.toString();
	}

	/* Replaces the named module at the specified index with the given named module.
	 * This generally happens when modifying the network as a result of a named module
	 * performing an input or output transition: in turn making it evolve into a different
	 * named module */
	public void replaceModule(int index, NamedModule namedModule) {
		moduleSet.set(index, namedModule);
	}
	
}
