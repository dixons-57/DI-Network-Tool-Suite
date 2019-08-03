package DISetAlgebraOperations;

import DISetAlgebraLTSStructure.LTSDefinition;
import DISetAlgebraLTSStructure.LTSState;
import DISetAlgebraStructure.NamedModule;
import DISetAlgebraStructure.NamedPort;
import DISetAlgebraStructure.NamedPortSet;
import GUI.ConsoleWindow;

/* Contains operations for analysing clash and safety properties of an LTS shown in the DI-Set Algebra tab's LTS screen */
public class LTSAnalysis {

	/* Analyses for clashing and then safety in turn */
	public static boolean[] analyseProperties(LTSDefinition definition){
		boolean[] properties = new boolean[2];
		if(clashAnalysis(definition)){
			properties[0]=true;
		}
		else{
			properties[0]=false;
		}
		if(safetyAnalysis(definition)){
			properties[1]=true;
		}
		else{
			properties[1]=false;
		}
		return properties;
	}

	/* This checks for clashing of the LTS. It iterates through each state of the LTS
	 * and then checks that the bus does not contain more than one instance of each
	 * named output port */
	private static boolean clashAnalysis(LTSDefinition definition) {
		ConsoleWindow.output("Checking LTS for signal clashes");
		for(int i=0; i<definition.getNoOfStates();i++){
			LTSState stateToCheck = definition.getState(i);
			ConsoleWindow.output("Checking state number "+stateToCheck.getStateNo());
			NamedPortSet currentStateBus=((stateToCheck.getNetworkTerm().getBus())).getContents();
			
			for(int j=0;j<currentStateBus.getNoOfPorts()-1;j++){
				NamedPort signalToCheck = currentStateBus.getPort(j);
				for(int k=j+1;k<currentStateBus.getNoOfPorts();k++){
					if(signalToCheck.sameAs(currentStateBus.getPort(k))){
						ConsoleWindow.output("Signal clash in state "+i +" via "+signalToCheck.print());
						return true;
					}
				}
			}
			ConsoleWindow.output("State number "+stateToCheck.getStateNo()+" is clear of signal clashes");
		}
		ConsoleWindow.output("LTS clear of signal clashes");
		return false;
	}

	/* This checks for safety of the LTS. It iterates through each state of the LTS, then each
	 * named module, filters the set of input signals for the given module label, and checks that
	 * safety holds in the usual way (i.e. an action is defined with an input set which is a superset
	 * of those which are pending, provided that the module is not in an intermediate "bullet" state) */
	private static boolean safetyAnalysis(LTSDefinition definition) {
		ConsoleWindow.output("Checking LTS for safety");
		for(int i=0; i<definition.getNoOfStates();i++){
			LTSState stateToCheck = definition.getState(i);
			ConsoleWindow.output("Checking state number "+stateToCheck.getStateNo());
			for(int j=0;j<stateToCheck.getNetworkTerm().getNoOfModuleInstances();j++){
				NamedModule currentModuleToCheckSafety = stateToCheck.getNetworkTerm().getModuleInstance(j);
				NamedPortSet inputSignalsForThisModule = stateToCheck.getNetworkTerm().getBus().getPortsWithLabel(currentModuleToCheckSafety.getModuleLabel());
				if(!currentModuleToCheckSafety.checkSafety(inputSignalsForThisModule)){
					ConsoleWindow.output("Safety violated in state "+i+" of module "
							+currentModuleToCheckSafety.getModuleLabel() +" with set of inputs "+inputSignalsForThisModule.printSet());
					return false;
				}
			}
			ConsoleWindow.output("State number "+stateToCheck.getStateNo()+" does not contain non-safety for any module");
		}
		ConsoleWindow.output("LTS/Network is always safe");
		return true;
	}

}
