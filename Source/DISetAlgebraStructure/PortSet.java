package DISetAlgebraStructure;

import java.util.Vector;

/* A set of ports in DI-Set algebra. It corresponds to type A in the BNF in the thesis */
public class PortSet {

	/* The set of ports */
	private Vector<String> contents = new Vector<String>();

	/* Adds the given port to the set */
	public void addPort(String port) {
		contents.add(port);
	}

	/* Clears the set of ports */
	public void clear() {
		contents.clear();
	}

	/* Checks whether the given port is present in the set */
	public boolean contains(String port) {
		for(int i=0;i<contents.size();i++){
			if(port.equals(contents.get(i))){
				return true;
			}
		}
		return false;
	}

	/* Deep copies the port set. It provides a fresh object
	 * with a fresh Vector, but identical Strings in Java all point to the same
	 * memory location, so these don't need to be copied */
	public PortSet deepCopy() {
		PortSet copy = new PortSet();
		for(int i=0;i<contents.size();i++){
			copy.addPort(contents.get(i));
		}
		return copy;
	}

	/* Gets the number of ports in the set */
	public int getNoOfPorts() {
		return contents.size();
	}

	/* Gets the port at the specified index */
	public String getPort(int index) {
		return contents.get(index);
	}

	/* Prints the set of ports */
	public String printPorts() {
		StringBuffer output = new StringBuffer();
		output.append("{");
		for(int i=0;i<contents.size();i++){
			output.append(contents.get(i));
			if(i<contents.size()-1){
				output.append(",");
			}
		}
		output.append("}");
		return output.toString();
	}

	/* Removes the port at the specified index */
	public void removePort(int index) {
		contents.remove(index);
	}

	/* Checks whether this port set is the same as the given port set. */
	public boolean sameAs(PortSet compare) {
		PortSet firstSet = deepCopy();
		PortSet secondSet = compare.deepCopy();
		if(firstSet.getNoOfPorts()!=secondSet.getNoOfPorts()){
			return false;
		}
		for(int i=0;i<firstSet.getNoOfPorts();i++){
			boolean found = false;
			for(int j=0;j<secondSet.getNoOfPorts();j++){
				if(firstSet.contents.get(i).equals(secondSet.contents.get(j))){
					found=true;
					secondSet.contents.remove(j);
					break;
				}
			}
			if(!found){
				return false;
			}
		}
		return true;
	}
	
}
