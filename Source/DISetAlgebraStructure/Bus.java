package DISetAlgebraStructure;

/* A communication bus in DI-Set algebra. It corresponds to type G in the BNF in the thesis */
public class Bus {
	
	/* The wire function which is part of the bus */
	private WireFunction w = new WireFunction();
	
	/* The actual bus contents */
	private NamedPortSet contents = new NamedPortSet();
	
	/* Adds the given set of named ports to the bus */
	public void addMultipleSignals(NamedPortSet signals) {
		for(int i=0;i<signals.getNoOfPorts();i++){
			addSignal(signals.getPort(i));
		}
	}

	/* Adds the given named port to the bus */
	public void addSignal(NamedPort signal){
		contents.add(signal);
	}

	/* Retrieves the wire function */
	public WireFunction getWireFunction(){
		return w;
	}
	
	/* Sets the wire function */
	public void setWireFunction(WireFunction set){
		w=set;
	}
	
	/* Returns a copy of the bus. The signal contents are
	 * deep copied, but the wire function is shallow copied, as it
	 * is never modified during "execution" of a term */
	Bus copy() {
		Bus copy = new Bus();
		copy.contents=contents.deepCopy();
		copy.setWireFunction(getWireFunction());
		return copy;
	}

	/* Retrieve the contents of the bus */
	public NamedPortSet getContents() {
		return contents;
	}

	/* Retrieves contents which match the specified module label */
	public NamedPortSet getPortsWithLabel(String label) {
		NamedPortSet filtered = new NamedPortSet();
		for(int i=0;i<contents.getNoOfPorts();i++){
			if(contents.getPort(i).getLabel().equals(label)){
				filtered.add(contents.getPort(i).deepCopy());
			}
		}
		return filtered;
	}

	/* Print the contents of the bus */
	String printContents() {
		return contents.printSet()+"w";
	}

	/* Removes the specified set of signals from the bus */
	public void removeMultipleSignals(NamedPortSet signals) {
		for(int i=0;i<signals.getNoOfPorts();i++){
			NamedPort passedPort = signals.getPort(i);
			for(int j=contents.getNoOfPorts()-1;j>=0;j--){
				NamedPort busPort = contents.getPort(j);
				if(busPort.getLabel().equals(passedPort.getLabel()) &&
						passedPort.getPort().equals(busPort.getPort())){
					contents.removePort(j);
					break;
				}
			}
		}
	}

	/* Checks whether the given bus has the same contents as this bus */
	boolean sameAs(Bus otherBus) {
		Bus secondBus = otherBus;
		return contents.sameAs(secondBus.contents);
	}

	/* Checks whether the contents of this bus are a superset of the contents of the
	 * given bus */
	boolean superBus(Bus otherBus) {
		return contents.superSet(otherBus.contents);
	}
}
