package InputOutputOperations;

import CommonStructures.IntSet;
import GUI.ConsoleWindow;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Contains parsing functionality for the Set Notation module definitions found in the
 * Conversion tab, Construction tab, and Environment Generation tab */
public class ParseSetNotationModule{

	/* Parses the Set Notation module definition given as a giant string (stripping out appropriate
	 * white spaces, newline characters etc.), and checks syntactic correctness.
	 * See ConsoleWindow.output calls below for more information as to what operations are performed at each point */
	public static SetNotationModule parse(String definitionString) throws Exception{

		/* Create empty module to store parsed data */
		SetNotationModule builtModule = new SetNotationModule();

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

			/* Check the currentline is not empty */
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
				
				IntSet currentInputSet = new IntSet();
				IntSet currentOutputSet = new IntSet();
				
				ConsoleWindow.output("Parsing new action for module definition line. String is \""+listOfActions[j]+"\"");
				
				String[] splitAtStateDot = listOfActions[j].split("\\Q.\\E");
				if(splitAtStateDot.length!=2){
					ConsoleWindow.output("Error parsing action. Problem detecting dot separating IO set and builtModuleing state");
					throw new Exception();
				}
				String IOsetWithBrackets = splitAtStateDot[0];
				String currentResultState = splitAtStateDot[1];
				
				if(!GeneralOperations.alphaNumeric(currentResultState ,false)){
					ConsoleWindow.output("Non alpha-numeric char detected in builtModuleing module name. Parsing cancelled");
					throw new Exception();
				}
				
				if(IOsetWithBrackets.charAt(0)!='(' || IOsetWithBrackets.charAt(IOsetWithBrackets.length()-1)!=')'){
					ConsoleWindow.output("Error parsing action. Problem detecting brackets which surround IO set");
					throw new Exception();
				}
				
				String IOset = IOsetWithBrackets.substring(1,IOsetWithBrackets.length()-1);
				String[] IOsplitAtComma = IOset.split("\\Q},{\\E");
				
				if(IOsplitAtComma.length!=2){
					ConsoleWindow.output("Error parsing action. Problem detecting division between I and O sets");
					throw new Exception();
				}
				
				if(IOsplitAtComma[0].charAt(0)!='{'){
					ConsoleWindow.output("Error parsing action. Problem detecting opening bracket of input set");
					throw new Exception();
				}
				
				if(IOsplitAtComma[1].charAt(IOsplitAtComma[1].length()-1)!='}'){
					ConsoleWindow.output("Error parsing action. Problem detecting closing bracket of output set");
					throw new Exception();
				}
				
				if(IOsplitAtComma[0].length()==1){
					ConsoleWindow.output("Empty input set detected. Not allowed for set notation. Parsing cancelled!");
					throw new Exception();
				}
				else{
					String[] inputItems = IOsplitAtComma[0].substring(1,IOsplitAtComma[0].length()).split(",");
					for(int k=0;k<inputItems.length;k++){
						if(!GeneralOperations.alphaNumeric(inputItems[k],false)){
							ConsoleWindow.output("Non alpha-numeric char detected in input name. Parsing cancelled");
							throw new Exception();
						}
						for(int l=0;l<k;l++){
							if(inputItems[l].equals(inputItems[k])){
								ConsoleWindow.output("Multiset input detected with input name "+ inputItems[k]+". Parsing cancelled");
								throw new Exception();
							}
						}
						builtModule.addInputName(inputItems[k]);
						currentInputSet.add(builtModule.getInputIndex(inputItems[k]));
					}
				}	
	
				if(IOsplitAtComma[1].length()==1){
					ConsoleWindow.output("Empty output set detected. Not allowed for set notation. Parsing cancelled!");
					throw new Exception();
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
					ConsoleWindow.output("Resulting state \""+currentResultState +"\" is undefined! Parsing cancelled");
				}

				/* Build transition */
				SetTransition transition = new SetTransition(builtModule.getStateIndex(stateName),currentInputSet,
						builtModule.getStateIndex(currentResultState),currentOutputSet,builtModule);
				builtModule.addTransition(transition);

				ConsoleWindow.output("Successfully parsed action: "+transition.printStringAction());
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

		ConsoleWindow.output("Parsing of module completed successfully, module is: "+builtModule.printModule());
		
		return builtModule;
	}
	
}
