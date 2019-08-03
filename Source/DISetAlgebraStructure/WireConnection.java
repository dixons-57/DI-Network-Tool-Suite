package DISetAlgebraStructure;

/* A wire connection in DI-Set algebra. It corresponds to type wire in the BNF in the thesis */
public class WireConnection {

	/* The source named port of the wire connection */
	private NamedPort sourcePort;
	
	/* The target named port of the wire connection */
	private NamedPort targetPort;
	
	/* Constructs the wire connection */
	public WireConnection(NamedPort source, NamedPort target){
		sourcePort=source;
		targetPort=target;
	}

	/* Gets the named port at the source of the connection */
	public NamedPort getSource(){
		return sourcePort;
	}
	
	/* Gets the named port at the target of the connection */
	public NamedPort getTarget(){
		return targetPort;
	}
	
	/* Prints the connection */
	public String printPair() {
		return "("+sourcePort.print()+","+targetPort.print()+")";
	}
	
}
