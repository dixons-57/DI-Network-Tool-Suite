package ConstructionOperations;
import java.util.Vector;
import CommonStructures.IntSet;
import GUI.ConsoleWindow;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Generates the construction for the Set Notation module parsed/stored in the Construction tab. It selects the correct 
 * algorithm and method for constructing the module depending on whether it is non-arb or eq-arb, and b-arb and non-b-arb. 
 * 
 * Some important information to help understand this class.
 * 
 * For implementation purposes, we consider the construction of the following aspect of stage 1...
 * 1) the wires which end at the inputs of SerNQ or SerNQ' in stage 1 (and stage 2 if non-b-arb), 
 * 2) the Fork trees at the source of these wires and the wire between this Fork tree and the query output of a SerNQ' module in stage 1,
 * 3) the wires which start at the "non-query" outputs of SerNQ and SerNQ' in stage 1 (and stage 2 if non-b-arb) 
 * 4) the Join trees at the end of these wires and the wire between this Join tree and the: query input of a SerNQ module in stage 2
 * if non-b-arb, input of a Fork tree in Stage 2 if b-arb
 * .... to be part of a distinct "update stage" part of the algorithm, rather than to be part of the generation 
 * of stage 1. This is because the majority of both stages need to be generated first in order for us to 
 * know how "big" to make the Fork trees and Join trees, which is dependent on the number and existence of SerNQ and SerNQ' 
 * modules (and Fork tree modules if an irreversible stage 2 is needed). Hence in this class, when referring to "stage 1", 
 * we are referring to the construction of stage 1 (as shown in the thesis), except for these
 * components here which are connected at the end as part of the (software-only) "update stage".
 * 
 * The algorithm used for generating stage 1 is re-used when generating the non-b-arb reversible stage 2. In this case 
 * 1) stage 1 of some original module M is generated as usual, 
 * 2) M is inverted to get M2, 
 * 3) stage 1 of the new inverted module M2 is then generated, but SerQM and SerQM' modules from the original M are used (instead of SerQM2 and SerQM2'). 
 * 4) This new stage 1 of M2 is then "inverted" (with the exception of SerQM and SerQM', which are simply replaced by SerQM' and SerQM respectively). 
 * 5) Finally the two stages are connected up via Join trees and Fork trees, (outlined in the thesis), as part of the "update stage" of the algorithm. 
 * 
 * Hence there is no dedicated algorithm for generating a non-b-arb reversible stage 2. */
public class GenerateDecomposition {

	/* Stores the overall construction (set of modules and wires) */
	private static ConstructionCircuit overallConstruction;
	
	/* The original Set Notation module definition that we are constructing */
	private static SetNotationModule originalModuleDefinition;

	/* The generated SerN definition corresponding to the Set Notation module */
	private static ConstructionModule SerNDefinition;
	
	/* The generated SerNQ definition corresponding to the Set Notation module */
	private static ConstructionModule SerNQDefinition;
	
	/* The generated SerNQ' definition corresponding to the Set Notation module */
	private static ConstructionModule SerNQPrimeDefinition;
	
	/* The set of stage 1 SerNQ modules in the construction */
	private static Vector<ConstructionModule> SerNQModuleInstances;
	
	/* The set of wires linking the stage 1 SerNQ modules to the stage 1 fork trees in the construction */
	private static Vector<Wire> SerNQToForkTreeInterconnections;
	
	/* The set of stage 1 Fork tree modules in the construction */
	private static Vector<ConstructionModule> forkTreeModuleInstances;
	
	/* The set of stage 1 MxN Join columns (composed of MxN Join modules) */
	private static Vector<Vector<Vector<ConstructionModule>>> MxNJoinColumns;
	
	/* The set of wires connecting the stage 1 MxN Join columns */
	private static Vector<Vector<Wire>> MxNJoinColumnInterconnections;
	
	/* The set of stage 1 Choice modules (in the event that the original Set Notation module is eq-arb) */
	private static Vector<ConstructionModule> choiceModuleInstances;
	
	/* The set of stage 1 SerNQ' modules in the construction */ 
	private static Vector<ConstructionModule> SerNQPrimeModuleInstances;

	/* These following are only used if the original module is non-b-arb and we are generating the "reversible"
	 * version of stage 2 */
	
	/* The set of stage 2 SerNQ' modules in the construction - they are SerNQ modules when this stage is initially constructed,
	 * before stage 2 as a whole is "inverted" and these are exchanged for SerNQ' modules (which are not actually mutual inverses) */
	private static Vector<ConstructionModule> outputSerNQPrimeModuleInstances;
	
	/* The set of wires linking the stage 2 Join tree modules (Fork trees prior to inversion) to the stage 2 SerNQ' modules 
	 * (SerNQ prior to "inversion") */
	private static Vector<Wire> outputJoinTreeToSerNQPrimeInterconnections;
	
	/* The set of stage 2 Join tree modules in the construction - they are Fork tree modules when this stage is initially constructed,
	 * before stage 2 as a whole is "inverted" and these are exchanged for Join tree modules */
	private static Vector<ConstructionModule> outputJoinTreeModuleInstances;
	
	/* The set of stage 2 MxN Fork columns (composed of MxN Fork modules) -  they are MxN Join columns (and MxN Join modules) 
	 * when this stage is initially constructed, before stage 2 as a whole is "inverted" and these are exchanged for MxN Fork columns
	 * and MxN Fork modules */
	private static Vector<Vector<Vector<ConstructionModule>>> outputMxNForkColumns;
	
	/* The set of wires connecting the stage 2 MxN Fork columns, these were merely inverted during the inversion operation */
	private static Vector<Vector<Wire>> outputMxNForkColumnInterconnections;
	
	/* The set of stage2 SerNQ modules in the construction - they are SerNQ' modules when this stage is initially constructed,
	 * before stage 2 as a whole is "inverted" and these are exchanged for SerNQ modules (which are not actually mutual inverses) */
	private static Vector<ConstructionModule> outputSerNQModuleInstances;

	/* These following are only used if the original module is b-arb and we are generating the "irreversible"
	 * version of stage 2 */

	/* The set of stage 2 Fork tree modules */
	private static Vector<ConstructionModule> irreversibleOutputStageForkTreeModuleInstances;
	
	/* The set of wires linking the stage 2 Fork tree modules to the stage 2 Merge tree modules */
	private static Vector<Wire> irreversibleOutputStageInterconnections;
	
	/* The set of stage 2 Merge tree modules */
	private static Vector<ConstructionModule> irreversibleOutputStageMergeTreeModuleInstances;

	/* "Update stage" modules and wires */
	
	/* The set of Fork tree modules which link the SerNQ' modules in stage 1 to all SerNQ and SerNQ' modules
	 * in both stages */
	private static Vector<ConstructionModule> updateStageForkTreeModuleInstances;
	
	/* The set of Join tree modules which link the SerNQ and SerNQ' modules in both stages to either
	 * the stage 2 SerNQ modules (if non-b-arb stage 2) or the stage 2 Fork trees (if b-arb stage 2) */
	private static Vector<ConstructionModule> updateStageJoinTreeModuleInstances;
	
	/* The set of wires linking stage 1 and stage 2 together via the above Fork/Join trees and the SerNQ
	 * and SerNQ' modules in both stages */
	private static Vector<Wire> updateStageInterconnections;

	/* Generates the construction of the given Set Notation module. The correct construction method is chosen
	 * depending on the whether the module non-b-arb or b-arb */
	public static ConstructionCircuit generateDecomposition(SetNotationModule setModule){	
		GenerateDecomposition.originalModuleDefinition=setModule;
		
		/* Create SerN module from the Set Notation module definition */
		ConsoleWindow.output("Generating SerN definition");
		SerNDefinition = GenerateSerModules.createSerN(originalModuleDefinition);
		ConsoleWindow.output("SerN generation complete. Definition is: "+SerNDefinition.getInternalSetDefinition().printModule());
		
		/* Print out some useful info about SerN for the user */
		for(int i=0;i<SerNDefinition.getNoOfInputs();i++){
			IntSet setInput = SerNDefinition.getSetInput(i);
			if(!setInput.containsNegative()){
				ConsoleWindow.output("SerN input " +SerNDefinition.getInput(i).getName()+ " maps to "+
						setInput.printStringRepresentation(originalModuleDefinition.getInputNames()));
			}
			else{
				IntSet modifiableSetInput = setInput.deepCopy();
				int value= modifiableSetInput.get(modifiableSetInput.size()-1);
				modifiableSetInput.remove(value);
				value=-value;
				value++;

				ConsoleWindow.output("SerN input " +SerNDefinition.getInput(i).getName()+ " maps to "+ value+"th occurrence of "+
						modifiableSetInput.printStringRepresentation(originalModuleDefinition.getInputNames())+" in the same state");
			}
		}
		
		/* Create SerNQ module from SerN definition */
		ConsoleWindow.output("Generating SerNQ definition");
		SerNQDefinition = GenerateSerModules.createSerNQFromSerN(SerNDefinition);
		ConsoleWindow.output("SerNQ generation complete. Definition is: "+SerNQDefinition.getInternalSetDefinition().printModule());
		
		/* Create SerNQ' module from SerN definition */
		ConsoleWindow.output("Generating SerNQ' definition");
		SerNQPrimeDefinition = GenerateSerModules.createSerNQPrimeFromSerN(SerNDefinition);
		ConsoleWindow.output("SerNQ' generated complete. Definition is: "+SerNQPrimeDefinition.getInternalSetDefinition().printModule());

		/* Initialise empty stage 1 module and wire collections */
		SerNQModuleInstances = new Vector<ConstructionModule>();
		SerNQPrimeModuleInstances = new Vector<ConstructionModule>();
		forkTreeModuleInstances = new Vector<ConstructionModule>();
		SerNQToForkTreeInterconnections=new Vector<Wire>();

		/* Store the stage 1 MxN Join columns
		 * inner Vector - list of MxN Joins making up a column
		 * middle Vector - list of columns making up all columns within the same "state" of the module
		 * outer Vector - list of "states" making up collection of all MxN Joins */
		MxNJoinColumns = new Vector<Vector<Vector<ConstructionModule>>>();

		/* Store wires for the stage 1 MxN Join columns
		 * inner vector is all interconnections in the current "state"
		 * outer vector - list of all sets of wires by "state" */
		MxNJoinColumnInterconnections = new Vector<Vector<Wire>>();
		choiceModuleInstances = new Vector<ConstructionModule>();

		/* Initialise empty stage 2 module and wire collections - these are only used in non-b-arb reversible stage 2 */
		outputSerNQPrimeModuleInstances = new Vector<ConstructionModule>();
		outputSerNQModuleInstances= new Vector<ConstructionModule>();
		outputJoinTreeModuleInstances= new Vector<ConstructionModule>();
		outputJoinTreeToSerNQPrimeInterconnections=new Vector<Wire>();
		
		/* Store the stage 2 MxN Fork columns, same storage format as stage 1 MxN Join columns above */
		outputMxNForkColumns = new Vector<Vector<Vector<ConstructionModule>>>();
		
		/* Store the wires for the stage 2 MxN Fork columns, 
		 * same storage format as stage 1 MxN Join column wires above */
		outputMxNForkColumnInterconnections = new Vector<Vector<Wire>>();

		/* Initialise empty stage 2 module and wire collections - these are only used in b-arb irreversible stage 2 */
		irreversibleOutputStageForkTreeModuleInstances = new Vector<ConstructionModule>();
		irreversibleOutputStageMergeTreeModuleInstances = new Vector<ConstructionModule>();
		irreversibleOutputStageInterconnections = new Vector<Wire>();

		/* Initialise "update stage" module and wire collections */
		updateStageForkTreeModuleInstances=new Vector<ConstructionModule>();
		updateStageJoinTreeModuleInstances=new Vector<ConstructionModule>();
		updateStageInterconnections=new Vector<Wire>();

		/* Initialise the overall circuit storage object */
		overallConstruction=new ConstructionCircuit();
		overallConstruction.setNoOfSerNQForInputStage(originalModuleDefinition.getNoOfInputs());

		/* Initialise stage 1 of construction */
		ConsoleWindow.output("Generating stage 1 (input stage) of construction");

		generateStage1(SerNQModuleInstances,SerNQPrimeModuleInstances,forkTreeModuleInstances,
				SerNQToForkTreeInterconnections,MxNJoinColumns,MxNJoinColumnInterconnections,true);

		ConsoleWindow.output("Stage 1 (input stage) of construction complete");

		/* Remove the "negative" flags/relabellings that we put in the input sets of the Set Notation module if it
		 * was eq-arb. (we did this when generating SerN in order to preserve determinism of SerN - see GenerateSerNodules) */
		for(int i=0;i<originalModuleDefinition.getNoOfTransitions();i++){
			SetTransition currentTransition = originalModuleDefinition.getTransition(i);
			if(currentTransition.getInputSet().containsNegative()){
				IntSet inputSet = currentTransition.getInputSet();
				inputSet.remove(inputSet.get(inputSet.size()-1));
			}
		}
		
		/* The module is non-b-arb, invert it, then build stage 1 for the inverted module, then invert the "new" stage 1 back
		 * to get stage 2 (described in more detail at the top of this file) */
		if(!setModule.checkBarb()){
			ConsoleWindow.output("Generating \"reversible\" stage 2 (output stage) of construction using \"inverse\" of input stage approach");
			ConsoleWindow.output("Inverting original Set Notation module definition to achieve new input stage data");

			originalModuleDefinition.invertModule();

			SerNDefinition.swapSetAppearances();
			
			ConsoleWindow.output("Inversion of original Set Notation module definition complete, resulting module: "+originalModuleDefinition.printModule());
			ConsoleWindow.output("Generating stage 1 (input stage) for inverted module definition");

			generateStage1(outputSerNQPrimeModuleInstances,outputSerNQModuleInstances,outputJoinTreeModuleInstances,
					outputJoinTreeToSerNQPrimeInterconnections,outputMxNForkColumns,outputMxNForkColumnInterconnections,false);

			ConsoleWindow.output("Stage 1 (input stage) for inverted module definiton complete");
			ConsoleWindow.output("Inverting Set Notation module definition back to its initial definition");

			originalModuleDefinition.invertModule();
			SerNDefinition.swapSetAppearances();
			
			ConsoleWindow.output("Restoration of original Set Notation module definition complete");
			ConsoleWindow.output("\"Inverting\" stage 1 (input stage) of inverted Set Notation module definition to achieve stage 2 (output stage) of the original "
					+ "Set Notation module definition");
			invertStage2();

			ConsoleWindow.output("\"Inverting\" of inverted Set Notation module's stage 1 (input stage) complete. \"Reversible\" stage 2 (output stage) of construction complete");
		}
		
		/* If the module is b-arb, build the irreversible stage 2 */
		else{
			ConsoleWindow.output("Generating \"irreversible\" stage 2 (output stage) of construction using Fork trees and Merge trees");
			generateIrreversibleStage2();
			ConsoleWindow.output("\"Irreversible\" stage 2 (output stage) of construction complete");
		}
		
		/* Generate the "update stage" of the construction */
		ConsoleWindow.output("Generating update stage of construction (connecting stage 1 and stage 2 together via Fork/Join trees");
		generateUpdateStage();
		ConsoleWindow.output("Update stage of construction complete");
		ConsoleWindow.output("Collecting up all modules and interconnections into a list, and identifying external ports");
		
		/* Collect modules and wires, label external inputs/outputs of the circuit and finish */
		tidyUpAndLabel();
		return overallConstruction;
	}

	/* Generates stage 1 (input stage) of the construction and adds the resulting modules/wires which are constructed
	 * to the sets passed as variables to this method. This allows the same function to be used to build the reversible stage 2
	 * (prior to inverting it to be in the correct direction, so we need to invert it later on - see above) 
	 * All variable names and comments in the following method assume that the function is being used to build the forwards stage 1 input stage. 
	 * 
	 * This builds the stage 1 described in the thesis in detail, but this is the first and only example of an imperative algorithm which
	 * actually constructs it. Intuitively it is fairly straightforward and simply builds what is described in the thesis for a given module,
	 * but due to the imperative nature is quite tedious and laborious, with lots of variables to remember "free" inputs/outputs,
	 * and which MxN columns need to "cancel" signals from other columns etc. 
	 * 
	 * Please see ConsoleWindow.output calls below, as well as the comments if you are interested in the specific nature of the 
	 * algorithm and exactly what it does in what order. */
	static void generateStage1(Vector<ConstructionModule> SerNQ, Vector<ConstructionModule> SerNQPrime, Vector<ConstructionModule> forkTrees,
			Vector<Wire> SerNQToForks, Vector<Vector<Vector<ConstructionModule>>> MxNJoins, Vector<Vector<Wire>> MxNJoinInterconnects, boolean inputStage){

		ConsoleWindow.output("Creating SerNQ and SerNQ' modules for this stage");

		/* Number of SerNQ is number of input lines to original Set Notation module 
		 * - one SerNQ for each input line a,b,c... etc. */
		int noOfSerNQNeeded=originalModuleDefinition.getNoOfInputs();

		ConsoleWindow.output(noOfSerNQNeeded+ " SerNQ modules are needed for this stage");

		/* Number of SerNQ' is number of input lines for SerN */
		int noOfSerNQPrime=SerNDefinition.getNoOfInputs();

		ConsoleWindow.output(noOfSerNQPrime+ " SerNQ' modules are needed for this stage");

		/* Generate a series of SerNQ modules */
		for(int i=0;i<noOfSerNQNeeded;i++){
			ConstructionModule currentSerNQ = GenerateSerModules.createSerNQFromSerN(SerNDefinition);
			currentSerNQ.setIdentifyingName(currentSerNQ.getIdentifyingName()+" \""+originalModuleDefinition.getInputName(i)+"\"");
			SerNQ.add(currentSerNQ);
			ConsoleWindow.output("Created new SerNQ instance "+currentSerNQ.getIdentifyingName());
		}

		/* Generate a series of SerNQ' modules */
		for(int i=0;i<noOfSerNQPrime;i++){
			ConstructionModule currentSerNQPrime = GenerateSerModules.createSerNQPrimeFromSerN(SerNDefinition);
			StringBuffer setModuleInputSetThatThisMapsTo=new StringBuffer();
			setModuleInputSetThatThisMapsTo.append("{");
			if(inputStage){
				for(int j=0;j<SerNDefinition.getSetInput(i).size();j++){
					if(SerNDefinition.getSetInput(i).get(j)>=0){
							setModuleInputSetThatThisMapsTo.append(originalModuleDefinition.getInputName(SerNDefinition.getSetInput(i).get(j)));
					}
					else{
						setModuleInputSetThatThisMapsTo.append(SerNDefinition.getSetInput(i).get(j));
					}
					if(j<SerNDefinition.getSetInput(i).size()-1){
						setModuleInputSetThatThisMapsTo.append(",");
					}
				}
			}
			setModuleInputSetThatThisMapsTo.append("}");
			currentSerNQPrime.setIdentifyingName(currentSerNQPrime.getIdentifyingName()+(" \""+setModuleInputSetThatThisMapsTo.toString()+"\""));
			SerNQPrime.add(currentSerNQPrime);
			ConsoleWindow.output("Created new SerNQ' instance "+currentSerNQPrime.getIdentifyingName());
		}

		ConsoleWindow.output("Finished creating SerNQ and SerNQ' modules for this stage");
		ConsoleWindow.output("Creating Forks which link SerNQ state query outputs to MxN Join columns");

		/* Generate top-end fork trees (just underneath the SerNQ modules) */
		
		/* For every SerNQ module (i.e. each input line) */
		for(int i=0;i<noOfSerNQNeeded;i++){

			//* For every state (each qx output of the SerNQ) */
			for(int j=0;j<originalModuleDefinition.getNoOfStates();j++){

				/* We need to work out how many actions in this state which the current input line appears in */

				/* Retrieve the "cancellation set" (this is the set of input sets which
				 * appear in the same state as this one), 
				 * Each element of this Cector represents an input
				 * set, and requires a MxN Join column to implement later) */
				IntSet currentCancellationSet = SerNDefinition.getSetInputAppearancesForState(j).deepCopy();

				int noOfOutputsForThisForkTree=0;

				/* Retrieve each input set which appears in this state and work out if it contains the current
				 * input line. If it does, this means that we need to increase the size of the fork tree corresponding
				 * to this SerNQ instance and state, as there is another MxN column that needs signalling when this input
				 * line is signalled */
				for(int k=0; k<currentCancellationSet.size();k++){
					IntSet currentInputSet = SerNDefinition.getSetInput(currentCancellationSet.get(k));

					if(currentInputSet.containsNegative()){
						continue;
					}

					if(currentInputSet.contains(i)!=-1){
						noOfOutputsForThisForkTree++;
					}
				}
				ConstructionModule forkTreeForThisInputLine = GenerateCommonModules.createForkTree(noOfOutputsForThisForkTree);
				forkTrees.add(forkTreeForThisInputLine);

				/* Build the Fork tree */
				forkTreeForThisInputLine.setIdentifyingName("("+originalModuleDefinition.getInputName(i)+
						","+originalModuleDefinition.getStateName(j)+")"+forkTreeForThisInputLine.getIdentifyingName());
				ConsoleWindow.output("Created "+noOfOutputsForThisForkTree+"-way Fork instance named "+forkTreeForThisInputLine.getIdentifyingName()+" as "+
						"the input line "+originalModuleDefinition.getInputName(i)+" appears in "+noOfOutputsForThisForkTree+" different sets in state "+
						originalModuleDefinition.getStateName(j));

				/* Only bother adding a wire to this fork tree if the number of its output lines is 2 or more
				 * (otherwise we can just skip this fork tree as we are dealing with a 1-way Fork, i.e. a wire!) */
				if(noOfOutputsForThisForkTree>1){
					Wire connectForkTreeToSerNQ = new Wire(SerNQ.get(i).getOutput
							(SerNQ.get(i).getNoOfOutputs()-originalModuleDefinition.getNoOfStates()+j),forkTreeForThisInputLine.getInput(0));
					SerNQToForks.add(connectForkTreeToSerNQ);

					ConsoleWindow.output("Connected "+forkTreeForThisInputLine.getIdentifyingName()+" to "+SerNQ.get(i).getIdentifyingName()+"'s "+SerNQ.get(i).getOutput
							(SerNQ.get(i).getNoOfOutputs()-originalModuleDefinition.getNoOfStates()+j).getName()+" output line");
				}
				else{
					ConsoleWindow.output(forkTreeForThisInputLine.getIdentifyingName()+" is left unconnected as it is 0 or 1-way. It will be deleted at the end.");
				}
			}
		}

		/* Now we need to build the "cancellation network" on a state-by-state basis. These are collections of MxN Join columns
		 * which process input sets in the same state, and therefore are connected by wires. This is so that the "satisfied"
		 * column can remove/cancel any other signals which happen to be pending as a result of forking them to the "incorrect"
		 * (unsatisfied) MxN Join column */

		/* For every state of the module */
		for(int i=0;i<originalModuleDefinition.getNoOfStates();i++){

			ConsoleWindow.output("Building MxN Join columns for state "+originalModuleDefinition.getStateName(i));

			Vector<Wire> cancellationSetInterconnections = new Vector<Wire>();
			Vector<Vector<ConstructionModule>> columnsForState = new Vector<Vector<ConstructionModule>>();

			/* Retrieve the set of input sets appearing in this state, also referred to as the "cancellation set" */
			IntSet cancellationSet = SerNDefinition.getSetInputAppearancesForState(i).deepCopy();

			/* Strip out any duplicate sets (signified by a negative integer at the end of the set), as
			 * we do not build extra columns for equal sets (in the case of eq-arb modules). Recall that these sets
			 * are "arbitrated" between using Choice trees later on (see the thesis) */
			for(int j=cancellationSet.size()-1;j>=0;j--){
				if(SerNDefinition.getSetInput(cancellationSet.get(j)).containsNegative()){
					cancellationSet.remove(j);
				}
			}

			/* For each MxN Join in each MxN Join column in the cancellation set, we record the index of each other column
			 * (in the overall list of columns) which will need to cancel a signal pending on THIS MxN Join, should that column
			 * be satisfied (recall that satisfied columns need to then cancel signals pending on various MxN Joins in other columns) 
			 * These are calculated as we need to know how "big" to make each MxN Join, which is dependent on how many other "columns" need
			 * to cancel pending signals on it (should they be satisfied) */
			Vector<Vector<Vector<Integer>>> columnsWhichCancelEachColumn = new Vector<Vector<Vector<Integer>>>();

			/* In one-to-one correspondence with the above structure, we record which axis of the MxN Join needs a pending signal "cancelling",
			 * (the pending signal will always be remaining on the 0th input line of a particular axis.) If this value is true for a particular
			 * MxN Join, then it means that we need to cancel a signal pending on the 0th input of the vertical axis of this MxN Join, via a signal
			 * on the "next free" input (information which is stored in the module's specialAttributes and is incremented as the algorithm goes along) 
			 * of the horizontal axis. If the value is false then a signal on the 0th input of the horizontal axis needs cancelling via the vertical axis.*/
			Vector<Vector<Vector<Boolean>>> axisWhichNeedsCancellingForEachColumn = new Vector<Vector<Vector<Boolean>>>();

			/* For every input set in this cancellation set */
			for(int j=0;j<cancellationSet.size();j++){

				/* Retrieve the input set */
				IntSet currentInputSet=SerNDefinition.getSetInput(cancellationSet.get(j));

				ConsoleWindow.output("Building column for input set "+currentInputSet.printStringRepresentation(originalModuleDefinition.getInputNames())+
						" in state "+originalModuleDefinition.getStateName(i));

				Vector<ConstructionModule> MxNJoinColumn = new Vector<ConstructionModule>();

				/* The inner two most Vectors of the above defined columnsWhichCancelEachColumn - this variable
				 * stores the indexes of other columns in the cancellation set which cancel signals on each MxN Join
				 * in this column, should the other column be satisfied */
				Vector<Vector<Integer>> columnReferencesForEachJoin = new Vector<Vector<Integer>>();

				/* The corresponding values to the above of which axis needs cancelling for each MxN Join in this column */
				Vector<Vector<Boolean>> axisWhichNeedsCancellingForEachJoin = new Vector<Vector<Boolean>>();

				/* Now build the actual MxN Join column for the current input set in the current state */

				/* We need this number of MxN Joins in this particular column - recall that the kth MxN Join in a column
				 * represents the addition of the k+1th input of the input set to the already accumulated k inputs from
				 * the input set */
				int noOfJoinsForThisColumn = currentInputSet.size()-1;

				ConsoleWindow.output("This join requires "+noOfJoinsForThisColumn+" MxN Joins as the input set is size "+(noOfJoinsForThisColumn+1));

				/* For every one of the MxN Joins in this column */
				for(int k=0;k<noOfJoinsForThisColumn;k++){

					ConsoleWindow.output("Building "+k+"th MxN Join in this column");

					ConsoleWindow.output("Calculating size of M and N for this MxN Join");

					/* The list of indexes of other columns in the cancellation set which cancel signals on
					 * the current MxN Join, should the other set be satisfied */
					Vector<Integer> columnReferencesForThisJoin = new Vector<Integer>();

					/* The corresponding values to the above of which axis needs cancelling for this MxN Join */
					Vector<Boolean> axisWhichNeedsCancellingForThisJoin = new Vector<Boolean>();

					/* Signalling the horizontal axis of this MxN Join represents the successful accumulation of the
					* first k inputs from the input set */
					IntSet horizontalAxisAccumulatesInputIndexes = new IntSet();
					for(int l=0;l<=k;l++){
						horizontalAxisAccumulatesInputIndexes.add(currentInputSet.get(l));
					}

					ConsoleWindow.output("Horizontal axis of this MxN Join (M) represents the accumulation of the inputs "+
							horizontalAxisAccumulatesInputIndexes.printStringRepresentation(originalModuleDefinition.getInputNames()));

					/* Signalling the vertical axis of this MxN join represents the (k+1)th input from
					/* the set - to be joined with the previous k inputs (already joined together) */
					int verticalAxisRepresentsInputIndex = currentInputSet.get(k+1);

					ConsoleWindow.output("Vertical axis of this MxN Join (N) represents the addition of the input "+
							originalModuleDefinition.getInputName(verticalAxisRepresentsInputIndex));

					/* Given that we now know which inputs are being joined for this MxN Join, we need
					* to work out size of M and N, which depends on how many other columns need to cancel
					* pending signals on this MxN Join (should they be satisfied) */

					/* Recall that N is the vertical axis, and is used for cancelling the horizontal pending input
					 * The size of this axis is dependent on how many other input set sets in the cancellation set 
					 * contain the horizontal set of this MxN Join as a subset, but NOT horizontal set UNION vertical set, plus one
					 * (for the actual main vertical signal which this MxN Join synchronises) */

					/* Recall that M is the horizontal axis, and is used for cancelling the vertical pending input.
					 * The size of this axis is dependent on how many other input sets fail the above condition but contain
					 * the vertical singleton set of this MxN Join, plus one (for the actual main horizontal signal that this
					 * MxN Join synchronises) */

					int horizontalAxisSizeForThisJoin=1;
					int verticalAxisSizeForThisJoin=1;

					/* Iterate through every other input set in the same state (cancellation set) */
					for(int l=0;l<cancellationSet.size();l++){

						/* Don't compare the set against itself */
						if(l!=j){

							/* Retrieve the input set */
							IntSet otherInputSet = SerNDefinition.getSetInput(cancellationSet.get(l));

							ConsoleWindow.output("Checking if the MxN Join column corresponding to input set "+
									otherInputSet.printStringRepresentation(originalModuleDefinition.getInputNames())+" in state "+
									originalModuleDefinition.getStateName(i)+" would need to cancel any pending signals on this MxN Join");

							/* If the set represented by the horizontal axis of this MxN Join exists in the other set */
							if(horizontalAxisAccumulatesInputIndexes.subset(otherInputSet)){

								/* But the set represented by the vertical axis doesn't */
								if(otherInputSet.contains(verticalAxisRepresentsInputIndex)==-1){

									ConsoleWindow.output("The inputs represented by the signal that would pend on the horizontal axis (M) of "
											+ "this join ("+horizontalAxisAccumulatesInputIndexes.printStringRepresentation(originalModuleDefinition.getInputNames())+
											") are present in the other columns input set ("+otherInputSet.printStringRepresentation(originalModuleDefinition.getInputNames())+
											"), but the horizontal signal's corresponding inputs unioned with the vertical signal's (N) corresponding input ("
											+originalModuleDefinition.getInputName(verticalAxisRepresentsInputIndex)+") are not (i.e. the set "
											+horizontalAxisAccumulatesInputIndexes.setUnion(new IntSet(verticalAxisRepresentsInputIndex))
											.printStringRepresentation(originalModuleDefinition.getInputNames())+"), and hence if the other input set were signalled, a horizontal signal (M) "
											+ "would remain pending on this join indefinitely "
											+ "(never joining with a signal on N and continuing onwards). "
											+ "The other column would therefore have to cancel these signals via the vertical axis (N), "
											+ "and therefore we increase the size of this MxN Join's vertical axis (N) to accommodate this.");

									/* Add an input to N (to allow cancelling of the horizontal signal which may be left pending on this MxN Join) */
									verticalAxisSizeForThisJoin++;

									/* Record a link between the l-th column in the cancellation set to this MxN Join, with false as the axis record */
									columnReferencesForThisJoin.add(new Integer(l));
									axisWhichNeedsCancellingForThisJoin.add(new Boolean(false));
								}
							}

							/* If the set represented by the horizontal axis of this MxN Join does NOT exist in the other set, BUT the singleton set
							 * represented by the vertical axis is present */
							else if(otherInputSet.contains(verticalAxisRepresentsInputIndex)!=-1){

								ConsoleWindow.output("The input represented by the signal that would pend on the vertical axis (N) of "
										+ "this join ("
										+originalModuleDefinition.getInputName(verticalAxisRepresentsInputIndex)+") is present in the other column's input set ("
										+otherInputSet.printStringRepresentation(originalModuleDefinition.getInputNames())+
										"), but the horizontal signal's corresponding inputs ("+
										horizontalAxisAccumulatesInputIndexes.printStringRepresentation(originalModuleDefinition.getInputNames())+
										") are not, and hence if the other input set were signalled, a the vertical signal (N) "
										+ "would remain pending on this join indefinitely "
										+ "(never joining with a signal on M and continuing onwards). "
										+ "The other column would therefore have to cancel these signals via the horizontal axis (M), "
										+ "and therefore we increase the size of this MxN Join's horizontal axis (M) to accommodate this.");

								/* Add an input to M (to allow cancelling of the vertical signal which may be left pending on this MxN Join) */
								horizontalAxisSizeForThisJoin++;

								/* Record a link between the l-th column in the cancellation set to this MxN Join, with true as the axis record */
								columnReferencesForThisJoin.add(new Integer(l));
								axisWhichNeedsCancellingForThisJoin.add(new Boolean(true));
								break;
							}
							
							/* Neither only the set represented by the horizontal axis, nor only the set represented by the vertical axis exist in the
							 * other input set, and so we do not need to increase the size of this MxN Join */
							else{
								ConsoleWindow.output("Neither 1) ONLY the inputs represented by the horizontal axis (without the vertical signal) nor "
										+ "2) the input represented by the vertical axis and NOT ALL of the horizontal axis' inputs, "
										+ "are present in the other column's input set, and hence the other column does not cancel any signals on this MxN Join");
							}
						}
					}
					
					/* Build the current MxN Join module and store it along with any relevant information regarding which columns may cancel its pending signals */
					ConstructionModule currentJoin = GenerateCommonModules.createJoin(horizontalAxisSizeForThisJoin, verticalAxisSizeForThisJoin);

					currentJoin.setIdentifyingName("("+horizontalAxisAccumulatesInputIndexes.printStringRepresentation(originalModuleDefinition.getInputNames())+","+
							originalModuleDefinition.getInputName(verticalAxisRepresentsInputIndex)+")"+currentJoin.getIdentifyingName());
					MxNJoinColumn.add(currentJoin);
					columnReferencesForEachJoin.add(columnReferencesForThisJoin);
					axisWhichNeedsCancellingForEachJoin.add(axisWhichNeedsCancellingForThisJoin);
					
					ConsoleWindow.output("Built "+horizontalAxisSizeForThisJoin+"x"+verticalAxisSizeForThisJoin+" Join for this column "
					+currentJoin.getIdentifyingName()+" representing the joining of the set "+
							"("+horizontalAxisAccumulatesInputIndexes.printStringRepresentation(originalModuleDefinition.getInputNames())+","+
							originalModuleDefinition.getInputName(verticalAxisRepresentsInputIndex)+")");
				}
				
				/* Finish building the current MxN Join column and store it along with any relevant information regarding which columns 
				 * may cancel any of its MxN Join's pending signals */
				columnsForState.add(MxNJoinColumn);
				columnsWhichCancelEachColumn.add(columnReferencesForEachJoin);
				axisWhichNeedsCancellingForEachColumn.add(axisWhichNeedsCancellingForEachJoin);
			}
			MxNJoins.add(columnsForState);

			ConsoleWindow.output("Built all MxN Join columns for state "+originalModuleDefinition.getStateName(i));
			
			/* Now we can now link up the various modules with wires, which are relevant to the current state */

			/* We link Fork trees to MxN Columns
			 * we link MxN Join columns internally
			 * we link the columns together within this cancellation network
			 * we link "ends of" this cancellation networks to SerNQ' modules */

			ConsoleWindow.output("Building wires between and within these columns");
			
			/* For every input set (MxN Join column) in this state */
			for(int j=0;j<cancellationSet.size();j++){

				/* Retrieve the input set */
				IntSet currentInputSet=SerNDefinition.getSetInput(cancellationSet.get(j));

				ConsoleWindow.output("Connecting column corresponding to input set "+
				currentInputSet.printStringRepresentation(originalModuleDefinition.getInputNames())+" in state "+
						originalModuleDefinition.getStateName(i));
				
				int inputIndex = currentInputSet.get(0);

				/* Retrieve the correct Fork tree which sends a signal to the top-most MxN Join's horizontal axis */
				ConstructionModule firstInputForkTree=forkTrees.get((inputIndex*originalModuleDefinition.getNoOfStates())+i);

				ConsoleWindow.output("Retrieving "+firstInputForkTree.getIdentifyingName()+" which corresponds to first input: "+originalModuleDefinition.getInputName(inputIndex)+ 
						" in state "+originalModuleDefinition.getStateName(i));
				
				/* Retrieve the MxN Join column that we are dealing with */
				Vector<ConstructionModule> currentJoinColumnToLinkWithFork = columnsForState.get(j);
				ModuleOutputLine firstInputForkOrSerNQOutput;

				/* If the Fork tree is actually a "true" fork, i.e. it has 2 or more outputs, retrieve the next free output of it */
				if(firstInputForkTree.getNoOfOutputs()>1){
					firstInputForkOrSerNQOutput = firstInputForkTree.getOutput(firstInputForkTree.getAttribute(0));
					
					ConsoleWindow.output("Retrieving next free output of "+firstInputForkTree.getIdentifyingName()+" which is "+firstInputForkOrSerNQOutput.getName());
				}
				
				/* Otherwise skip the Fork tree, and retrieve the output of the SerNQ module (the Fork tree can simply be replaced by a wire) */
				else{
					firstInputForkOrSerNQOutput=SerNQ.get(inputIndex).getOutput(SerNQ.get(inputIndex).getNoOfOutputs()-originalModuleDefinition.getNoOfStates()+i);
					ConsoleWindow.output(firstInputForkTree.getIdentifyingName()+" is 0-way, so ignoring and retrieving the query output "+firstInputForkOrSerNQOutput.getName()+" of "+SerNQ.get(inputIndex).getIdentifyingName());
				}
				
				/* If the MxN Join column is greater than 0 (i.e. the current input set is not a singleton, and actually
				 * requires the joining of signals) */
				if(currentJoinColumnToLinkWithFork.size()>0){
					ConstructionModule firstJoinInColumn = currentJoinColumnToLinkWithFork.get(0);
					ModuleInputLine firstJoinInput = firstJoinInColumn.getInput(0);

					/* Link the output of the Fork tree (or SerNQ if ignoring the Fork tree) to the input of the top MxN Join's in this column's topmost horizontal input */
					Wire forkToFirstJoin = new Wire(firstInputForkOrSerNQOutput,firstJoinInput);
					cancellationSetInterconnections.add(forkToFirstJoin);
					
					ConsoleWindow.output("Linked output "+firstInputForkOrSerNQOutput.getName()+" to top-most MxN Join in column "+
							firstJoinInColumn.getIdentifyingName()+"'s top-most horizontal input "+firstJoinInput.getName() +" so first input of set is dealt with");
				}
				firstInputForkTree.incrementAttribute(0);

				/* For every remaining input of the input set */
				for(int k=0;k<currentInputSet.size()-1;k++){

					/* Get the next MxN Join in this column, (whose vertical axis corresponds to this input) */
					ConstructionModule currentJoinInInputSetColumn = currentJoinColumnToLinkWithFork.get(k);
					
					/* Retrieve the SerNQ corresponding to this input */
					inputIndex=currentInputSet.get(k+1);

					ConsoleWindow.output("Retrieving "+k+"th MxN Join in this column: "+
							currentJoinInInputSetColumn.getIdentifyingName()+" and "+(k+1)+
							"th input from the set: "+originalModuleDefinition.getInputName(inputIndex));
					
					/* Find the appropriate Fork tree which distributes this input to various MxN Join columns */
					ConstructionModule currentInputForkTree=forkTrees.get((inputIndex*originalModuleDefinition.getNoOfStates())+i);

					ConsoleWindow.output("Retrieving "+currentInputForkTree.getIdentifyingName()+" which corresponds to current input: "+originalModuleDefinition.getInputName(inputIndex)+ 
							" in state "+originalModuleDefinition.getStateName(i));
					
					ModuleOutputLine currentInputForkOrSerNQOutput;

					/* Similarly to above, if the Fork tree is actually a "true" fork, i.e. it has 2 or more outputs, retrieve the next free output of it */
					if(currentInputForkTree.getNoOfOutputs()>1){
						currentInputForkOrSerNQOutput = currentInputForkTree.getOutput(currentInputForkTree.getAttribute(0));
						currentInputForkTree.incrementAttribute(0);
						ConsoleWindow.output("Retrieving next free output of "+currentInputForkTree.getIdentifyingName()+" which is "+currentInputForkOrSerNQOutput.getName());
					}
					
					/* Otherwise skip the Fork tree, and retrieve the output of the input's SerNQ module (the Fork tree can simply be replaced by a wire) */
					else{
						currentInputForkOrSerNQOutput=SerNQ.get(inputIndex).getOutput
								(SerNQ.get(inputIndex).getNoOfOutputs()-originalModuleDefinition.getNoOfStates()+i);

						ConsoleWindow.output("Fork is 0-way, so ignoring and retrieving the query output "+currentInputForkOrSerNQOutput.getName()+" of "+SerNQ.get(inputIndex).getIdentifyingName());
					}
					
					/* Link the output of the Fork tree (or SerNQ if ignoring the Fork tree) to the current MxN Join in this column's topmost vertical input */
					ModuleInputLine currentJoinFirstVerticalInput = currentJoinInInputSetColumn.getInput(currentJoinInInputSetColumn.getAttribute(0));
					
					Wire currentForkOrSerNQToCurrentJoin = new Wire(currentInputForkOrSerNQOutput,
							currentJoinFirstVerticalInput);
					cancellationSetInterconnections.add(currentForkOrSerNQToCurrentJoin);
					ConsoleWindow.output("Linked output "+currentInputForkOrSerNQOutput.getName()+" to current MxN Join in column "+
							currentJoinInInputSetColumn.getIdentifyingName()+"'s top-most vertical input "+currentJoinFirstVerticalInput.getName());

					/* If we are not on the last MxN Join, then link the (0,0) output to the 0th horizontal input of the next MxN Join in this column */
					if(k<currentInputSet.size()-2){
						Wire outputToNextJoin = new Wire(currentJoinInInputSetColumn.getOutput(0),currentJoinColumnToLinkWithFork.get(k+1).getInput(0));
						cancellationSetInterconnections.add(outputToNextJoin);
						ConsoleWindow.output("This is not the bottom-most join of the current column, so we link first output "+
								currentJoinInInputSetColumn.getOutput(0).getName()+" to the first horizontal input "+ currentJoinColumnToLinkWithFork.get(k+1).getInput(0).getName()
								+ " of the next MxN Join "+currentJoinColumnToLinkWithFork.get(k+1).getIdentifyingName());
					}
				}

				/* We now need to take the (0,0) output of this MxN Join column, and build a "cancellation network" for it, 
				 * meaning we connect a path from it to other MxN Join columns in this cancellation set, 
				 * in order to cancel any pending inputs which may be present on other MxN Joins in other MxN Join columns
				 * as a result of signalling this input set */
				

				ModuleOutputLine currentOutputToConnect;

				/* If the number of MxN Joins in this MxN Joins column is 0 (i.e. the input set is a singleton), then take the output of the SerNQ
				 * for this input line in this state (it will also necessarily skip the Fork tree for this input, as a singleton input set cannot
				 * be a member of any other input set in this state, due to the module being non-arb or eq-arb) */
				if(currentJoinColumnToLinkWithFork.size()==0){
					currentOutputToConnect=firstInputForkOrSerNQOutput;
					ConsoleWindow.output("There are no MxN Joins in this column, so taking the output "+currentOutputToConnect.getName()+
							" of "+currentOutputToConnect.getModule().getIdentifyingName());
				}
				
				/* Else take the (0,0) output of this MxN Join column's bottommost MxN Join */
				else{
					currentOutputToConnect=currentJoinColumnToLinkWithFork.get(currentJoinColumnToLinkWithFork.size()-1).getOutput(0);
					ConsoleWindow.output("Taking the first output "+currentOutputToConnect.getName()+" of the bottom-most MxN Join "
							+currentOutputToConnect.getModule().getIdentifyingName()+" of this column");
				}

				/* For every "other" MxN Join column in this cancellation set */
				for(int k=0;k<columnsWhichCancelEachColumn.size();k++){
					if(k!=j){
	
						/* Retrieve the list of "cancellation" references for this current "other" column in the cancellation set */
						Vector<Vector<Integer>> columnsWhichCancelThisOtherColumn = columnsWhichCancelEachColumn.get(k);

						/* For every MxN Join in the current "other" column */
						for(int l=0;l<columnsWhichCancelThisOtherColumn.size();l++){

							/* Retrieve the list of "cancellation" references for this MxN Join, i.e. the list of columns which will
							 * need to cancel signals on this MxN Join if they are satisfied */
							Vector<Integer> columnsWhichCancelThisOtherColumnsCurrentJoin = columnsWhichCancelThisOtherColumn.get(l);

							/* For every MxN Join column which is said to cancel signals on this MxN Join */
							for(int m=0;m<columnsWhichCancelThisOtherColumnsCurrentJoin.size();m++){

								/* Retrieve the value of the MxN Join column which "cancels" siganls on this MxN Join */
								int cancellingColumnIndex = columnsWhichCancelThisOtherColumnsCurrentJoin.get(m).intValue();

								/* If the value of the MxN Join column is the one that we are building the cancellation
								 *  network for (i.e. the current (not "other") MxN Join column does in fact cancel signals on this 
								 *  "other" MxN Join column's current MxN Join */
								if(cancellingColumnIndex==j){
									
									ConsoleWindow.output("MxN Column for input set "+SerNDefinition.getSetInput(cancellationSet.get(k)).printStringRepresentation(originalModuleDefinition.getInputNames())
									+" in state "+originalModuleDefinition.getStateName(i)+" contains MxN Joins that have signals that need cancelling by the current input set");

									/* Retrieve the MxN Join that needs signals cancelling */
									ConstructionModule joinToCancelSignalOn = MxNJoins.get(i).get(k).get(l);
									
									boolean cancelVerticalViaHorizontal = axisWhichNeedsCancellingForEachColumn.get(k).get(l).get(m).booleanValue();

									ModuleInputLine inputToSignalToCancelSignalOnJoin;

									/* Cancel the pending 0th horizontal input's signal via the next free vertical input if necessary */
									if(cancelVerticalViaHorizontal==false){
										
										ConsoleWindow.output("MxN Join "+joinToCancelSignalOn.getIdentifyingName()+" needs horizontal signal cancelling via free vertical axis input");
										
										inputToSignalToCancelSignalOnJoin = joinToCancelSignalOn.getInput
												(joinToCancelSignalOn.getAttribute(0)+joinToCancelSignalOn.getAttribute(2)+1);
										cancellationSetInterconnections.add(new Wire(currentOutputToConnect,inputToSignalToCancelSignalOnJoin));
										
										ConsoleWindow.output("Connecting output"+currentOutputToConnect.getName()+" of "+currentOutputToConnect.getModule().getIdentifyingName()+" to input "+
										inputToSignalToCancelSignalOnJoin.getName()+" of "+inputToSignalToCancelSignalOnJoin.getModule().getIdentifyingName());
										
										currentOutputToConnect=joinToCancelSignalOn.getOutput(joinToCancelSignalOn.getAttribute(0) *((joinToCancelSignalOn.getAttribute(2)+1)));
										
										ConsoleWindow.output("Corresponding output of current MxN Join "+currentOutputToConnect.getModule().getIdentifyingName()+
												" to continue from is "+currentOutputToConnect.getName());
										
										joinToCancelSignalOn.incrementAttribute(2);
									}
									
									/* Otherwise cancel the 0th vertical input's signal via the next free horizontal input */
									else{
										
										ConsoleWindow.output("MxN Join "+joinToCancelSignalOn.getIdentifyingName()+" needs vertical signal cancelling via free horizontal axis input");
										
										inputToSignalToCancelSignalOnJoin = joinToCancelSignalOn.getInput(joinToCancelSignalOn.getAttribute(1)+1);
										cancellationSetInterconnections.add(new Wire(currentOutputToConnect,inputToSignalToCancelSignalOnJoin));
				

										ConsoleWindow.output("Connecting output"+currentOutputToConnect.getName()+" of "+currentOutputToConnect.getModule().getIdentifyingName()+" to input "+
										inputToSignalToCancelSignalOnJoin.getName()+" of "+inputToSignalToCancelSignalOnJoin.getModule().getIdentifyingName());
										
										currentOutputToConnect=joinToCancelSignalOn.getOutput((joinToCancelSignalOn.getAttribute(1)+1));
										
										ConsoleWindow.output("Corresponding output of current MxN Join "+currentOutputToConnect.getModule().getIdentifyingName()+
												" to continue from is "+currentOutputToConnect.getName());
										
										joinToCancelSignalOn.incrementAttribute(1);
									}
								}
							}
						}
					}
				}
				
				ConsoleWindow.output("Finished cancelling any pending signals as a result of signalling input set "+
					currentInputSet.printStringRepresentation(originalModuleDefinition.getInputNames()));
				
				/* After "cancelling" pending signals in other MxN Join columns in the cancellation set, we need to work out whether
				 * we need to add Choice trees in order to make a non-deterministic choice (if the original module is eq-arb)
				 * between multiple inputs of SerN/SerNQ/SerNQ' which map to this single input set */

				/* Calculate the number of "choices" we need to decide between, by seeing if the current input set is a
				 * subset of other input sets in the same state. Note that the Set Notation module is not arb, so there will not be any "true"
				 * subsets between input sets in the same state. However this will identify "relabellings" of the input set, which recall we performed
				 * by placing negative integers into input sets which were already present in actions in the same state. E.g. {0,1,2}
				 * is a subset of {0,1,2,-1}, {0,1,2,-2} etc. Hence we can use this method to count the number of "choices" that need
				 * to be considered. */
					int numberOfEqArbChoicesForThisInputSet = 0;
					IntSet currentInputSetAppearancesInThisState = SerNDefinition.getSetInputAppearancesForState(i);
					IntSet indexOfSerNQPrimeOfEachChoice= new IntSet();
					for(int k=0;k<currentInputSetAppearancesInThisState.size();k++){
						IntSet setToCheckIsNegativeSuperSet = SerNDefinition.getSetInput(currentInputSetAppearancesInThisState.get(k));
		
						if(currentInputSet.subset(setToCheckIsNegativeSuperSet)){
							numberOfEqArbChoicesForThisInputSet++;
							
							/* Also retrieve the SerNQ' module in stage 1 which corresponds to this relabelling of the input set (alternatively
							 * the relevant input set of SerN/SerNQ/SerNQ' found as a result of following this particular outcome of the 
							 * non-deterministic choice) */
							indexOfSerNQPrimeOfEachChoice.add(currentInputSetAppearancesInThisState.get(k));
						}
					}
				
				/* If there is only one instance of this input set in this state (always true if the module is non-arb, and when this 
				 * method is called to build the reversible stage 2 as the module is necessarily non-b-arb if this method is called 
				 * to perform stage 2), then connect the bottom of the "last cancelled" MxN Join column, or the bottom of this MxN Join column
				 * itself if no "cancellations" took place, (or SerNQ if the input set is a singleton) straight to the SerNQ' module for this input set */
				if(numberOfEqArbChoicesForThisInputSet==1 || !inputStage){
					ModuleInputLine receivingInputOfOnlySerNQPrime = SerNQPrime.get(indexOfSerNQPrimeOfEachChoice.get(0)).getInput(SerNDefinition.getNoOfInputs()+i);
					cancellationSetInterconnections.add(new Wire(currentOutputToConnect,receivingInputOfOnlySerNQPrime));
					
					ConsoleWindow.output("Connecting last output "+currentOutputToConnect.getName()+" of "+
							currentOutputToConnect.getModule().getIdentifyingName()+ " to this input set's SerNQ' module "+
							receivingInputOfOnlySerNQPrime.getModule().getIdentifyingName()+" input "+receivingInputOfOnlySerNQPrime.getName());
				}

				/* Else connect the bottom of the "last cancelled" MxN Join column, or the bottom of this MxN Join column
				 * itself if no "cancellations" took place, (or SerNQ if the input set is a singleton) to the input line of a new Choice module
				 * of the appropriate size (to arbitrate between the multiple instances of this input set in the original definition of
				 * the Set Notation module) */
				else{
					
					ConsoleWindow.output("Input set is part of a "+numberOfEqArbChoicesForThisInputSet+"-way eq-arb non-deterministic choice");
					
					ConstructionModule currentInputSetChoice = GenerateCommonModules.createChoiceTree(numberOfEqArbChoicesForThisInputSet);
					
					ConsoleWindow.output("Building "+numberOfEqArbChoicesForThisInputSet+"-way Choice module");
					
					cancellationSetInterconnections.add(new Wire(currentOutputToConnect,currentInputSetChoice.getInput(0)));
					
					ConsoleWindow.output("Connecting last output "+currentOutputToConnect.getName()+" of "+
							currentOutputToConnect.getModule().getIdentifyingName()+ " to this input set's Choice module "+
							currentInputSetChoice.getIdentifyingName()+"'s only input "+currentInputSetChoice.getInput(0).getName());
					
					/* For every output line of this Choice module, now connect it to the SerNQ' module corresponding to an occurrence 
					 * of the this input set in the same state of the Set Notation module (i.e. for each input of SerNQ' which corresponds to
					 * this input set */
					for(int k=0;k<numberOfEqArbChoicesForThisInputSet;k++){

						ModuleInputLine receivingInputOfCurrentChoicesSerNQPrime = SerNQPrime.get(indexOfSerNQPrimeOfEachChoice.get(k)).getInput(SerNDefinition.getNoOfInputs()+i);
						cancellationSetInterconnections.add(new Wire(currentInputSetChoice.getOutput(k),receivingInputOfCurrentChoicesSerNQPrime));
						
						ConsoleWindow.output("Linking "+k+"th output "+currentInputSetChoice.getOutput(k).getName()+" of "+currentInputSetChoice.getIdentifyingName()+
								" to "+receivingInputOfCurrentChoicesSerNQPrime.getModule().getIdentifyingName()+"'s input "+receivingInputOfCurrentChoicesSerNQPrime.getName());
						
					}
					choiceModuleInstances.add(currentInputSetChoice);
				}
			}
			
			/* Add all the wires associated with the processing of this input set in this state to the collection */
			MxNJoinInterconnects.add(cancellationSetInterconnections);
		}

		/* Remove Fork trees that are only sized 1 (they have been ignored by the algorithm in place of wires */
		for(int i=forkTrees.size()-1;i>=0;i--){
			if(forkTrees.get(i).getNoOfOutputs()<2){
				ConsoleWindow.output("Removing "+forkTrees.get(i).getIdentifyingName()+" as it is unused");
				forkTrees.remove(i);
			}
		}
	}

	/* This generates the irreversible version of stage 2 in the case that the Set Notation module is b-arb. It creates
	 * a Fork tree module for each output set of the Set Notation module, and a Merge tree module for each output line of
	 * the Set Notation module */
	private static void generateIrreversibleStage2() {

		/* For every output set of the original Set Notation module */
		for(int i=0;i<SerNDefinition.getNoOfOutputs();i++){

			/* Create a Fork tree of the same size */
			ConstructionModule forkForCurrentOutputSet = GenerateCommonModules.createForkTree(SerNDefinition.getSetOutput(i).size());
			forkForCurrentOutputSet.setIdentifyingName(SerNDefinition.getSetOutput(i).printStringRepresentation
					(originalModuleDefinition.getOutputNames())+forkForCurrentOutputSet.getIdentifyingName());
			irreversibleOutputStageForkTreeModuleInstances.add(forkForCurrentOutputSet);
			
			ConsoleWindow.output("Created "+forkForCurrentOutputSet.getIdentifyingName()+" corresponding to output set " +
					SerNDefinition.getSetOutput(i).printStringRepresentation(originalModuleDefinition.getOutputNames()));
		}

		/* For every output line of the original Set Notation module */
		for(int i=0;i<originalModuleDefinition.getNoOfOutputs();i++){

			/* Count how many output sets of the original module it appears in */
			int noOfInputSetsContainingCurrentOutput=0;
			for(int j=0;j<SerNDefinition.getNoOfOutputs();j++){
				if(SerNDefinition.getSetOutput(j).contains(i)!=-1){
					noOfInputSetsContainingCurrentOutput++;
				}
			}

			/* Create a Merge tree of that size */
			ConstructionModule mergeForCurrentOutput = GenerateCommonModules.createMergeTree(noOfInputSetsContainingCurrentOutput);

			mergeForCurrentOutput.setIdentifyingName("("+originalModuleDefinition.getOutputName(i)+")"+mergeForCurrentOutput.getIdentifyingName());
			irreversibleOutputStageMergeTreeModuleInstances.add(mergeForCurrentOutput);	
			
			ConsoleWindow.output("Created "+mergeForCurrentOutput.getIdentifyingName()+" corresponding to output line " +
					originalModuleDefinition.getOutputName(i));
		}

		/* Now for each Fork tree (output set of the original module */
		for(int i=0;i<irreversibleOutputStageForkTreeModuleInstances.size();i++){
			ConstructionModule forkForCurrentOutputSet = irreversibleOutputStageForkTreeModuleInstances.get(i);
			IntSet currentOutputSet = SerNDefinition.getSetOutput(i);
			
			/* For each output line in the output set corresponding to this Fork tree, connect the output of the Fork tree corresponding to
			 * this output line to the next free input of the Merge tree corresponding to the same output line */
			for(int j=0;j<forkForCurrentOutputSet.getNoOfOutputs();j++){
				int currentOutput = currentOutputSet.get(j);
				ConstructionModule currentOutputMerge=irreversibleOutputStageMergeTreeModuleInstances.get(currentOutput);
				Wire forkToMerge= new Wire(forkForCurrentOutputSet.getOutput(forkForCurrentOutputSet.getAttribute(0)),currentOutputMerge.getInput(currentOutputMerge.getAttribute(0)));
				irreversibleOutputStageInterconnections.add(forkToMerge);
				
				ConsoleWindow.output("Connected "+forkForCurrentOutputSet.getOutput(forkForCurrentOutputSet.getAttribute(0)).getName()+" of "+ 
					forkForCurrentOutputSet.getIdentifyingName()+" to input " + currentOutputMerge.getInput(currentOutputMerge.getAttribute(0)) + 
					" of "+currentOutputMerge.getIdentifyingName());
				
				forkForCurrentOutputSet.incrementAttribute(0);
				currentOutputMerge.incrementAttribute(0);
				
			}
		}
	}

	/* This is the "update stage" of the algorithm. It is effectively connects the two stages together, in a different way depending on
	 * whether the reversible or irreversible stage 2 has been constructed. */
	private static void generateUpdateStage() {
		
		/* The version of the "update stage" in the case of the irreversible stage 2 construction, when the original module is b-arb */
		if(originalModuleDefinition.checkBarb()){
			
			/* We build Fork trees which link each SerNQ' of stage 1 to all SerNQ/SerNQ' in stage 1 
			* and then Join trees which link each identical output from SerNQ/SerNQ' of stage 1 to the appropriate Fork tree in stage 2 */

			ConsoleWindow.output("Generating b-arb style update stage, which forks to only SerNQ and SerNQ' in input stage");
			
			/* For every SerNQ' in stage 1, build a Fork tree whose output size is the total number of all SerNQ and SerNQ' in stage 1,
			 * and then connect the "q" output of this SerNQ' to the input of this Fork tree. Finally, for each output of the Fork tree,
			 * connect it to the input of a SerNQ/SerNQ' in stage 1 which corresponds to this SerNQ' */
			for(int i=0;i<SerNDefinition.getNoOfInputs();i++){
				int numberOfOutputsOfCurrentInputSetFork = SerNQModuleInstances.size()+SerNQPrimeModuleInstances.size();
				ConstructionModule forkTreeToForkToAllAuxiliaries = GenerateCommonModules.createForkTree(numberOfOutputsOfCurrentInputSetFork);
				
				ConsoleWindow.output("Creating "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+
						" to link "+SerNQPrimeModuleInstances.get(i).getIdentifyingName() +" to all "+numberOfOutputsOfCurrentInputSetFork +
						" SerNQ and SerNQ' modules in the input stage");
				
				updateStageForkTreeModuleInstances.add(forkTreeToForkToAllAuxiliaries);
				forkTreeToForkToAllAuxiliaries.setIdentifyingName("(I"+(i+1)+")"+forkTreeToForkToAllAuxiliaries.getIdentifyingName());
				Wire currentInputSetSerNQPrimeToCurrentFork = new Wire(SerNQPrimeModuleInstances.get(i).getOutput(SerNQPrimeModuleInstances.get(i).getNoOfOutputs()-1),forkTreeToForkToAllAuxiliaries.getInput(0));
				updateStageInterconnections.add(currentInputSetSerNQPrimeToCurrentFork);
				
				ConsoleWindow.output("Connected output "+SerNQPrimeModuleInstances.get(i).getOutput(SerNQPrimeModuleInstances.get(i).getNoOfOutputs()-1).getName()+
						" of "+SerNQPrimeModuleInstances.get(i).getIdentifyingName()+" to only input "+forkTreeToForkToAllAuxiliaries.getInput(0).getName()+" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName());
				
				for(int j=0;j<SerNQModuleInstances.size();j++){
					Wire currentForkToCurrentSerNQ = new Wire(forkTreeToForkToAllAuxiliaries.getOutput(j),SerNQModuleInstances.get(j).getInput(i));
					updateStageInterconnections.add(currentForkToCurrentSerNQ);
					
					ConsoleWindow.output("Connected output "+forkTreeToForkToAllAuxiliaries.getOutput(j).getName()+
							" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+" to input "+SerNQModuleInstances.get(j).getInput(i).getName()+" of "+SerNQModuleInstances.get(j).getIdentifyingName());
				}
				forkTreeToForkToAllAuxiliaries.setAttribute(0,SerNQModuleInstances.size());
				for(int j=0;j<SerNQPrimeModuleInstances.size();j++){
					Wire currentForkToCurrentSerNQPrime= new Wire(forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)),SerNQPrimeModuleInstances.get(j).getInput(i));
					updateStageInterconnections.add(currentForkToCurrentSerNQPrime);
					
					ConsoleWindow.output("Connected output "+forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)).getName()+
							" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+" to input "+SerNQPrimeModuleInstances.get(j).getInput(i).getName()+" of "+SerNQPrimeModuleInstances.get(j).getIdentifyingName());
					
					forkTreeToForkToAllAuxiliaries.incrementAttribute(0);
				}
			}

			/* For every Fork tree in stage 2, build a Join tree whose input size is the total number of all SerNQ and SerNQ' in stage 1,
			 * and then connect the output of this Join tree to the input of this Fork tree. Finally, for each input of the Join tree,
			 * connect it to the output of a SerNQ/SerNQ' in stage 1 which corresponds to this Fork tree (output set) */
			for(int i=0;i<SerNDefinition.getNoOfOutputs();i++){
				int numberOfInputsOfCurrentOutputSetJoin = SerNQModuleInstances.size()+SerNQPrimeModuleInstances.size();
				ConstructionModule joinTreeToJoinFromAllAuxiliaries = GenerateCommonModules.createJoinTree(numberOfInputsOfCurrentOutputSetJoin);
				updateStageJoinTreeModuleInstances.add(joinTreeToJoinFromAllAuxiliaries);

				ConsoleWindow.output("Creating "+numberOfInputsOfCurrentOutputSetJoin+"-way Join Tree Network to link all SerNQ and SerNQ' modules in the input stage to "
						+irreversibleOutputStageForkTreeModuleInstances.get(i).getIdentifyingName() +" in the irreversible output stage");
				
				for(int j=0;j<SerNQModuleInstances.size();j++){
					Wire currentSerNQToJoinTree = new Wire(SerNQModuleInstances.get(j).getOutput(i),joinTreeToJoinFromAllAuxiliaries.getInput(j));
					updateStageInterconnections.add(currentSerNQToJoinTree);
					
					ConsoleWindow.output("Connected output "+SerNQModuleInstances.get(j).getOutput(i).getName()+
							" of "+SerNQModuleInstances.get(j).getIdentifyingName()+" to input "+joinTreeToJoinFromAllAuxiliaries.getInput(j).getName()+
							" of "+joinTreeToJoinFromAllAuxiliaries.getInput(j).getModule().getIdentifyingName()+ " (input to Join Tree sub-circuit)");
				}
				joinTreeToJoinFromAllAuxiliaries.setAttribute(0,SerNQModuleInstances.size());
				for(int j=0;j<SerNQPrimeModuleInstances.size();j++){
					Wire currentSerNQPrimeToJoinTree= new Wire(SerNQPrimeModuleInstances.get(j).getOutput(i),joinTreeToJoinFromAllAuxiliaries.getInput(j+SerNQModuleInstances.size()));
					updateStageInterconnections.add(currentSerNQPrimeToJoinTree);
					
					ConsoleWindow.output("Connected output "+SerNQPrimeModuleInstances.get(j).getOutput(i).getName()+
							" of "+SerNQPrimeModuleInstances.get(j).getIdentifyingName()+" to input "+joinTreeToJoinFromAllAuxiliaries.getInput(j+SerNQModuleInstances.size()).getName()+
							" of "+joinTreeToJoinFromAllAuxiliaries.getInput(j+SerNQModuleInstances.size()).getModule().getIdentifyingName()+ " (input to Join Tree sub-circuit)");
					
					joinTreeToJoinFromAllAuxiliaries.incrementAttribute(0);
				}			
				
				ModuleOutputLine outputFromJoinTree=joinTreeToJoinFromAllAuxiliaries.getOutput(0);
				ConstructionModule forkTreeForOutputSet=irreversibleOutputStageForkTreeModuleInstances.get(i);
				ModuleInputLine inputToForkTree =forkTreeForOutputSet.getInput(0);
				Wire joinTreeToOutputSetFork = new Wire(outputFromJoinTree,inputToForkTree);
				updateStageInterconnections.add(joinTreeToOutputSetFork);

				ConsoleWindow.output("Connected output "+outputFromJoinTree.getName()+
						" of "+outputFromJoinTree.getModule().getIdentifyingName()+" (output from Join Tree sub-circuit) to only input "
						+inputToForkTree.getName()+" of "+forkTreeForOutputSet.getIdentifyingName());
			}
		}
		
		/* The version of the "update stage" in the case of the reversible stage 2 construction, when the original module is non-b-arb */
		else{
			
			ConsoleWindow.output("Generating non-b-arb style update stage, which updates SerNQ and SerNQ' in input and output stage");
			
			/* We build Fork trees which link each SerNQ' of stage 1 to all SerNQ/SerNQ' in stage 1 and stage 2
			 * and then Join trees which link each identical output from SerNQ/SerNQ' of stage 1 and stage 2 to the 
			 * appropriate SerNQ in stage 2's "q" input */

			/* For every SerNQ' in stage 1, build a Fork tree whose output size is the total number of all SerNQ and SerNQ' in stage 1 and stage 2,
			 * and then connect the "q" output of this SerNQ' to the input of this Fork tree. Finally, for each output of the Fork tree,
			 * connect it to the input of a SerNQ/SerNQ' in stage 1 or stage 2 which corresponds to this SerNQ' */
			for(int i=0;i<SerNDefinition.getNoOfInputs();i++){
				int numberOfOutputsOfCurrentInputSetFork = SerNQModuleInstances.size()+SerNQPrimeModuleInstances.size() + outputSerNQPrimeModuleInstances.size()+outputSerNQModuleInstances.size();
				ConstructionModule forkTreeToForkToAllAuxiliaries = GenerateCommonModules.createForkTree(numberOfOutputsOfCurrentInputSetFork);
				
				ConsoleWindow.output("Creating "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+
						" to link "+SerNQPrimeModuleInstances.get(i).getIdentifyingName() +" to all "+numberOfOutputsOfCurrentInputSetFork +
						" SerNQ and SerNQ' modules in the input and output stages");
				
				updateStageForkTreeModuleInstances.add(forkTreeToForkToAllAuxiliaries);
				forkTreeToForkToAllAuxiliaries.setIdentifyingName("(I"+(i+1)+")"+forkTreeToForkToAllAuxiliaries.getIdentifyingName());
				Wire currentInputSetSerNQPrimeToCurrentFork = new Wire(SerNQPrimeModuleInstances.get(i).getOutput(SerNQPrimeModuleInstances.get(i).getNoOfOutputs()-1),forkTreeToForkToAllAuxiliaries.getInput(0));
				updateStageInterconnections.add(currentInputSetSerNQPrimeToCurrentFork);
			
				ConsoleWindow.output("Connected output "+SerNQPrimeModuleInstances.get(i).getOutput(SerNQPrimeModuleInstances.get(i).getNoOfOutputs()-1).getName()+
						" of "+SerNQPrimeModuleInstances.get(i).getIdentifyingName()+" to only input "+forkTreeToForkToAllAuxiliaries.getInput(0).getName()+" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName());
				
				for(int j=0;j<SerNQModuleInstances.size();j++){
					Wire currentForkToCurrentSerNQ = new Wire(forkTreeToForkToAllAuxiliaries.getOutput(j),SerNQModuleInstances.get(j).getInput(i));
					forkTreeToForkToAllAuxiliaries.incrementAttribute(0);
					updateStageInterconnections.add(currentForkToCurrentSerNQ);
				
					ConsoleWindow.output("Connected output "+forkTreeToForkToAllAuxiliaries.getOutput(j).getName()+
							" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+" to input "+SerNQModuleInstances.get(j).getInput(i).getName()+" of "+SerNQModuleInstances.get(j).getIdentifyingName());
				}
				for(int j=0;j<SerNQPrimeModuleInstances.size();j++){
					Wire currentForkToCurrentSerNQPrime= new Wire(forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)),SerNQPrimeModuleInstances.get(j).getInput(i));
					updateStageInterconnections.add(currentForkToCurrentSerNQPrime);
					
					ConsoleWindow.output("Connected output "+forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)).getName()+
							" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+" to input "+SerNQPrimeModuleInstances.get(j).getInput(i).getName()+" of "+SerNQPrimeModuleInstances.get(j).getIdentifyingName());
				
					forkTreeToForkToAllAuxiliaries.incrementAttribute(0);
				}
				for(int j=0;j<outputSerNQModuleInstances.size();j++){
					Wire currentForkToCurrentOutputStageSerNQ = new Wire(forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)),outputSerNQModuleInstances.get(j).getInput(i));
					updateStageInterconnections.add(currentForkToCurrentOutputStageSerNQ);
					
					ConsoleWindow.output("Connected output "+forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)).getName()+
							" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+" to input "+outputSerNQModuleInstances.get(j).getInput(i).getName()+" of "+outputSerNQModuleInstances.get(j).getIdentifyingName());
					
					forkTreeToForkToAllAuxiliaries.incrementAttribute(0);
				}
				for(int j=0;j<outputSerNQPrimeModuleInstances.size();j++){
					Wire currentForkToCurrentOutputStageSerNQPrime = new Wire(forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)),outputSerNQPrimeModuleInstances.get(j).getInput(i));
					updateStageInterconnections.add(currentForkToCurrentOutputStageSerNQPrime);
					
					ConsoleWindow.output("Connected output "+forkTreeToForkToAllAuxiliaries.getOutput(forkTreeToForkToAllAuxiliaries.getAttribute(0)).getName()+
							" of "+forkTreeToForkToAllAuxiliaries.getIdentifyingName()+" to input "+outputSerNQPrimeModuleInstances.get(j).getInput(i).getName()+" of "+outputSerNQPrimeModuleInstances.get(j).getIdentifyingName());
					
					forkTreeToForkToAllAuxiliaries.incrementAttribute(0);
				}
			}

			/* For every SerNQ tree in stage 2, build a Join tree whose input size is the total number of all SerNQ and SerNQ' in stage 1 and stage 2,
			 * and then connect the output of this Join tree to the "q" input of this SerNQ tree. Finally, for each input of the Join tree,
			 * connect it to the output of a SerNQ/SerNQ' in stage 1 or stage 2 which corresponds to this SerNQ' (output set) */
			for(int i=0;i<SerNDefinition.getNoOfOutputs();i++){
				int numberOfInputsOfCurrentOutputSetJoin = SerNQModuleInstances.size()+SerNQPrimeModuleInstances.size() + outputSerNQPrimeModuleInstances.size()+outputSerNQModuleInstances.size();
				ConstructionModule joinTreeToJoinFromAllAuxiliaries = GenerateCommonModules.createJoinTree(numberOfInputsOfCurrentOutputSetJoin);

				ConsoleWindow.output("Creating "+numberOfInputsOfCurrentOutputSetJoin+
						"-way Join Tree Network to link all SerNQ and SerNQ' modules in the input and output stages to "+outputSerNQModuleInstances.get(i).getIdentifyingName());
				
				updateStageJoinTreeModuleInstances.add(joinTreeToJoinFromAllAuxiliaries);
				for(int j=0;j<SerNQModuleInstances.size();j++){
					Wire currentSerNQToJoinTree = new Wire(SerNQModuleInstances.get(j).getOutput(i),joinTreeToJoinFromAllAuxiliaries.getInput(j));
					updateStageInterconnections.add(currentSerNQToJoinTree);
					
					ConsoleWindow.output("Connected output "+SerNQModuleInstances.get(j).getOutput(i).getName()+
							" of "+SerNQModuleInstances.get(j).getIdentifyingName()+" to input "+joinTreeToJoinFromAllAuxiliaries.getInput(j).getName()+" of "+
							joinTreeToJoinFromAllAuxiliaries.getInput(j).getModule().getIdentifyingName()+"(input to Join Tree sub-circuit)");
					
					joinTreeToJoinFromAllAuxiliaries.incrementAttribute(0);
				}
				for(int j=0;j<SerNQPrimeModuleInstances.size();j++){
					Wire currentSerNQPrimeToJoinTree= new Wire(SerNQPrimeModuleInstances.get(j).getOutput(i),joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)));
					updateStageInterconnections.add(currentSerNQPrimeToJoinTree);
					
					ConsoleWindow.output("Connected output "+SerNQPrimeModuleInstances.get(j).getOutput(i).getName()+
							" of "+SerNQPrimeModuleInstances.get(j).getIdentifyingName()+" to input "+
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)).getName()+" of "+
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)).getModule().getIdentifyingName()+" (input to Join Tree sub-circuit)");
					
					joinTreeToJoinFromAllAuxiliaries.incrementAttribute(0);
				}
				for(int j=0;j<outputSerNQModuleInstances.size();j++){
					Wire currentOutputStageSerNQToJoinTree= new Wire(outputSerNQModuleInstances.get(j).getOutput(i),
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)));
					updateStageInterconnections.add(currentOutputStageSerNQToJoinTree);
					
					ConsoleWindow.output("Connected output "+outputSerNQModuleInstances.get(j).getOutput(i).getName()+
							" of "+outputSerNQModuleInstances.get(j).getIdentifyingName()+" to input "+
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)).getName()+" of "+
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)).getModule().getIdentifyingName()+" (input to Join Tree sub-circuit)");
					
					joinTreeToJoinFromAllAuxiliaries.incrementAttribute(0);
				}
				for(int j=0;j<outputSerNQPrimeModuleInstances.size();j++){
					Wire currentOutputStageSerNQPrimeToJoinTree = new Wire(outputSerNQPrimeModuleInstances.get(j).getOutput(i),joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)));
					updateStageInterconnections.add(currentOutputStageSerNQPrimeToJoinTree);
					
					ConsoleWindow.output("Connected output "+outputSerNQPrimeModuleInstances.get(j).getOutput(i).getName()+
							" of "+outputSerNQPrimeModuleInstances.get(j).getIdentifyingName()+" to input "+
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)).getName()+" of "+
							joinTreeToJoinFromAllAuxiliaries.getInput(joinTreeToJoinFromAllAuxiliaries.getAttribute(0)).getModule().getIdentifyingName()+" (input to Join Tree sub-circuit)");
					
					joinTreeToJoinFromAllAuxiliaries.incrementAttribute(0);
				}

				ModuleOutputLine joinTreeOutput=joinTreeToJoinFromAllAuxiliaries.getOutput(0);
				ConstructionModule outputSetCorrespondingSerNQ=outputSerNQModuleInstances.get(i);

				ModuleInputLine inputOfSerNQ =outputSetCorrespondingSerNQ.getInput(SerNDefinition.getNoOfInputs());
				Wire joinToOutputSetCorrespondingSerNQ = new Wire(joinTreeOutput,inputOfSerNQ);
				updateStageInterconnections.add(joinToOutputSetCorrespondingSerNQ);
				
				ConsoleWindow.output("Connected output "+joinTreeOutput+
						" of "+joinTreeOutput.getModule().getIdentifyingName()+" (output from Join Tree sub-circuit) to query input "+inputOfSerNQ.getName()+" of "+inputOfSerNQ.getModule().getIdentifyingName());
			}			
		}
	}

	/* As described at the top of this file, this basically "inverts" a stage 2 construction which has been produced by the main
	 * algorithm (which built it according to the construction method of stage 1 for the inverted Set Notation module). This replaces
	 * all Joins with Forks and vice versa, and swaps all SerQM with SerQM' and vice versa. This yields a "correct" forwards version
	 * of stage 2 for the original module, ready to be connected to stage 1 via the update stage of the algorithm. */
	private static void invertStage2() {
		for(int i=0;i<outputSerNQPrimeModuleInstances.size();i++){		
			ConsoleWindow.output("Swapping "+outputSerNQPrimeModuleInstances.get(i).getIdentifyingName()+" for SerNQ' module");	
			GenerateSerModules.convertExistingSerNQToSerNQPrime(outputSerNQPrimeModuleInstances.get(i),
					SerNQPrimeDefinition.getInternalSetDefinition(),SerNDefinition.getNoOfOutputs());
			
		}
		for(int i=0;i<outputSerNQModuleInstances.size();i++){	
			ConsoleWindow.output("Swapping "+outputSerNQModuleInstances.get(i).getIdentifyingName()+" for SerNQ module");
			GenerateSerModules.convertExistingSerNQPrimeToSerNQ(outputSerNQModuleInstances.get(i),
					SerNQDefinition.getInternalSetDefinition(),SerNDefinition.getNoOfOutputs());
			
		}
		for(int i=0;i<outputJoinTreeModuleInstances.size();i++){			
			ConstructionModule joinTreeDefinitionToReplaceForkTreeDefinition =GenerateCommonModules.createJoinTree(outputJoinTreeModuleInstances.get(i).getNoOfOutputs());
			ConsoleWindow.output("Inverting module "+outputJoinTreeModuleInstances.get(i).getIdentifyingName());
			outputJoinTreeModuleInstances.get(i).invertModule(joinTreeDefinitionToReplaceForkTreeDefinition.getInternalSetDefinition());
		}
		for(int i=0;i<outputJoinTreeToSerNQPrimeInterconnections.size();i++){
			ConsoleWindow.output("Inverting wire "+outputJoinTreeToSerNQPrimeInterconnections.get(i).print());	
			outputJoinTreeToSerNQPrimeInterconnections.get(i).invertWire();
		}
		for(int i=0;i<outputMxNForkColumns.size();i++){	
			ConsoleWindow.output("Inverting MxN Join columns in state "+originalModuleDefinition.getStateName(i));
			for(int j=0;j<outputMxNForkColumns.get(i).size();j++){
				ConsoleWindow.output("Inverting MxN Join column");
				for(int k=0;k<outputMxNForkColumns.get(i).get(j).size();k++){
					ConstructionModule joinToInvert = outputMxNForkColumns.get(i).get(j).get(k);	
					ConsoleWindow.output("Inverting MxN Join "+joinToInvert.getIdentifyingName());	
					ConstructionModule forkDefinitionToReplaceJoinDefinition = GenerateCommonModules.createFork(
							joinToInvert.getAttribute(0),joinToInvert.getNoOfInputs()-joinToInvert.getAttribute(0));
					joinToInvert.invertModule(forkDefinitionToReplaceJoinDefinition.getInternalSetDefinition());
				}
			}
		}
		for(int i=0;i<outputMxNForkColumnInterconnections.size();i++){
			for(int j=0;j<outputMxNForkColumnInterconnections.get(i).size();j++){
				ConsoleWindow.output("Inverting wire "+outputMxNForkColumnInterconnections.get(i).get(j).print());
				outputMxNForkColumnInterconnections.get(i).get(j).invertWire();
			}
		}
	}

	/* This basically adds all modules and wires from the construction into the main circuit object (from their separate collections).
	 * It numbers all modules and wires for readability. It also labels the external circuit input and output lines with their 
	 * external labels */
	private static void tidyUpAndLabel() {
		for(int i=0;i<SerNQModuleInstances.size();i++){
			overallConstruction.addConstructionModule(SerNQModuleInstances.get(i));
			overallConstruction.setCircuitInput(i,SerNDefinition.getNoOfInputs(),originalModuleDefinition.getInputName(i));
			
			ConsoleWindow.output("External input port "+originalModuleDefinition.getInputName(i)+" is on input "+
			overallConstruction.getModule(i).getInput(SerNDefinition.getNoOfInputs()).getName()+" of "+overallConstruction.getModule(i).getIdentifyingName());
		}
		for(int i=0;i<SerNQPrimeModuleInstances.size();i++){
			overallConstruction.addConstructionModule(SerNQPrimeModuleInstances.get(i));
		}
		for(int i=0;i<choiceModuleInstances.size();i++){
			overallConstruction.addConstructionModule(choiceModuleInstances.get(i));
		}
		for(int i=0;i<forkTreeModuleInstances.size();i++){
			overallConstruction.addConstructionModule(forkTreeModuleInstances.get(i));
		}
		for(int i=0;i<SerNQToForkTreeInterconnections.size();i++){
			overallConstruction.addWire(SerNQToForkTreeInterconnections.get(i));
		}
		for(int i=0;i<MxNJoinColumns.size();i++){
			for(int j=0;j<MxNJoinColumns.get(i).size();j++){
				for(int k=0;k<MxNJoinColumns.get(i).get(j).size();k++){
					overallConstruction.addConstructionModule(MxNJoinColumns.get(i).get(j).get(k));
				}
			}
		}
		for(int i=0;i<MxNJoinColumnInterconnections.size();i++){
			for(int j=0;j<MxNJoinColumnInterconnections.get(i).size();j++){
				overallConstruction.addWire(MxNJoinColumnInterconnections.get(i).get(j));
			}
		}
		if(originalModuleDefinition.checkBarb()){
			for(int i=0;i<irreversibleOutputStageForkTreeModuleInstances.size();i++){
				overallConstruction.addConstructionModule(irreversibleOutputStageForkTreeModuleInstances.get(i));
			}
			for(int i=0;i<irreversibleOutputStageMergeTreeModuleInstances.size();i++){
				overallConstruction.addConstructionModule(irreversibleOutputStageMergeTreeModuleInstances.get(i));
				overallConstruction.setCircuitOutput(overallConstruction.getNoOfModules()-1,0,originalModuleDefinition.getOutputName(i));
				
				ConsoleWindow.output("External output port "+originalModuleDefinition.getOutputName(i)+" is on output "+
						overallConstruction.getModule(overallConstruction.getNoOfModules()-1).getOutput(0).getName()+" of "
						+overallConstruction.getModule(overallConstruction.getNoOfModules()-1).getIdentifyingName());
			}
			for(int i=0;i<irreversibleOutputStageInterconnections.size();i++){
				overallConstruction.addWire(irreversibleOutputStageInterconnections.get(i));
			}
		}
		else{
			for(int i=0;i<outputSerNQPrimeModuleInstances.size();i++){
				overallConstruction.addConstructionModule(outputSerNQPrimeModuleInstances.get(i));
				overallConstruction.setCircuitOutput(overallConstruction.getNoOfModules()-1,SerNDefinition.getNoOfOutputs(),originalModuleDefinition.getOutputName(i));
				
				ConsoleWindow.output("External output port "+originalModuleDefinition.getOutputName(i)+" is on output "+
						overallConstruction.getModule(overallConstruction.getNoOfModules()-1).getOutput(SerNDefinition.getNoOfOutputs()).getName()+" of "
						+overallConstruction.getModule(overallConstruction.getNoOfModules()-1).getIdentifyingName());
			}
			for(int i=0;i<outputSerNQModuleInstances.size();i++){
				overallConstruction.addConstructionModule(outputSerNQModuleInstances.get(i));
			}
			for(int i=0;i<outputJoinTreeModuleInstances.size();i++){
				overallConstruction.addConstructionModule(outputJoinTreeModuleInstances.get(i));
			}
			for(int i=0;i<outputJoinTreeToSerNQPrimeInterconnections.size();i++){
				overallConstruction.addWire(outputJoinTreeToSerNQPrimeInterconnections.get(i));
			}
			for(int i=0;i<outputMxNForkColumns.size();i++){
				for(int j=0;j<outputMxNForkColumns.get(i).size();j++){
					for(int k=0;k<outputMxNForkColumns.get(i).get(j).size();k++){
						overallConstruction.addConstructionModule(outputMxNForkColumns.get(i).get(j).get(k));
					}
				}
			}
			for(int i=0;i<outputMxNForkColumnInterconnections.size();i++){
				for(int j=0;j<outputMxNForkColumnInterconnections.get(i).size();j++){
					overallConstruction.addWire(outputMxNForkColumnInterconnections.get(i).get(j));
				}
			}
		}
		for(int i=0;i<updateStageForkTreeModuleInstances.size();i++){
			overallConstruction.addConstructionModule(updateStageForkTreeModuleInstances.get(i));
		}
		for(int i=0;i<updateStageJoinTreeModuleInstances.size();i++){
			overallConstruction.addConstructionModule(updateStageJoinTreeModuleInstances.get(i));
		}
		for(int i=0;i<updateStageInterconnections.size();i++){
			overallConstruction.addWire(updateStageInterconnections.get(i));
		}
		overallConstruction.numberModules();
	}

}
