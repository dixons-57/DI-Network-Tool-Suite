package DISetAlgebraLTSStructure;

import DISetAlgebraStructure.NamedPortSet;
import DISetAlgebraStructure.PartiallyVisibleNetwork;

/* Represents a transition in the LTS. It stores the network term
 * in the source and target states of the LTS (but not the encapsulating
 * state objects themselves */
public class Transition {

	/* The label of the transition, not including the input or output
	 * operator (? and !), representing by a set of named ports */
	private NamedPortSet transitionLabel;
	
	/* The network term in the source state of the LTS */
	private PartiallyVisibleNetwork sourceStateTerm;
	
	/* The network term in the target state of the LTS */
	private PartiallyVisibleNetwork targetStateTerm;
	
	/* The type of the transition:
	 * 0 for input transition
	 * 1 for output transition
	 * 2 for tau */
	private int type;

	/* Return the label of the transition */
	public NamedPortSet getTransitionLabel() {
		return transitionLabel;
	}
	
	/* Get the network term in the source state of the transition */
	public PartiallyVisibleNetwork getSourceStateTerm(){
		return sourceStateTerm;
	}

	/* Get the network term in the target state of the transition */
	public PartiallyVisibleNetwork getTargetStateTerm(){
		return targetStateTerm;
	}

	/* Return the type of the transition */
	public int getType() {
		return type;
	}

	/* Print the transition */
	public String printArrow() {
		String label=null;
		if(type>=2){
			label="t";
		}
		else if(type==0){
			label="?"+transitionLabel.printSet();
		}
		else if(type==1){
			label="!"+transitionLabel.printSet();
		}
		return("--->"+label);
	}

	/* Set the label of the transition */
	public void setTransitionLabel(NamedPortSet effects) {
		transitionLabel=effects;
	}

	/* Set the network term in the source state of the transition */
	public void setSourceStateTerm(PartiallyVisibleNetwork state) {
		sourceStateTerm=state;
	}

	/* Set the network term in the target state of the transition */
	public void setTargetStateTerm(PartiallyVisibleNetwork state) {
		targetStateTerm=state;
	}

	/* Set the type of the transition */
	public void setType(int transitionType) {
		type=transitionType;
	}
	
}
