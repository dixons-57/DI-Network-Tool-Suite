package InputOutputOperations;

import java.util.Vector;

import DISetAlgebraStructure.Bus;
import DISetAlgebraStructure.IOAction;
import DISetAlgebraStructure.NamedModule;
import DISetAlgebraStructure.NamedPort;
import DISetAlgebraStructure.NamedPortSet;
import DISetAlgebraStructure.Module;
import DISetAlgebraStructure.PartiallyVisibleNetwork;
import DISetAlgebraStructure.PortSet;
import DISetAlgebraStructure.WireConnection;
import DISetAlgebraStructure.WireFunction;
import GUI.ConsoleWindow;

/* Contains parsing functionality for the DI-Set algebra network definitions found in the
 * DI-Set Algebra tab's Main Screen */
public class ParseDISetAlgebra {

	/* This parses a full entry including module constant definitions, wire function definition and 
	 * top-level network definition. See ConsoleWindow.output calls below for more information 
	 * as to what operations are performed at each point */
	public static PartiallyVisibleNetwork parseEntireDefinition(String definitionString) throws Exception{
		
		Vector<Module> moduleDefinitions;
		PartiallyVisibleNetwork builtNetwork=null;
		WireFunction builtWireFunction=null;

		/* Filter out any spaces */
		definitionString=definitionString.replaceAll("\\s+","");
		
		/* Filter out any new lines */
		definitionString=definitionString.replaceAll("\n","");

		/* Separate into lines via semi-colons */
		String[] linesOfDefinition = definitionString.split(";");

		/* If there is more than one line */
		if(linesOfDefinition.length>1){
			String[] moduleDefinitionStrings = new String[linesOfDefinition.length-2];
			for(int i=0;i<moduleDefinitionStrings.length;i++){
				moduleDefinitionStrings[i]=linesOfDefinition[i];
			}
			moduleDefinitions=parseModules(moduleDefinitionStrings);
			
			/* If the name of the 2nd to last line is not "w" (for the wire function) then there is an error*/
			if(linesOfDefinition[linesOfDefinition.length-2].charAt(0)!='w'){
				ConsoleWindow.output("Missing wire function. The second to last line should have the name \"w\"");
				throw new Exception();
			}
				
			/* Parse the wire function */
			builtWireFunction = parseWireFunction((linesOfDefinition[linesOfDefinition.length-2].split("="))[1]);

			/* Stop now if there is no wire function */
			if(builtWireFunction==null){
				ConsoleWindow.output("Missing wire function. Please define a line that is not the bottom line called \"Wire\"");
				throw new Exception();
			}

			/* Parse the network definition) */
			builtNetwork=parseNetwork(linesOfDefinition[linesOfDefinition.length-1],moduleDefinitions);	
			builtNetwork.getBus().setWireFunction(builtWireFunction);
			
			return builtNetwork;
		}
		else{
			ConsoleWindow.output("Only one line in definition. Expecting at least three. (one or more module definitions, a wire function, and a network definition)");
			throw new Exception();
		}

	}

	/* This parses the set of constant definitions. It is similar to the parsing of Set Notation module definitions
	 * in ParseSetNotationModule. See ConsoleWindow.output calls below for more information 
	 * as to what operations are performed at each point */
	public static Vector<Module> parseModules(String[] moduleDefinitionStrings) throws Exception{
		Vector<Module> moduleDefinitions = new Vector<Module>();

		/* Variable to store state name once detected */
		String stateName;

		ConsoleWindow.output("Building list of module state names");
		
		/* For each line */
		for(int i=0;i<moduleDefinitionStrings.length;i++){

			/* Check the line is not empty */
			if(moduleDefinitionStrings[i].length()==0){
				ConsoleWindow.output("Empty line defined. Please tidy");
				throw new Exception();
			}

			/* Section for retrieving the state name */

			String[] splitAtEquals = moduleDefinitionStrings[i].split("\\Q=\\E");
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
			
			for(int j=0;j<moduleDefinitions.size();j++){
				if(moduleDefinitions.get(j).getName().equals(stateName)){
					ConsoleWindow.output("Duplicate module state definition. Parsing cancelled");
					throw new Exception();
				}
			}
			
			moduleDefinitions.add(new Module(stateName));
			
		}
		ConsoleWindow.output("List of state names built successfully");

		/* For every line */
		for(int i=0; i<moduleDefinitionStrings.length;i++){

			Module builtModule = moduleDefinitions.get(i);
			
			/* Retrieve the state name for this line, and then use that to
			 * calculate the index to begin parsing from */
			stateName=moduleDefinitions.get(i).getName();
			
			String stateBody = moduleDefinitionStrings[i].substring(stateName.length()+1, moduleDefinitionStrings[i].length());

			ConsoleWindow.output("Reading data for "+i+"th module state line:" + stateName);
			ConsoleWindow.output("Parsing list of actions for module definition line");

			String[] listOfActions = stateBody.split("\\Q+\\E");
			for(int j=0;j<listOfActions.length;j++){
				
				PortSet currentInputSet = new PortSet();
				PortSet currentOutputSet = new PortSet();
				
				ConsoleWindow.output("Parsing new action for module definition line. String is \""+listOfActions[j]+"\"");
				
				String[] splitAtStateDot = listOfActions[j].split("\\Q.\\E");
				if(splitAtStateDot.length!=2){
					ConsoleWindow.output("Error parsing action. Problem detecting dot separating IO set and resulting state");
					throw new Exception();
				}
				String IOsetWithBrackets =splitAtStateDot[0];
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
					ConsoleWindow.output("Empty input set detected");
				}
				else{
					String[] inputItems = IOsplitAtComma[0].substring(1,IOsplitAtComma[0].length()).split(",");
					for(int k=0;k<inputItems.length;k++){
						if(!GeneralOperations.alphaNumeric(inputItems[k],false)){
							ConsoleWindow.output("Non alpha-numeric char detected in input name. Parsing cancelled");
							throw new Exception();
						}
						String freshInputPort = inputItems[k];
						if(currentInputSet.contains(freshInputPort)){
							ConsoleWindow.output("Multiset input detected with input "+freshInputPort+". Parsing cancelled");
							throw new Exception();
						}
						currentInputSet.addPort(freshInputPort);
					}
				}
				
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
						String freshOutputPort = outputItems[k];
						if(currentOutputSet.contains(freshOutputPort)){
							ConsoleWindow.output("Multiset input detected with output "+freshOutputPort+". Parsing cancelled");
							throw new Exception();
						}
						currentOutputSet.addPort(freshOutputPort);
					}
				}
				
				if(currentInputSet.getNoOfPorts()==0 && currentOutputSet.getNoOfPorts()==0){
					ConsoleWindow.output("Input and output sets are both empty. Not allowed in this software! Parsing cancelled");
					throw new Exception();
				}
				
				int resultingStateIndex=-1;
				
				for(int k=0;k<moduleDefinitions.size();k++){
					if(moduleDefinitions.get(k).getName().equals(currentResultState)){
						resultingStateIndex=k;
					}
				}
				
				if(resultingStateIndex==-1){
					ConsoleWindow.output("Resulting state in current action undefined: "+currentResultState+". Parsing cancelled");
					throw new Exception();
				}

				/* Build action */
				IOAction currentAction = new IOAction(currentInputSet,currentOutputSet,moduleDefinitions.get(resultingStateIndex));
				builtModule.addAction(currentAction);

				ConsoleWindow.output("Successfully parsed action: "+currentAction.printAction());
			}

			ConsoleWindow.output("Parsing of module state line completed successfully, module is: "+builtModule.printModuleLine());
		}

		return moduleDefinitions;
	}
	
	/* This parses the wire function definition. See ConsoleWindow.output calls below for more information 
	 * as to what operations are performed at each point */
	private static WireFunction parseWireFunction(String definitionString) throws Exception{
		ConsoleWindow.output("Attempting to parse wire function definition");
		WireFunction builtWireFunction = new WireFunction();
		if(!(definitionString.charAt(0)=='{') | !(definitionString.charAt(definitionString.length()-1)=='}')){
			ConsoleWindow.output("Problem with outermost brackets of wire function");
			throw new Exception();
		}
		else if(definitionString.length()>2){
			
			String definitionStringWithoutBrackets=definitionString.substring(1, definitionString.length()-1); 
			String[] listOfPairs = definitionStringWithoutBrackets.split("\\Q),\\E");
			for(int i=0;i<listOfPairs.length;i++){
				ConsoleWindow.output("Attempting to parse pair within function");
				String currentPair = listOfPairs[i];
				if(i<listOfPairs.length-1){
					currentPair=currentPair+")";
				}
				if(!(currentPair.charAt(0)=='(') | !(currentPair.charAt(currentPair.length()-1)==')')){
					ConsoleWindow.output("Problem with brackets around pair");
					throw new Exception();
				}
				String currentPairWithoutBrackets = currentPair.substring(1, currentPair.length()-1);
				String[] bothComponents = currentPairWithoutBrackets.split(",");
				if(bothComponents.length!=2){
					ConsoleWindow.output("Problem with dividing semi-colon within pair");
					throw new Exception();
				}
				String leftComponent = bothComponents[0];
				String rightComponent=bothComponents[1];
				String[] leftSubComponents = leftComponent.split(":");
				if(leftSubComponents.length!=2){
					ConsoleWindow.output("Problem with labelled port colon in left component of pair");
					throw new Exception();
				}
				if(!GeneralOperations.alphaNumeric(leftSubComponents[0],false)){
					ConsoleWindow.output("Non alpha-numeric char detected in left component of pair's port name. Parsing cancelled");
					throw new Exception();
				}
				if(!GeneralOperations.alphaNumeric(leftSubComponents[1],false)){
					ConsoleWindow.output("Non alpha-numeric char detected in left component of pair's port label. Parsing cancelled");
					throw new Exception();
				}
				String[] rightSubComponents = rightComponent.split(":");
				if(rightSubComponents.length!=2){
					ConsoleWindow.output("Problem with labelled port colon in right component of pair");
					throw new Exception();
				}
				if(!GeneralOperations.alphaNumeric(rightSubComponents[0],false)){
					ConsoleWindow.output("Non alpha-numeric char detected in right component of pair's port name. Parsing cancelled");
					throw new Exception();
				}
				if(!GeneralOperations.alphaNumeric(rightSubComponents[1],false)){
					ConsoleWindow.output("Non alpha-numeric char detected in right component of pair's port label. Parsing cancelled");
					throw new Exception();
				}
				String leftPortName = leftSubComponents[0];
				NamedPort leftLabelledPort = new NamedPort(leftPortName,leftSubComponents[1]);
				String rightPortName = rightSubComponents[0];
				NamedPort rightLabelledPort = new NamedPort(rightPortName,rightSubComponents[1]);
				WireConnection currentConnection = new WireConnection(leftLabelledPort,rightLabelledPort);
				builtWireFunction.addConnection(currentConnection);

				ConsoleWindow.output("Successfully parsed pair in wire function: "+currentConnection.printPair());
			}
		}
		ConsoleWindow.output("Successfully parsed wire function: "+builtWireFunction.print());
		return builtWireFunction;
	}
	
	/* This parses the top-level network term. It parses the main section, bus contents, 
	 * and port-hiding sets separately using more methods defined below. 
	 * See ConsoleWindow.output calls below for more information as to what operations 
	 * are performed at each point */
	public static PartiallyVisibleNetwork parseNetwork(String definitionString, Vector<Module> moduleDefinitions) throws Exception{
		
		PartiallyVisibleNetwork builtNetwork = new PartiallyVisibleNetwork();
		builtNetwork.setConstantDefinitions(moduleDefinitions);
		
		String[] splitAtEquals = definitionString.split("=");
		if(splitAtEquals.length!=2){
			return null;
		}
		
		String networkName=splitAtEquals[0];
		
		if(!GeneralOperations.alphaNumeric(networkName,false)){
			ConsoleWindow.output("Non alpha-numeric char detected in network name. Parsing cancelled");
			throw new Exception();
		}
		
		ConsoleWindow.output("Name of network: "+networkName);		
	
		builtNetwork.setName(networkName);
		
		String networkBody=splitAtEquals[1];
		String[] moduleAndBusSplit = networkBody.split("\\Q||\\E");
		
		if(moduleAndBusSplit.length!=2){
			ConsoleWindow.output("Problem detecting parallel communication bag operator \"||\". Parsing cancelled");
			throw new Exception();
		}
		
		String modulesPart= moduleAndBusSplit[0];
		String busAndHiddenPart=moduleAndBusSplit[1];
		String[] busAndHiddenSplit=busAndHiddenPart.split("-");
		if(busAndHiddenSplit.length!=2){
			ConsoleWindow.output("Problem detecting port hiding operator \"-\". Parsing cancelled");
			throw new Exception();
		}
		
		String[] listOfLabelledModules = modulesPart.split("\\Q|\\E");
		String busString = busAndHiddenSplit[0];
		String hiddenString = busAndHiddenSplit[1];
		
		ConsoleWindow.output("Attempting to parse modules in network");
		
		for(int i=0;i<listOfLabelledModules.length;i++){
			
			ConsoleWindow.output("Attempting to parse new module");
			
			String[] moduleSplitAtColon = listOfLabelledModules[i].split(":");
			if(moduleSplitAtColon.length!=2){
				ConsoleWindow.output("Problem detecting module label operator \":\". Parsing cancelled");
				throw new Exception();
			}
			String bracketedModule=moduleSplitAtColon[0];

			if(bracketedModule.charAt(0)!='(' || bracketedModule.charAt(bracketedModule.length()-1)!=')'){
				ConsoleWindow.output("Problem parsing bracket around module constant. Parsing cancelled");
				throw new Exception();
			}
			
			String moduleString = bracketedModule.substring(1,bracketedModule.length()-1);
			if(!GeneralOperations.alphaNumeric(moduleString,false)){
				ConsoleWindow.output("Non alpha-numeric char detected in module constant. Parsing cancelled");
				throw new Exception();
			}
			
			ConsoleWindow.output("Module constant detected: "+moduleString);
			
			boolean defined=false;
			int index=0;
			for(int j=0;j<builtNetwork.getNoOfConstantDefinitions();j++){
				if(builtNetwork.getConstantDefinition(j).getName().equals(moduleString)){
					defined=true;
					index=j;
				}
			}
			if(!defined){
				ConsoleWindow.output("Module constant "+moduleString +" is not defined. Parsing cancelled");
				throw new Exception();
			}
			
			String moduleLabel = moduleSplitAtColon[1];
			
			if(!GeneralOperations.alphaNumeric(moduleLabel,false)){
				ConsoleWindow.output("Non alpha-numeric char detected in module label. Parsing cancelled");
				throw new Exception();
			}
			
			NamedModule labelledModule = new NamedModule(builtNetwork.getConstantDefinition(index),moduleLabel);
			builtNetwork.addModuleInstance(labelledModule);
			ConsoleWindow.output("Labelled module "+labelledModule.getModuleName() +":"+ labelledModule.getModuleLabel()+" detected");
		}
		ConsoleWindow.output("Attempting to parse communication bus");
		
		Bus builtBus = null;
		if(busString.charAt(busString.length()-1)!='w'){
			ConsoleWindow.output("Missing \"w\" at end of bus definition. Please add for formality");
			throw new Exception();
		}
		if(busString.charAt(0)!='{' || busString.charAt(busString.length()-2)!='}'){
			ConsoleWindow.output("Problem parsing bracket around bus definition. Parsing cancelled");
			throw new Exception();
		}
		if(busString.length()>3){
			busString=busString.substring(1,busString.length()-2);
		}
		else{
			busString="";
		}

		builtBus = parseBus(busString);		
		builtNetwork.setBus(builtBus);
			
		if(hiddenString.charAt(0)!='{' || hiddenString.charAt(hiddenString.length()-1)!='}'){
			ConsoleWindow.output("Problem parsing bracket around hidden ports set. Parsing cancelled");
			throw new Exception();
		}
		if(hiddenString.length()>2){
			hiddenString=hiddenString.substring(1,hiddenString.length()-1);
		}
		else{
			hiddenString="";
		}
		
		NamedPortSet hiddenSet = parseHiddenPorts(hiddenString);
		builtNetwork.setHiddenPorts(hiddenSet);
		
		return builtNetwork;
	}

	/* Parses the bus contents in the top-level network term */
	public static Bus parseBus(String busString) throws Exception{
		Bus builtBus = new Bus();
		if(!busString.equals("")){
			ConsoleWindow.output("Non-empty bus detected");
			String[] busItems = busString.split(",");
			for(int i=0;i<busItems.length;i++){
				NamedPort currentLabelledPort = parseLabelledPort(busItems[i],true);
				builtBus.addSignal(currentLabelledPort);
				ConsoleWindow.output("Signal successfully added on labelled port: "+currentLabelledPort.getPort());
			}
		}
		else{
			ConsoleWindow.output("Empty bus detected- this is ok");
		}
		
		return builtBus;
	}
	
	/* Parses the set of hidden ports in the top-level network term */
	public static NamedPortSet parseHiddenPorts(String hiddenString) throws Exception{
		NamedPortSet portSet = new NamedPortSet();
		if(!hiddenString.equals("")){
			ConsoleWindow.output("Non-empty hidden ports set detected");
			String[] hiddenItems = hiddenString.split(",");
			for(int i=0;i<hiddenItems.length;i++){
				NamedPort currentLabelledPort = parseLabelledPort(hiddenItems[i],true);
				portSet.add(currentLabelledPort);
				ConsoleWindow.output("Port hiding added successfully: "+currentLabelledPort.getPort());
			}
		}
		else{
			ConsoleWindow.output("Empty hidden ports set detected- this is ok");
		}
		return portSet;
	}

	/* Parses a labelled port. This is used by the parsing methods for the wire function, bus contents,
	 * and set of hidden ports */
	public static NamedPort parseLabelledPort(String portString, boolean wildcard)throws Exception{
		String[] splitAtColon = portString.split(":");
		if(splitAtColon.length!=2){
			ConsoleWindow.output("Problem detecting label operator \":\" in port component");
			throw new Exception();
		}
			
		String portName=splitAtColon[0];	
		if(!GeneralOperations.alphaNumeric(portName,false) && !(portName.length()==1 && portName.charAt(0)=='*' && wildcard)){
			ConsoleWindow.output("Non alpha-numeric char and non-wildcard (*) (if allowed) detected in port name. Parsing cancelled");
			throw new Exception();
		}
			
		String portLabel=splitAtColon[1];
		
		if(!GeneralOperations.alphaNumeric(portLabel,false)){
			ConsoleWindow.output("Non alpha-numeric char detected in port label. Parsing cancelled");
			throw new Exception();
		}
		return new NamedPort(portName,portLabel);
	}
}
