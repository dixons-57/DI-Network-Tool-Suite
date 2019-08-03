package DISetAlgebraStructure;

import java.util.Vector;

/* A partially-visible network in DI-Set algebra. It corresponds to type S=L-C in the BNF in the thesis. It is
 * the "top-level" term in the syntax. Note that, as described in the thesis, a "network" (type L in the BNF) is
 * not implemented directly. Instead the sub-components which make up L=P||G are stored here as fields moduleInstances and bus.
 * Hence this more directly corresponds to S=P||G-C. We therefore refer to this term more simply as a network throughout the software, rather than
 * the more verbose partially-visible network.
 * For convenience, all module constants (constants of type M in the BNF) which are used by this term are stored in this class in the constantDefinitions
 * field. "Instances" of these constants written within the S term simply point to these constant objects, until they need to be modified (due to a
 * transition involving S), during which a deep copy of the module constant object occurs, after which it is safe to modify the "local, full" instance. */
public class PartiallyVisibleNetwork {

	/* The name of the network as it appears on screen */
	private String networkName;
	
	/* The set of named modules (P) */
	private NamedModuleSet moduleInstances = new NamedModuleSet();	
	
	/* The bus (G) */
	private Bus bus;
	
	/* The set of hidden named ports (C) */
	private NamedPortSet hiddenPorts;
	
	/* The set of module state constant definitions which are referred to within this term. These are simply referenced by actions
	 * until any modifications are needed, and then they are deep copied into the action before they are modified*/
	private Vector<Module> constantDefinitions = new Vector<Module>();
	
	/* Adds the named module to the network (i.e. to the set P) */
	public void addModuleInstance(NamedModule namedModule) {
		moduleInstances.addModule(namedModule);
	}
	
	/* Deep copies everything except for module definitions, wire function and hidden ports,
	 * as these do not evolve between states of the LTS, and so can refer to the same thing */
	public PartiallyVisibleNetwork copy(){
		PartiallyVisibleNetwork copy = new PartiallyVisibleNetwork();
		
		/* these don't get modified so no need to deep copy these */
		copy.constantDefinitions=constantDefinitions;
		copy.hiddenPorts=hiddenPorts;
		copy.networkName=networkName;
		copy.bus=bus.copy();
		copy.moduleInstances=moduleInstances.deepCopy();
		
		return copy;
	}
	
	/* Gets the bus (G) */
	public Bus getBus() {
		return bus;
	}
	
	/* Gets the set of hidden ports (C) */
	public NamedPortSet getHiddenPorts() {
		return hiddenPorts;
	}
	
	/* Gets the constant definition at the given index */
	public Module getConstantDefinition(int i){
		return constantDefinitions.get(i);
	}
	
	/* Gets the named module instance at the given index (in P) */
	public NamedModule getModuleInstance(int index){
		return moduleInstances.getModule(index);
	}
	
	/* Gets the number of constant definitions stored in this object */
	public int getNoOfConstantDefinitions(){
		return constantDefinitions.size();
	}

	/* Gets the number of named modules (in P) */
	public int getNoOfModuleInstances(){
		return moduleInstances.getNoOfModules();
	}

	/* Checks whether this network is a "superset" of the given network. This is for the purpose
	 * of checking infinite growth when generating the LTS for this network. It involves checking
	 * whether named module states are equivalent, and then checking whether the bus contents for this network
	 * are a superset of the bus contents of the given network */
	public boolean isSuperNetwork(PartiallyVisibleNetwork smaller) {
		for(int i=0 ;i<moduleInstances.getNoOfModules();i++){
			if(!moduleInstances.getModule(i).sameAs(smaller.moduleInstances.getModule(i))){
				return false;
			}
		}
		if(!bus.superBus(smaller.bus)){
			return false;
		}
		return true;
	}

	/* Prints the definition of this network, including all constant definitions and the wire function */
	public String printEntireDefinition() {
		StringBuffer output = new StringBuffer();
		for(int i=0;i<constantDefinitions.size();i++){
			output.append(constantDefinitions.get(i).printModuleLine()+";\n");
		}
		output.append(bus.getWireFunction().print()+";\n");
		output.append(printNetwork()+";\n");
		return output.toString();
	}

	/* Prints the definition of this network (but not accompanying constant definitions or wire function, or the name of the network) */
	public String printNetwork(){
		StringBuffer output = new StringBuffer();
		output.append(networkName+" = ");
		output.append(printNetworkWithoutName());
		return output.toString();
	}
	
	/* Prints the definition of this network (but not accompanying constant definitions or wire function) */
	public String printNetworkWithoutName(){
		StringBuffer output = new StringBuffer();
		output.append(moduleInstances.printModuleSet());
		output.append("|| ");
		output.append(bus.printContents());
		output.append(" - ");
		output.append(hiddenPorts.printSet());
		return output.toString();
	}

	/* Replaces the named module at the given index (in P) with the given named module */
	public void replaceModuleInstance(int index, NamedModule replacementModule) {
		moduleInstances.replaceModule(index,replacementModule);
	}

	/* Checks that this network is the same as the given network. This simply checks that all
	 * named modules are in equivalent states, and the contents of the bus are equivalent. This
	 * is used when checking for membership of a state in the LTS, so it assumes that named modules
	 * in both networks at the same index (in P) contain the same label, and the wire function and
	 * constant definitions are equivalent, as these do not change between states. */
	public boolean sameAs(PartiallyVisibleNetwork compare) {
		for(int i=0;i<moduleInstances.getNoOfModules();i++){
			if(!moduleInstances.getModule(i).sameAs(compare.moduleInstances.getModule(i))){
				return false;
			}
		}
		if(!bus.sameAs(compare.bus)){
			return false;
		}
		return true;
	}

	/* Sets the bus (G) to the given bus */
	public void setBus(Bus newBus) {
		bus=newBus;
	}

	/* Sets the hidden port set (C) to the given set of named ports */
	public void setHiddenPorts(NamedPortSet hiddenSet) {
		hiddenPorts=hiddenSet;
	}

	/* Sets the list of constant definitions to the given list */
	public void setConstantDefinitions(Vector<Module> replacementDefinitions) {
		constantDefinitions=replacementDefinitions;
	}

	/* Sets the name of the network (really just an aesthetic moniker) to the given name */
	public void setName(String name) {
		networkName=name;
	}
	
}
