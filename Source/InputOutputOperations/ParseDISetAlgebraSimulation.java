package InputOutputOperations;

import DISetAlgebraLTSStructure.LTSDefinition;
import DISetAlgebraLTSStructure.Simulation;
import DISetAlgebraLTSStructure.SimulationPair;
import GUI.ConsoleWindow;

/* Contains parsing functionality for the (bi)simulation definitions found in the
 * DI-Set Algebra tab's LTS screen */
public class ParseDISetAlgebraSimulation {

	/* Parses the (bi)simulation definition given as a giant string (stripping out appropriate
	 * white spaces, newline characters etc.), and checks syntactic correctness (i.e. LTS state integer
	 * values are accurate), but not validity of the relation. See ConsoleWindow.output calls below for more
	 * information as to what operations are performed at each point */
	public static Simulation parseDefinition(String definitionString, LTSDefinition left, LTSDefinition right) throws Exception{
		ConsoleWindow.output("Attempting to parse bisimulation definition");
		
		/* Filter out any spaces */
		definitionString=definitionString.replaceAll("\\s+","");
		
		/* Filter out any new lines */
		definitionString=definitionString.replaceAll("\n","");

		/* Separate into lines via semi-colons */
		String[] linesOfInput = definitionString.split(";");
		
		Simulation simulationDefinition = new Simulation();
		
		/* Parse each state pair line-by-line */
		for(int i=0;i<linesOfInput.length;i++){
			String pairWithBrackets = linesOfInput[i];
			if(!(pairWithBrackets.charAt(0)=='(') | !(pairWithBrackets.charAt(pairWithBrackets.length()-1)==')')){
				ConsoleWindow.output("Problem with brackets of state pair");
				throw new Exception();
			}
			String pair = pairWithBrackets.substring(1,pairWithBrackets.length()-1);
			String[] bothComponents = pair.split(",");
			if(bothComponents.length!=2){
				ConsoleWindow.output("Problem with comma of state pair");
				throw new Exception();
			}
			int leftState = Integer.parseInt(bothComponents[0]);
			int rightState = Integer.parseInt(bothComponents[1]);
			if(leftState<0 || leftState>=left.getNoOfStates()){
				ConsoleWindow.output("Left component of pair is not a valid state in the left LTS");
				throw new Exception();
			}
			if(rightState<0 || rightState>=right.getNoOfStates()){
				ConsoleWindow.output("Right component of pair is not a valid state in the right LTS");
				throw new Exception();
			}
			simulationDefinition.addStatePair(new SimulationPair(left.getState(leftState),right.getState(rightState)));
		}
		if(simulationDefinition.checkDuplicates()){
			ConsoleWindow.output("Duplicate pairs detected. Please fix. Parsing cancelled");
			throw new Exception();
		}
		return simulationDefinition;
	
	}

}
