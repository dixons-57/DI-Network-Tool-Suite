package DISetAlgebraStructure;

/* A named port in DI-Set algebra. It corresponds to type nPort in the BNF in the thesis */
public class NamedPort {

	/* The port name, represented using a simple String */
	private String port;
	
	/* The port label */
	private String label;
	
	/* Constructs the named port */
	public NamedPort(String p, String l){
		port=p;
		label=l;
	}

	/* Performs a deep copy of the port (String objects themselves
	 * do not need to be "deep copied") */
	public NamedPort deepCopy() {
		return new NamedPort(port,label);
	}

	/* Retrieves the port label */
	public String getLabel(){
		return label;
	}

	/* Retrieves the port name */
	public String getPort(){
		return port;
	}
	
	/* Prints out the named port */
	public String print() {
		return port+":"+label;
	}
	
	/* Checks if a named port is equivalent to a given named port, by comparing
	 * port name and port label */
	public boolean sameAs(NamedPort namedPort) {
		if(port.equals(namedPort.port) && label.equals(namedPort.label)){
			return true;
		}
		return false;
	}
	
}
