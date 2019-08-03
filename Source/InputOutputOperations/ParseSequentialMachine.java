package InputOutputOperations;

import java.util.Vector;

import CommonStructures.IntSet;
import CommonStructures.SetSet;
import GUI.ConsoleWindow;
import SequentialMachineStructure.NDSequentialMachine;
import SequentialMachineStructure.SeqTransition;

/* Contains parsing functionality for the sequential machine module definition found in the
 * Conversion tab. The "main" definition and A function definition are parsed separately */
public class ParseSequentialMachine{

	/* Parses the main definition, then if there are no problems, proceeds to parse the A
	 * function definition if present. If no A function is present, the software auto-generates
	 * an A function definition (under the assumption that it is serial) */
	public static NDSequentialMachine parse(String definitionString, String AFunctionString) throws Exception{	
		NDSequentialMachine builtModule = parseMain(definitionString);
		if(AFunctionString.equals("")){
			ConsoleWindow.output("A function is empty. Auto-filling entirely with singleton inputs.");
			completeAllowables(builtModule);
		}
		else{
			builtModule=parseAllowables(builtModule,AFunctionString);
		}
		return builtModule;
	}

	/* Parses the sequential machine module definition given as a giant string (stripping out appropriate
	 * white spaces, newline characters etc.), and checks syntactic correctness.
	 * See ConsoleWindow.output calls below for more information as to what operations are performed at each point */
	public static NDSequentialMachine parseMain(String definitionString) throws Exception{

		/* Create empty module to store parsed data */
		NDSequentialMachine builtModule = new NDSequentialMachine();

		/* Filter out any spaces */
		definitionString=definitionString.replaceAll("\\s+","");
		
		/* Filter out any new lines */
		definitionString=definitionString.replaceAll("\n","");

		/* Separate into lines via semi-colons */
		String[] linesOfDefinition = definitionString.split(";");

		/* Variable to store state name once detected */
		String stateName;

		ConsoleWindow.output("Building list of state names");
		
		/* For each line */
		for(int i=0;i<linesOfDefinition.length;i++){

			/* Check the current line is not empty */
			if(linesOfDefinition[i].length()==0){
				ConsoleWindow.output("Empty line defined. Please tidy");
				throw new Exception();
			}

			/* Section for retrieving the state name */

			String[] splitAtEquals = linesOfDefinition[i].split("\\Q=\\E");
			stateName = splitAtEquals[0];
			
			if(splitAtEquals.length!=2){
				ConsoleWindow.output("Problem detecting equals sign on "+i+"th line");
				throw new Exception();
			}
			
			/* Check the current char is alphanumeric */
			if(!GeneralOperations.alphaNumeric(stateName,false)){
				ConsoleWindow.output("Non alpha-numeric char detected in state name. Parsing cancelled");
				throw new Exception();
			}
			
			if(!builtModule.addStateName(stateName)){
				ConsoleWindow.output("Duplicate state defined. Please check and try again");
				throw new Exception();
			}
			
		}
		ConsoleWindow.output("List of state names built successfully");

		/* For every line */
		for(int i=0; i<linesOfDefinition.length;i++){

			/* Retrieve the state name for this line, and then use that to
			 * calculate the index to begin parsing from */
			stateName=builtModule.getStateName(i);
			
			String stateBody = linesOfDefinition[i].substring(stateName.length()+1, linesOfDefinition[i].length());

			ConsoleWindow.output("Reading data for "+i+"th state:" + stateName);
			ConsoleWindow.output("Parsing list of actions for module definition line");
			
			String[] listOfActions = stateBody.split("\\Q+\\E");
			for(int j=0;j<listOfActions.length;j++){
				
				int currentInput;
				IntSet currentOutputSet = new IntSet();
				
				ConsoleWindow.output("Parsing new action for module definition line. String is \""+listOfActions[j]+"\"");
				
				String[] splitAtStateDot = listOfActions[j].split("\\Q.\\E");
				if(splitAtStateDot.length!=2){
					ConsoleWindow.output("Error parsing action. Problem detecting dot separating IO set and resulting state");
					throw new Exception();
				}
				String IOsetWithBrackets = splitAtStateDot[0];
				String currentResultState = splitAtStateDot[1];
				
				if(!GeneralOperations.alphaNumeric(currentResultState,false)){
					ConsoleWindow.output("Non alpha-numeric char detected in resulting module name. Parsing cancelled");
					throw new Exception();
				}
				
				if(IOsetWithBrackets.charAt(0)!='(' || IOsetWithBrackets.charAt(IOsetWithBrackets.length()-1)!=')'){
					ConsoleWindow.output("Error parsing action. Problem detecting brackets which surround IO set");
					throw new Exception();
				}
				String IOset = IOsetWithBrackets.substring(1,IOsetWithBrackets.length()-1);
				
				String[] IOsplitAtComma = IOset.split("\\Q,{\\E");
				
				if(IOsplitAtComma.length!=2){
					ConsoleWindow.output("Error parsing action. Problem detecting division between input name and output set");
					throw new Exception();
				}
				
				if(IOsplitAtComma[1].charAt(IOsplitAtComma[1].length()-1)!='}'){
					ConsoleWindow.output("Error parsing action. Problem detecting closing bracket of output set");
					throw new Exception();
				}
				
				if(!GeneralOperations.alphaNumeric(IOsplitAtComma[0],false)){
							ConsoleWindow.output("Non alpha-numeric char detected in input name. Parsing cancelled");
							throw new Exception();
				}
				builtModule.addInputName(IOsplitAtComma[0]);
				currentInput=builtModule.getInputIndex(IOsplitAtComma[0]);
				
				if(IOsplitAtComma[1].length()==1){
					ConsoleWindow.output("Empty output set detected");
				}
				else{
					String[] outputItems = IOsplitAtComma[1].substring(0,IOsplitAtComma[1].length()-1).split(",");
					for(int k=0;k<outputItems.length;k++){
						if(!GeneralOperations.alphaNumeric(outputItems[k],false)){
							ConsoleWindow.output("Non alpha-numeric char detected in output name. Parsing cancelled");
							throw new Exception();
						}
						for(int l=0;l<k;l++){
							if(outputItems[l].equals(outputItems[k])){
								ConsoleWindow.output("Multiset output detected with output name "+ outputItems[k]+". Parsing cancelled");
								throw new Exception();
							}
						}

						builtModule.addOutputName(outputItems[k]);
						currentOutputSet.add(builtModule.getOutputIndex(outputItems[k]));
					}
				}
				
				if(builtModule.getStateIndex(currentResultState)==-1){
					ConsoleWindow.output("Resulting state \""+currentResultState+"\" is undefined! Parsing cancelled");
				}

				/* Build actual transition */
				SeqTransition transition = new SeqTransition(builtModule.getStateIndex(stateName),
						currentInput,builtModule.getStateIndex(currentResultState),currentOutputSet,builtModule);
				builtModule.addTransition(transition);

				ConsoleWindow.output("Successfully parsed action: "+transition.printStringAction(false));
			}
			
		}
		if(builtModule.duplicateTransitions()){
			ConsoleWindow.output("Duplicate transitions detected - please fix. Parsing cancelled");
			throw new Exception();
		}
		if(builtModule.duplicateStates()){
			ConsoleWindow.output("Duplicate state bodies detected - please fix. Parsing cancelled");
			throw new Exception();
		}		
		ConsoleWindow.output("Parsing of module completed successfully, module is: "+builtModule.printMainOnly(false));
		
		return builtModule;
	}	
	
	/* Parses the Set Notation module's A function definition given as a giant string (stripping out appropriate
	 * white spaces, newline characters etc.), and checks syntactic correctness.
	 * See ConsoleWindow.output calls below for more information as to what operations are performed at each point */
	public static NDSequentialMachine parseAllowables(NDSequentialMachine builtModule, String AFunctionString) throws Exception{

		AFunctionString=AFunctionString.replaceAll("\\s+","");
		AFunctionString=AFunctionString.replaceAll("\n","");

		/* Split input into lines via semi-colons */
		String[] linesOfAFunction = AFunctionString.split(";");

		ConsoleWindow.output("Parsing allowable set data");

		/*for every line */
		for(int i=0; i<linesOfAFunction.length;i++){
			
			/* variables which cover entire line */
			String stateName;

			String[] splitAtEquals = linesOfAFunction[i].split("\\Q=\\E");

			
			if(splitAtEquals.length!=2){
				ConsoleWindow.output("Problem detecting equals sign on "+i+"th line");
				throw new Exception();
			}
			
			stateName=splitAtEquals[0];

			if(!GeneralOperations.alphaNumeric(stateName,false)){
				ConsoleWindow.output("Non alpha-numeric char detected in state name. Parsing cancelled");
				throw new Exception();
			}
			
			boolean defined=false;
			for(int j=0;j<builtModule.getNoOfStates();j++){
				if(builtModule.getStateName(j).equals(stateName)){
					defined=true;
				}
			}
			
			if(!defined){
				ConsoleWindow.output("State name in A function not in main definition. Parsing cancelled");
				throw new Exception();
			}
			
			if(builtModule.getAFunction().getState(builtModule.getStateIndex(stateName)).size()>0){
				ConsoleWindow.output("Duplicate state definition detected");
				throw new Exception();
			}

			ConsoleWindow.output("Reading allowable inputs for state: "+stateName);
			
			String stateBody = splitAtEquals[1];
			
			if(stateBody.charAt(0)!='{' || stateBody.charAt(stateBody.length()-1)!='}'){
				ConsoleWindow.output("Problem with brackets surrounding set of sets. Parsing cancelled");
				throw new Exception();
			}
			
			String[] listOfSets = stateBody.substring(1,stateBody.length()-1).split("\\Q},{\\E");
			
			if(listOfSets[0].charAt(0)!='{'){
				ConsoleWindow.output("Missing opening bracket of first set");
				throw new Exception();
			}
			listOfSets[0]=listOfSets[0].substring(1, listOfSets[0].length());
			
			if(listOfSets[listOfSets.length-1].charAt(listOfSets[listOfSets.length-1].length()-1)!='}'){
				ConsoleWindow.output("Missing closing bracket of last set");
				throw new Exception();
			}
			listOfSets[listOfSets.length-1]=listOfSets[listOfSets.length-1].substring(0,listOfSets[listOfSets.length-1].length()-1);
		
			for(int j=0;j<listOfSets.length;j++){
				IntSet builtSet = new IntSet();
				String[] listOfInputs = listOfSets[j].split(",");
				for(int k=0;k<listOfInputs.length;k++){
					if(!GeneralOperations.alphaNumeric(listOfInputs[k],false)){
						ConsoleWindow.output("Non alpha-numeric char detected in A set input. Parsing cancelled");
						throw new Exception();
					}
					int index = builtModule.getInputIndex(listOfInputs[k]);
					if(index==-1){
						ConsoleWindow.output("Unrecognised input name in A function. Parsing cancelled");
						throw new Exception();
					}
					builtSet.add(index);
				}
				builtModule.getAFunction().addSetToState(builtSet, builtModule.getStateIndex(stateName));
				ConsoleWindow.output("Added allowable set "+builtSet.printStringRepresentation(builtModule.getInputNames())+" to state "+stateName);
			}
			
		}
		
		ConsoleWindow.output("Checking for undefined inputs in sets of A function.");
		
		if(!checkInputsDefined(builtModule)){
			ConsoleWindow.output("Undefined inputs in A function. Please correct");
			throw new Exception();
		}
		ConsoleWindow.output("No undefined inputs in sets of A function.");
		ConsoleWindow.output("Checking for duplicate sets in A function.");
		
		if(checkDuplicateSets(builtModule)){
			ConsoleWindow.output("Duplicate sets in A function. Please correct");
			throw new Exception();
		}
		ConsoleWindow.output("No duplicate sets in A function.");
		ConsoleWindow.output("Auto-completing any singleton inputs which aren't defined in A function.");
		
		completeAllowables(builtModule);
		
		ConsoleWindow.output("A function seems fine.");
		return builtModule;
	} 
	
	/* This simply checks that there are no duplicate entries in the A function definition */
	private static boolean checkDuplicateSets(NDSequentialMachine builtModule){
		for(int i=0;i<builtModule.getNoOfStates();i++){
			SetSet setsForState = builtModule.getAFunction().getState(i);
			for(int j=0;j<setsForState.size()-1;j++){
				IntSet firstSet = setsForState.get(j);
				for(int k=j+1;k<setsForState.size();k++){
					IntSet secondSet = setsForState.get(k);
					if(firstSet.equals(secondSet)){
						return true;
					}
				}
			}
		}
		return false;
	}

	/* This checks that A function entries are valid, by checking that a transition
	 * for the specified state and input name exists in the main module definition */
	private static boolean checkInputsDefined(NDSequentialMachine result) {
		for(int i=0;i<result.getNoOfStates();i++){
			SetSet setsForState = result.getAFunction().getState(i);
			for(int j=0;j<setsForState.size();j++){
				IntSet currentSet = setsForState.get(j);
				for(int k=0;k<currentSet.size();k++){
					int currentInput = currentSet.get(k);
					Vector<SeqTransition> definedTransitions = result.getTransitionsWithSourceAndInput(currentInput,i);
					if(definedTransitions.size()==0){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/* This simply auto-generates missing A function entries. If there exists an action in the main
	 * definition for a particular input "a" in a particular state "q", but there is no entry containing
	 * "a" in A(q), then we add a singleton set {a} to A(q). Hence it assumes that missing entries are
	 * not concurrent */
	private static void completeAllowables(NDSequentialMachine builtModule) {
		for(int i=0;i<builtModule.getNoOfStates();i++){
			Vector<SeqTransition> transitionsForState = builtModule.getTransitionsWithSource(i);
			SetSet allowablesForState = builtModule.getAFunction().getState(i);
			for(int j=0;j<transitionsForState.size();j++){
				SeqTransition currentTransition = transitionsForState.get(j);
				int currentInput = currentTransition.getInput();
				boolean found = false;
				for(int k=0;k<allowablesForState.size();k++){
					IntSet currentSet = allowablesForState.get(k);
					if(currentSet.contains(currentInput)!=-1){
						found=true;
						break;
					}
				}
				if(!found){
					ConsoleWindow.output("Auto-filling singleton allowable set for input "+
							builtModule.getInputName(currentInput) +" in state "+builtModule.getStateName(i));
					IntSet freshSingletonSet = new IntSet();
					freshSingletonSet.add(currentInput);
					builtModule.getAFunction().getState(i).add(freshSingletonSet);
				}
			}
		}
	}
	
}
