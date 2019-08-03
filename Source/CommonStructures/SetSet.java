package CommonStructures;

import java.util.Vector;

/* Representing a set of IntSet objects. Typical set operations are present.
 * Many operations rely heavily on corresponding IntSet operations. */
public class SetSet {

	/* The actual set data stored as a vector */
	private Vector<IntSet> contents = new Vector<IntSet>();

	/* Initialises the set with no values */
	public SetSet() {
	}
	
	/* Initialises the set with a vector of IntSet objects*/
	public SetSet(Vector<IntSet> initials){
		for(int i=0;i<initials.size();i++){
			contents.addElement(initials.get(i));
		}
	}

	/* Adds a given IntSet to the set if it doesn't exist
	 * in it already */
	public boolean add(IntSet add){
		if(contains(add)==-1){
			contents.addElement(add);
			return true;
		}
		return false;
	}

	/* Checks whether a given IntSet exists in the set,
	 * and returns its index if it does */
	public int contains(IntSet check){
		for(int i=0;i<contents.size();i++){
			if(contents.get(i).equals(check)){
				return i;
			}
		}
		return -1;
	}

	/* Returns a deep copy of the set object */
	public SetSet deepCopy(){
		SetSet copy = new SetSet();
		for(int i=0;i<size();i++){
			copy.add(get(i).deepCopy());
		}
		return copy;
	}

	/* Checks whether this set is equal to the second set */
	public boolean equals(SetSet second){
		if(subset(second) && second.subset(this)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/* Gets the value at the given index if the
	 * index is valid */
	public IntSet get(int index){
		if(index<0 || index>=contents.size()){
			return null;
		}
		return contents.get(index);
	}

	/* Prints the set out by mapping each IntSet element it to a given vector of
	 * strings and printing them based on their corresponding index */
	public String printStringRepresentation(Vector<String> map) {
		StringBuffer out = new StringBuffer();
		out.append("{");
		for(int i=0;i<size();i++){
			out.append(get(i).printStringRepresentation(map));
			if(i<size()-1){
				out.append(",");
			}
		}
		out.append("}");
		return out.toString();
	}

	/* Checks whether this set is a proper subset of
	 * the passed set */
	public boolean properSubset(SetSet second){
		if(subset(second) && !equals(second)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/* Removes the value at the given index if the index
	 * is valid */
	public boolean remove(int i){
		if(i>=0 && i<contents.size()){
			contents.removeElementAt(i);
			return true;
		}
		return false;
	}

	/* Removes a given IntSet if it exists in the set */
	public boolean remove(IntSet remove){
		int index = contains(remove);
		if(index==-1){
			return false;
		}
		else{
			contents.remove(index);
			return true;
		}
	}

	/* Standard set difference operation with this
	 * set as the first component, and the passed
	 * set as the second component */
	public SetSet setDifference(SetSet second){
		SetSet result = new SetSet();
		for(int i=0;i<size();i++){
			if(second.contains(get(i))==-1){
				result.add(get(i));
			}
		}
		return result;
	}

	/* Standard set union operation with this
	 * set as the first component, and the passed
	 * set as the second component */
	public SetSet setUnion(SetSet second){
		SetSet result = new SetSet(contents);
		for(int i=0;i<second.size();i++){
			result.add(second.get(i));
		}
		return result;
	}

	/* Returns size of the set */
	public int size(){
		return contents.size();
	}

	/* Checks whether this set is a subset of the passed set */
	public boolean subset(SetSet second){
		for(int i=0;i<size();i++){
			if(second.contains(get(i))==-1){
				return false;
			}
		}
		return true;
	}
}

