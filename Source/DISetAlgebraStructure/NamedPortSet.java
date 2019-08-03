package DISetAlgebraStructure;

import java.util.Vector;

/* A set of named ports in DI-Set algebra. It corresponds to type C in the BNF in the thesis */
public class NamedPortSet {

	/* The set of named ports */
	private Vector<NamedPort> portSet = new Vector<NamedPort>();

	/* Adds a named port to the set */
	public void add(NamedPort s) {
		portSet.add(s);
	}

	/* Checks if the give named port is in this set */
	public boolean contains(NamedPort port){
		for(int i=0; i<portSet.size();i++){
			if((portSet.get(i).getPort().equals(port.getPort()) 
					|| portSet.get(i).getPort().equals("*")) &&
					portSet.get(i).getLabel().equals(port.getLabel())){
				return true;
			}
		}
		return false;
	}

	/* Checks if the set contains a given set of ports. Labels are ignored
	 * as this function is only run on a given port set which has identical labels */
	public boolean contains(PortSet child) {
		NamedPortSet copyParent = deepCopy();
		PortSet copyChild = child.deepCopy();
		
		for(int i=copyChild.getNoOfPorts()-1;i>=0;i--){
			String find = copyChild.getPort(i);
			boolean found=false;
			for(int j=copyParent.getNoOfPorts()-1;j>=0;j--){
				NamedPort toCheck = copyParent.getPort(j);
				if(toCheck.getPort().equals(find)){
					found=true;
					copyParent.portSet.remove(j);
					copyChild.removePort(i);
					break;
				}
			}
			if(!found){
				return false;
			}
		}
		return true;
	}

	/* Performs a deep copy of the named port set */
	public NamedPortSet deepCopy() {
		NamedPortSet copy = new NamedPortSet();
		for(int i=0;i<portSet.size();i++){
			copy.add(portSet.get(i).deepCopy());
		}
		return copy;
	}

	/* Returns a copy filters out any duplicate entries in the named port set */
	public NamedPortSet filterDuplicates() {
		NamedPortSet copy = deepCopy();
		for(int j=0;j<getNoOfPorts();j++){
			NamedPort port = getPort(j);
			Vector<Integer> locationsOfCurrent = new Vector<Integer>();
			for(int k=0;k<copy.getNoOfPorts();k++){
				if(copy.getPort(k).sameAs(port)){
					locationsOfCurrent.add(k);
				}
			}
			for(int k=locationsOfCurrent.size()-1;k>0;k--){
				copy.removePort(locationsOfCurrent.get(k));
			}
		}
		return copy;
	}
	
	/* Gets the number of named ports in the set */
	public int getNoOfPorts() {
		return portSet.size();
	}

	/* Gets the named port at the specified index in the set */
	public NamedPort getPort(int i) {
		return portSet.get(i);
	}
	
	/* Prints out the named port set */
	public String printSet() {
		StringBuffer output= new StringBuffer();
		output.append("{");
		for(int i=0;i<portSet.size();i++){
			output.append(portSet.get(i).print());
			if(i<portSet.size()-1){
				output.append(",");
			}
		}
		output.append("}");
		return output.toString();
	}

	/* Removes the named port at the specified index in the set */
	public NamedPort removePort(int i){
		return portSet.remove(i);
	}

	/* Checks if the named port set is equal to the given named port set */
	public boolean sameAs(NamedPortSet compare) {
		NamedPortSet firstSet = deepCopy();
		NamedPortSet secondSet = compare.deepCopy();
		
		if(firstSet.getNoOfPorts()!=secondSet.getNoOfPorts()){
			return false;
		}
		for(int i=0;i<firstSet.getNoOfPorts();i++){
			NamedPort firstPort = firstSet.getPort(i);
			boolean found = false;
			for(int j=0;j<secondSet.getNoOfPorts();j++){
				NamedPort secondPort = secondSet.getPort(j);
				if(firstPort.sameAs
						(secondPort)){
					found=true;
					secondSet.portSet.remove(j);
					break;
				}
			}
			if(!found){
				return false;
			}
		}
		return true;
	}

	/* Checks if this named port set is a super set of the given named port set.*/
	public boolean superSet(NamedPortSet child) {
		NamedPortSet firstSet = deepCopy();
		NamedPortSet secondSet = child.deepCopy();

		/* First strip out all instances of the second set
		 * from a copy of this set. If a component is not present
		 * in the copy of this set then return false */
		for(int i=0;i<secondSet.getNoOfPorts();i++){
			NamedPort secondPort = secondSet.getPort(i);
			boolean found = false;
			for(int j=0;j<firstSet.getNoOfPorts();j++){
				NamedPort firstPort = firstSet.getPort(j);
				if(firstPort.sameAs(secondPort)){
					found=true;
					firstSet.portSet.remove(j);
					break;
				}
			}
			if(!found){
				return false;
			}
		}
		
		/*If we reach here then the second set is definitely
		 * a subset of the first set */
		
		return true;
	}
	
}
