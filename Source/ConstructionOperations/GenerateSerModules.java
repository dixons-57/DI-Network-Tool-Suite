package ConstructionOperations;
import java.util.Vector;

import CommonStructures.IntSet;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Contains operations for generating SerN, SerNQ, SerNQ' modules from a given Set Notation module definition. */
public class GenerateSerModules{

	/* Generates the SerN module based on a Set Notation module definition */
	public static ConstructionModule createSerN(SetNotationModule setModuleDefinition){
		
		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();
		
		/* Add all the state names of the Set Notation module to the new SerN module */
		for(int i=0;i<setModuleDefinition.getNoOfStates();i++){
			internalSet.addStateName(setModuleDefinition.getStateName(i));
		}
		
		/* If the Set Notation module definition is eq-arb, then we need to first relabel equal input sets 
		 * in the same state (see the relevant chapter of the thesis), so that they can be mapped to 
		 * different SerN inputs (remember that the SerN modules themselves are not responsible for arbitration) */
		
		/* Iterate through every state of the module */
		for(int i=0;i<setModuleDefinition.getNoOfStates();i++){
			
			/* Retrieve its transitions */
			Vector<SetTransition> transitionsForState = setModuleDefinition.getTransitionsWithSource(i);
			
			/* In decreasing order */
			for(int j=transitionsForState.size()-1;j>0;j--){
				
				/* Retrieve the input set */
				IntSet inputSet=transitionsForState.get(j).getInputSet();
				
				/* Now count how many times the input set appears in the transitions "prior"
				 * to this one in the list */
				int previousOccurencesOfThisInputSet=0;
				for(int k=0;k<j;k++){
					if(transitionsForState.get(k).getInputSet().equals(inputSet)){
						previousOccurencesOfThisInputSet++;
					}
				}
				
				/* If this input set has already appeared in the same state "n" times, where n>0,
				 * then add -n to the end of the set. Hence if {a,b,c} appears in 3 actions in the same
				 * state, (stored internally as {0,1,2}) the input sets of these actions would instead
				 * become {0,1,2}, {0,1,2,-1}, and {0,1,2,-2}. This helps force us to map repeated
				 * occurrences to different inputs of SerN */
				if(previousOccurencesOfThisInputSet>0){
					inputSet.add(-previousOccurencesOfThisInputSet);
				}
			}
		}
		
		/* Lists all unique Set Notation module input sets (sets with a negative trailing value are 
		 * counted separately from the set they are "equal" to). This allows a mapping between 
		 * SerN and the original module (so I1 maps to the first element in this vector etc) */
		Vector<IntSet> setModuleInputSets = new Vector<IntSet>();
		
		/* Same as above but with output sets */
		Vector<IntSet> setModuleOutputSets = new Vector<IntSet>();
		
		/* Stores the list of associations of each input set with each source state. 
		 * See ConstructionModule for more details */
		Vector<IntSet> setModuleInputSetAppearancesByState= new Vector<IntSet>();
		
		/* Same as above but with output sets and target states */
		Vector<IntSet> setModuleOutputSetAppearancesByState = new Vector<IntSet>();
		
		/* Initialise the above two sets with one empty set per state */
		for(int i=0;i<setModuleDefinition.getNoOfStates();i++){
			setModuleInputSetAppearancesByState.add(new IntSet());
			setModuleOutputSetAppearancesByState.add(new IntSet());
		}
		
		/* For every transition of the Set Notation module */
		for(int i=0;i<setModuleDefinition.getNoOfTransitions();i++){
			SetTransition currentTransition=setModuleDefinition.getTransition(i);
			
			/* Retrieve its input and output sets */
			IntSet inputSet=currentTransition.getInputSet();
			IntSet outputSet=currentTransition.getOutputSet();
			int sourceState=currentTransition.getSourceState();
			int resultState=currentTransition.getResultState();
			
			/* Get the corresponding SerN input index for the current Set Notation input set, adding it to the list of mappings if not yet done so */
			int SerNInputIndex=fileInputSet(setModuleInputSets,setModuleInputSetAppearancesByState.get(sourceState),inputSet,internalSet);
			
			/* Get the corresponding SerN output index for the current Set Notation output set, adding it to the list of mappings if not yet done so */
			int SerNOutputIndex=fileOutputSet(setModuleOutputSets,setModuleOutputSetAppearancesByState.get(resultState),outputSet,internalSet);
			
			/* Build the equivalent SerN transition */
			IntSet SerNOutputSet = new IntSet();
			SerNOutputSet.add(SerNOutputIndex);
			internalSet.addTransition(new SetTransition(sourceState,new IntSet(SerNInputIndex),resultState,SerNOutputSet.deepCopy(),internalSet));
		}
		
		/* Build the set of input and output lines for the SerN module */
		Vector<ModuleInputLine> SerNInputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> SerNOutputPorts = new Vector<ModuleOutputLine>();
		for(int i=0;i<setModuleInputSets.size();i++){
			SerNInputPorts.add(new ModuleInputLine(internalSet.getInputName(i),i));
		}
		for(int i=0;i<setModuleOutputSets.size();i++){
			SerNOutputPorts.add(new ModuleOutputLine(internalSet.getOutputName(i),i));
		}
		
		/* Build the final SerN module, and store the mappings between its input/output sets, as well as the 
		 * associations of each set with source and target states of actions (input sets and their source state,
		 * output sets and their target states) */
		ConstructionModule SerNinstance = new ConstructionModule("SerN",SerNInputPorts,SerNOutputPorts,internalSet);
		SerNinstance.storeSetModuleInputs(setModuleInputSets);
		SerNinstance.storeSetModuleOutputs(setModuleOutputSets);
		SerNinstance.storeSetModuleInputAppearancesByState(setModuleInputSetAppearancesByState);
		SerNinstance.storeSetModuleOutputAppearancesByState(setModuleOutputSetAppearancesByState);

		return SerNinstance;
	}

	/* Builds a SerNQ module from a SerN module instance. It is fairly straightforward and adds
	 * a query input line, then query output line for each state of the module, as well as
	 * relevant actions to allow the querying of the module's state */
	public static ConstructionModule createSerNQFromSerN(ConstructionModule SerNInstance){
		SetNotationModule internalSet = new SetNotationModule();
		Vector<ModuleInputLine> SerNQInputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> SerNQOutputPorts = new Vector<ModuleOutputLine>();
		
		for(int i=0;i<SerNInstance.getNoOfInputs();i++){
			SerNQInputPorts.add(new ModuleInputLine(SerNInstance.getInput(i).getName(),i));
			internalSet.addInputName(SerNInstance.getInternalSetDefinition().getInputName(i));
		}
		SerNQInputPorts.add(new ModuleInputLine("q",SerNInstance.getNoOfInputs()));
		internalSet.addInputName("q");
		for(int i=0;i<SerNInstance.getNoOfOutputs();i++){
			SerNQOutputPorts.add(new ModuleOutputLine(SerNInstance.getOutput(i).getName(),i));
			internalSet.addOutputName(SerNInstance.getInternalSetDefinition().getOutputName(i));
		}
		for(int i=0;i<SerNInstance.getInternalSetDefinition().getNoOfStates();i++){
			SerNQOutputPorts.add(new ModuleOutputLine("q"+SerNInstance.getInternalSetDefinition().getStateName(i),SerNInstance.getNoOfOutputs()+i));
			internalSet.addOutputName("q"+SerNInstance.getInternalSetDefinition().getStateName(i));
		}
		
		for(int i=0;i<SerNInstance.getInternalSetDefinition().getNoOfStates();i++){
			Vector<SetTransition> SerNActionsForThisState=SerNInstance.getInternalSetDefinition().getTransitionsWithSource(i);
			internalSet.addStateName("SNQ"+SerNInstance.getInternalSetDefinition().getStateName(i));
			for(int j=0;j<SerNActionsForThisState.size();j++){
				SetTransition SerNAction = SerNActionsForThisState.get(j);
				internalSet.addTransition(new SetTransition(i,new IntSet(SerNAction.getInputSet().get(0)),
						SerNAction.getResultState(),SerNAction.getOutputSet().deepCopy(),internalSet));
			}
			IntSet outputSetForQuery = new IntSet();
			outputSetForQuery.add(internalSet.getOutputIndex("q"+SerNInstance.getInternalSetDefinition().getStateName(i)));
			internalSet.addTransition(new SetTransition(i,new IntSet(internalSet.getInputIndex("q")),i,outputSetForQuery.deepCopy(),internalSet));
		}
		ConstructionModule SerNQModule = new ConstructionModule("SerNQ",SerNQInputPorts,SerNQOutputPorts,internalSet);
		
		return SerNQModule;
	}
	
	/* Builds a SerNQ' module from a SerN module instance. Recall that it is similar to the above
	 * SerNQ behaviour, but the query functionality is inverted. It is fairly straightforward and adds
	 * a query output line, then query input line for each state of the module, as well as
	 * relevant actions to allow the "inverse" querying of the module's state */
	public static ConstructionModule createSerNQPrimeFromSerN(ConstructionModule SerNInstance){
		SetNotationModule internalSet = new SetNotationModule();
		Vector<ModuleInputLine> SerNQModuleInputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> SerNQModuleOutputPorts = new Vector<ModuleOutputLine>();
		
		for(int i=0;i<SerNInstance.getNoOfInputs();i++){
			SerNQModuleInputPorts.add(new ModuleInputLine(SerNInstance.getInput(i).getName(),i));
			internalSet.addInputName(SerNInstance.getInternalSetDefinition().getInputName(i));
		}
		for(int i=0;i<SerNInstance.getNoOfOutputs();i++){
			SerNQModuleOutputPorts.add(new ModuleOutputLine(SerNInstance.getOutput(i).getName(),i));
			internalSet.addOutputName(SerNInstance.getInternalSetDefinition().getOutputName(i));
		}
		for(int i=0;i<SerNInstance.getInternalSetDefinition().getNoOfStates();i++){
			SerNQModuleInputPorts.add(new ModuleInputLine("q"+SerNInstance.getInternalSetDefinition().getStateName(i),SerNInstance.getNoOfInputs()+i));
			internalSet.addInputName("q"+SerNInstance.getInternalSetDefinition().getStateName(i));
		}

		SerNQModuleOutputPorts.add(new ModuleOutputLine("q",SerNInstance.getNoOfOutputs()));
		internalSet.addOutputName("q");
		
		for(int i=0;i<SerNInstance.getInternalSetDefinition().getNoOfStates();i++){
			Vector<SetTransition> SerNActionsForThisState=SerNInstance.getInternalSetDefinition().getTransitionsWithSource(i);
			internalSet.addStateName("SNQ\'"+SerNInstance.getInternalSetDefinition().getStateName(i));
			for(int j=0;j<SerNActionsForThisState.size();j++){
				SetTransition SerNAction = SerNActionsForThisState.get(j);
				internalSet.addTransition(new SetTransition(i,new IntSet(SerNAction.getInputSet().get(0)),SerNAction.getResultState(),SerNAction.getOutputSet().deepCopy(),internalSet));
			}
			IntSet outputSetForInverseQuery = new IntSet();
			outputSetForInverseQuery.add(internalSet.getOutputIndex("q"));	
			internalSet.addTransition(new SetTransition(i,new IntSet(internalSet.getInputIndex("q"+SerNInstance.getInternalSetDefinition().getStateName(i))),
					i,outputSetForInverseQuery.deepCopy(),internalSet));
		}
		ConstructionModule SerNQPrimeModule = new ConstructionModule("SerNQ'",SerNQModuleInputPorts,SerNQModuleOutputPorts,internalSet);
		return SerNQPrimeModule;
	}

	/* This converts a specific SerNQ' module to a SerNQ module. It effectively "inverts" the query functionality in the expected
	 * way, so that instead of multiple query input lines, and a single query output line, there becomes a single query input line
	 * and multiple query output lines. This is utilised by the construction algorithm, in the case of "inverting" stage 2 (which does not
	 * actually invert SerQM and SerQM' modules, but merely converts all SerQM modules to SerQM' modules and vice versa). */
	public static void convertExistingSerNQPrimeToSerNQ(ConstructionModule moduleToConvert, SetNotationModule nonPrimeInternalSet, int noOfSerNInputs){
		moduleToConvert.setInternalSetDefinition(nonPrimeInternalSet);
		moduleToConvert.setIdentifyingName(moduleToConvert.getIdentifyingName()+"ToSerNQ");
		ModuleOutputLine inverseQueryOutputOfSerNQPrime = moduleToConvert.getOutput(moduleToConvert.getNoOfOutputs()-1);  
		moduleToConvert.removeOutput(moduleToConvert.getNoOfOutputs()-1);
		for(int i=0;i<moduleToConvert.getInternalSetDefinition().getNoOfStates();i++){
			ModuleInputLine firstNonSerNInputOfSerNQPrime = moduleToConvert.getInput(noOfSerNInputs);
			moduleToConvert.removeInput(noOfSerNInputs);
			ModuleOutputLine currentSerNQQueryOutput = new ModuleOutputLine(firstNonSerNInputOfSerNQPrime.getName(),moduleToConvert,moduleToConvert.getNoOfOutputs());
			moduleToConvert.addOutput(currentSerNQQueryOutput);
		}
		ModuleInputLine SerNQQueryInput = new ModuleInputLine(inverseQueryOutputOfSerNQPrime.getName(),moduleToConvert,moduleToConvert.getNoOfInputs());
		moduleToConvert.addInput(SerNQQueryInput);
	}

	/* This converts a specific SerNQ module to a SerNQ' module. It effectively "inverts" the query functionality in the expected
	 * way, so that instead of a single query input line, and multiple query output lines, there becomes a single query output line
	 * and multiple query input lines. This is utilised by the construction algorithm, in the case of "inverting" stage 2 (which does not
	 * actually invert SerQM and SerQM' modules, but merely converts all SerQM modules to SerQM' modules and vice versa). */
	public static void convertExistingSerNQToSerNQPrime(ConstructionModule moduleToConvert, SetNotationModule primeInternalSet, int noOfSerNOutputs){
		moduleToConvert.setInternalSetDefinition(primeInternalSet);
		moduleToConvert.setIdentifyingName(moduleToConvert.getIdentifyingName()+"ToSerNQ'");
		ModuleInputLine queryInputOfSerNQ= moduleToConvert.getInput(moduleToConvert.getNoOfInputs()-1);
		moduleToConvert.removeInput(moduleToConvert.getNoOfInputs()-1);
		for(int i=0;i<moduleToConvert.getInternalSetDefinition().getNoOfStates();i++){
			ModuleOutputLine firstNonSerNOutputOfSerNQ = moduleToConvert.getOutput(noOfSerNOutputs);
			moduleToConvert.removeOutput(noOfSerNOutputs);
			ModuleInputLine currentSerNQPrimeInverseQueryInput = new ModuleInputLine(firstNonSerNOutputOfSerNQ.getName(),moduleToConvert,moduleToConvert.getNoOfInputs());
			moduleToConvert.addInput(currentSerNQPrimeInverseQueryInput);
		}
		ModuleOutputLine SerNQPrimeInverseQueryOutput = new ModuleOutputLine(queryInputOfSerNQ.getName(),moduleToConvert,moduleToConvert.getNoOfOutputs());
		moduleToConvert.addOutput(SerNQPrimeInverseQueryOutput);
	}
	
	/* This creates a mapping from a Set Notation module input set to a corresponding SerN input line, or returns the mapping if it already exists.
	 * We keep track of what mappings have already been defined with the setModuleInputSets variable, and we record that the current state being
	 * examined by the calling method contains action(s) with the current input set with the setModuleInputSetAppearancesForThisState variable. */
	private static int fileInputSet(Vector<IntSet> setModuleInputSets, IntSet setModuleInputSetAppearancesForThisState, 
			IntSet inputSet, SetNotationModule internalSet) {
		
		/* Assume the set doesn't exist in the list of filed input sets */
		boolean setAlreadyFiled=false;
		
		/* Search for it */
		int indexOfSet=-1;
		for(int i=0;i<setModuleInputSets.size();i++){
			if(setModuleInputSets.get(i).equals(inputSet)){
				setAlreadyFiled=true;
				indexOfSet=i;
				break;
			}
		}
		
		/* Files into the input set list if it doesn't exist, creating a new input line for SerN */
		if(!setAlreadyFiled){
			setModuleInputSets.add(inputSet);

			indexOfSet=setModuleInputSets.size()-1;
			internalSet.addInputName("I"+setModuleInputSets.size());
			
			/* We also must not yet have recorded that it appears in the current state, so add it */
			setModuleInputSetAppearancesForThisState.add(new Integer(setModuleInputSets.size()-1));
		}
		else{
			
			/* This means that the input set has already been added to the list of mappings */
			
			/* We still need to check if it appears in this state's "appearances" list 
			 * and add it appropriately */
			boolean existsInThisStateAppearancesList=false;
			for(int i=0;i<setModuleInputSetAppearancesForThisState.size();i++){
				if(setModuleInputSetAppearancesForThisState.get(i)==indexOfSet){
					existsInThisStateAppearancesList=true;
					break;
				}
			}
			if(!existsInThisStateAppearancesList){
				setModuleInputSetAppearancesForThisState.add(new Integer(indexOfSet));
			}
		}
		return indexOfSet;
	}

	/* This creates a mapping from a Set Notation module output set to a corresponding SerN output line, or returns the mapping if it already exists.
	 * We keep track of what mappings have already been defined with the setModuleOutputSets variable, and we record that the current state being
	 * examined by the calling method is a target of action(s) with the current output set with the setModuleOutputSetAppearancesForThisState variable. */
	private static int fileOutputSet(Vector<IntSet> setModuleOutputSets, IntSet setModuleOutputSetAppearancesForThisState,
			IntSet outputSet, SetNotationModule internalSet) {
		
		/* Assume the set doesn't exist in the list of filed output sets */
		boolean setAlreadyFiled=false;
		int indexOfSet=0;
		
		/* Search for it */
		for(int i=0;i<setModuleOutputSets.size();i++){
			if(setModuleOutputSets.get(i).equals(outputSet)){
				setAlreadyFiled=true;
				indexOfSet=i;
				break;
			}
		}
		
		/* Files into the output set list if it doesn't exist, creating a new output line for SerN */
		if(!setAlreadyFiled){
			
			setModuleOutputSets.add(outputSet);
			indexOfSet=setModuleOutputSets.size()-1;
			internalSet.addOutputName("O"+setModuleOutputSets.size());
			
			/* We also must not yet have recorded that the current state appears as a "target" of an
			 * action containing this output set, so add it */
			setModuleOutputSetAppearancesForThisState.add(new Integer(setModuleOutputSets.size()-1));
		}
		else{
			
			/* This means that the output set has already been added to the list of mappings */

			/* We still need to check if it appears in this state's "appearances" list 
			 * and add it appropriately */
			boolean existsInThisStateAppearancesList=false;
			for(int i=0;i<setModuleOutputSetAppearancesForThisState.size();i++){
				if(setModuleOutputSetAppearancesForThisState.get(i)==indexOfSet){
					existsInThisStateAppearancesList=true;
					break;
				}
			}
			if(!existsInThisStateAppearancesList){
				setModuleOutputSetAppearancesForThisState.add(new Integer(indexOfSet));
			}
		}
		return indexOfSet;
	}
}
