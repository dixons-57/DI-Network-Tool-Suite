package DISetAlgebraStructure;

import java.util.Vector;

/* A wire function in DI-Set algebra. It corresponds to type w in the BNF in the thesis */
public class WireFunction {

	/* The set of connection pairs which make up the function */
	private Vector<WireConnection> connections = new Vector<WireConnection>();
	
	/* Adds the specified connection to the function */
	public void addConnection(WireConnection connect){
		connections.add(connect);
	}

	/* Returns the target named port of the functino given a source named port. If undefined,
	 * identity mapping is assumed */
	public NamedPort getTarget(NamedPort sourcePort){
		for(int i=0;i<connections.size();i++){
			if(connections.get(i).getSource().getLabel().equals(sourcePort.getLabel()) &&
					connections.get(i).getSource().getPort().equals(sourcePort.getPort())){
				return connections.get(i).getTarget().deepCopy();
			}
		}
		return sourcePort.deepCopy();
	}
	
	/* Prints out the wire function as a list of connection pairs */
	public String print() {
		StringBuffer output = new StringBuffer();
		output.append("w = {");
		for(int i=0;i<connections.size();i++){
			output.append(connections.get(i).printPair());
			if(i<connections.size()-1){
				output.append(",");
			}
		}
		output.append("}");
		return output.toString();
	}
	
}
