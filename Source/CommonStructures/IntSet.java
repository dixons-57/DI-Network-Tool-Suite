package CommonStructures;

import java.util.Vector;

/* Representing a set of integers. Typical set operations are present.
 * The class can also be used as an ordered set (but not multiset) for certain
 * operations requiring sequences, and the prefix operation assumes that
 * it is being used as an ordered set. Equality checking assumes normal sets
 * and not ordered sets. */
public class IntSet{

	/* The actual set data stored as a vector */
	private Vector<Integer> contents = new Vector<Integer>();

	/* Initialises the set with no values */
	public IntSet() {
	}

	/* Initialises the set with a single element */
	public IntSet(int input) {
		contents.add(new Integer(input));
	}

	/* Same as above but uses an array of values */
	public IntSet(int[] initials){
		for(int i=0;i<initials.length;i++){
			contents.addElement(new Integer(initials[i]));
		}
	}

	/* Initialises the set with a given vector of values */
	public IntSet(Vector<Integer> initials){
		for(int i=0;i<initials.size();i++){
			contents.addElement(initials.get(i));
		}
	}

	/* Adds a given value to the set if it doesn't exist
	 * in it already */
	public boolean add(int add){
		if(contains(add)==-1){
			contents.addElement(add);
			return true;
		}
		return false;
	}

	/* Checks whether a given value exists in the set,
	 * and returns its index if it does */
	public int contains(int check){
		for(int i=0;i<contents.size();i++){
			if(contents.get(i)==(check)){
				return i;
			}
		}
		return -1;
	}

	/* Checks if the set contains any negative numbers */
	public boolean containsNegative(){	
		for(int i=0;i<size();i++){
			if(get(i)<0){
				return true;
			}
		}
		return false;
	}

	/* Returns a deep copy of the set object */
	public IntSet deepCopy(){
		IntSet copy = new IntSet();
		for(int i=0;i<size();i++){
			copy.add(get(i));
		}
		return copy;
	}

	/* Checks whether this set is equal to the second set */
	public boolean equals(IntSet second){
		if(subset(second) && second.subset(this)){
			return true;
		}
		else{
			return false;
		}
	}

	/* Gets the value at the given index if the
	 * index is valid */
	public int get(int index){
		if(index<0 || index>=contents.size()){
			return -1;
		}
		return contents.get(index).intValue();
	}

	/* Assuming that this is an ordered set, returns the 
	 * subset given by a starting index and ending index of 
	 * this set (note elements are stored in a linear list) */
	public IntSet getSubSet(int start, int end) {
		IntSet subset = new IntSet();
		for(int i=start;i<=end;i++){
			subset.add(get(i));
		}
		return subset;
	}

	/* Return all possible sequences given by all
	 * possible permutations of this set */
	public Vector<IntSet> permutations(){
		return recursePermutations(deepCopy());
	}
	
	/* Recursive method used by the above function to generate
	 * all possible sequences: it strips out an item, then finds
	 * all possible permutations of the resulting list, then adds
	 * the stripped item to the start of all such permutations */
	private Vector<IntSet> recursePermutations(IntSet list) {
		
		/* List of sequences to return up the stack */
		Vector<IntSet> toReturn = new Vector<IntSet>();
		if(list.size()==1){
			toReturn.add(list);
		}
		
		/* For every item in the current list */
		for(int i=0;i<list.size();i++){
			
			/* Make a fresh copy of the list */
			IntSet subList=list.deepCopy();
			
			/* Strip out the ith item */
			int firstItem=subList.contents.remove(i);
			
			/* Find all sequences/permutations of the new list */
			Vector<IntSet> subListPermutations = recursePermutations(subList);
			
			/* For all possible permutations of the new list, add the stripped item
			 * back in, and send them up the stack */
			for(int j=0;j<subListPermutations.size();j++){
				IntSet retrieved = subListPermutations.get(j).deepCopy();
				retrieved.contents.add(0,firstItem);
				toReturn.add(retrieved);
			}
		}
		return toReturn;
	}

	/* Returns the power set of this set: uses an iterative algorithm
	 * where results are continuously cloned and then new elements
	 * are added to existing results */
	public SetSet powerSet(){
		SetSet result = new SetSet();
		IntSet singleton = new IntSet();
		singleton.add(get(0));
		result.add(singleton);

		/* For each element of the original set except the first */
		for(int i=1;i<size();i++){

			/* Clone the current results */
			SetSet clone = new SetSet();
			for(int j=0;j<result.size();j++){
				clone.add(result.get(j).deepCopy());
			}

			/* For every element in the clone, add the current element to the subset */
			for(int j=0;j<clone.size();j++){
				clone.get(j).add(get(i));
			}			

			/* Add the element on its own too */
			IntSet single = new IntSet();
			single.add(get(i));
			clone.add(single);

			/* Add the additional results to the main set */
			for(int j=0;j<clone.size();j++){
				result.add(clone.get(j));
			}
		}
		return result;	
	}

	/* Assuming this is an ordered set, check whether this set
	 * is a prefix of the passed set */
	public boolean prefix(IntSet second){
		if(size()>second.size()){
			return false;
		}
		else{
			for(int i=0;i<size();i++){
				if(get(i)!=second.get(i)){
					return false;
				}
			}
			return true;
		}
	}

	/* Prints the set out by mapping it to a given vector of
	 * strings and printing them based on their corresponding index */
	public String printStringRepresentation(Vector<String> map) {
		StringBuffer out = new StringBuffer();
		out.append("{");
		for(int i=0;i<size();i++){
			out.append(map.get(get(i)));
			if(i<size()-1){
				out.append(",");
			}
		}
		out.append("}");
		return out.toString();
	}
	
	/* Prints the set in its "raw" integer form */
	public String printIntRepresentation() {
		StringBuffer out = new StringBuffer();
		out.append("{");
		for(int i=0;i<size();i++){
			out.append(get(i));
			if(i<size()-1){
				out.append(",");
			}
		}
		out.append("}");
		return out.toString();
	}

	/* Checks whether this set is a proper subset of
	 * the passed set */
	public boolean properSubset(IntSet second){
		if(subset(second) && !equals(second)){
			return true;
		}
		else{
			return false;
		}
	}

	/* Removes a given value if it exists in the set */
	public boolean remove(int remove){
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
	public IntSet setDifference(IntSet second){
		IntSet result = new IntSet();
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
	public IntSet setUnion(IntSet second){
		IntSet result = new IntSet(contents);
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
	public boolean subset(IntSet second){
		for(int i=0;i<size();i++){
			int item = get(i);
			if(second.contains(item)==-1){
				return false;
			}
		}
		return true;
	}
}
