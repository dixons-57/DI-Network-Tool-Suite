package DISetAlgebraOperations;

import java.util.Vector;

import DISetAlgebraLTSStructure.Transition;
import DISetAlgebraStructure.IOAction;
import DISetAlgebraStructure.NamedModule;
import DISetAlgebraStructure.NamedPort;
import DISetAlgebraStructure.NamedPortSet;
import DISetAlgebraStructure.PartiallyVisibleNetwork;
import DISetAlgebraStructure.PortSet;
import GUI.ConsoleWindow;

/* Includes operations for inferring available transitions in DI-Set algebra, as required in the DI-Set Algebra tab's various screens */
public class CalculateTransitions{
	
	/* Calculates input and output transitions separately */
	public static Vector<Transition> calculateTransitions(PartiallyVisibleNetwork networkDefinition){
		Vector<Transition> availableTransitions = new Vector<Transition>();
		
		ConsoleWindow.output("Calculating available input transitions");
		
		availableTransitions = calculateInputTransitions(networkDefinition,availableTransitions);
		
		ConsoleWindow.output("Calculating available output transitions");
		
		availableTransitions = calculateOutputTransitions(networkDefinition,availableTransitions);

		return availableTransitions;
	}
	
	/* Calculates all possible "input" transitions available for a network term */
	private static Vector<Transition> calculateInputTransitions(PartiallyVisibleNetwork networkDefinition, Vector<Transition> availableTransitions) {
		
		/* Iterate through every named module in the term */
		for(int i=0;i<networkDefinition.getNoOfModuleInstances();i++){
			NamedModule currentModule = networkDefinition.getModuleInstance(i);
			NamedPortSet signalsToModule = networkDefinition.getBus().getPortsWithLabel(currentModule.getModuleLabel());
			
			/* Iterate through all actions in the named module */
			for(int j=0;j<currentModule.getModuleState().getNoOfActions();j++){
				IOAction currentAction = currentModule.getModuleState().getAction(j);
				Vector<IOAction> otherActions = new Vector<IOAction>();
				
				/* If the action has a non-empty input set, then clearly it can
				 * process inputs, therefore this action is of further interest and may lead to an input transition */
				if(currentAction.getNoOfInputs()>0){
					for(int k=0;k<currentModule.getModuleState().getNoOfActions();k++){
						if(j!=k){
							otherActions.add(currentModule.getModuleState().getAction(k));
						}
					}
					
					/* This performs a simple check whether the correct named input ports are present
					 * in the bus in order for this action to process */
					if(signalsToModule.contains(currentAction.getInputSet())){
						
						StringBuffer involvedModuleDetails = new StringBuffer();
						involvedModuleDetails.append("(");
						if(!currentModule.getModuleName().equals("")){
							involvedModuleDetails.append(currentModule.getModuleName());
						}
						else{
							involvedModuleDetails.append(currentModule.printModuleLine());
						}
						involvedModuleDetails.append("):"+currentModule.getModuleLabel());
						
						ConsoleWindow.output("Input transition available involving module "+involvedModuleDetails.toString()+" via inputs "+
								currentAction.getInputSet().printPorts());
						
						/* Creates the actual transition object */
						Transition inputTransition = new Transition();
						
						/* Set the source of the transition as the old network term */
						inputTransition.setSourceStateTerm(networkDefinition);
						
						/* For now, let the target be the same as the source */
						PartiallyVisibleNetwork resultingNetwork = networkDefinition.copy();
						
						/* Work out the set of labels to put on the transition (the same as the input set of the action,
						 * but with module label affixed */
						NamedPortSet acceptedInputs = new NamedPortSet();
						for(int k=0;k<currentAction.getNoOfInputs();k++){
							acceptedInputs.add(new NamedPort(currentAction.getInputPort(k),currentModule.getModuleLabel()));
						}				
						
						/* Strip out these signals from the bus */
						resultingNetwork.getBus().removeMultipleSignals(acceptedInputs);
						
						/* Add all non-hidden labels to the transition */
						NamedPortSet sideEffects = new NamedPortSet();
						for(int k=0;k<acceptedInputs.getNoOfPorts();k++){
							if(!networkDefinition.getHiddenPorts().contains(acceptedInputs.getPort(k))){
								sideEffects.add(acceptedInputs.getPort(k).deepCopy());
							}
						}
						if(sideEffects.getNoOfPorts()>0){
							inputTransition.setTransitionLabel(sideEffects);
							inputTransition.setType(0);
						}
						
						/* Set the transition type to tau if the transition label is now empty after
						 * stripping out hidden ports */
						else{
							inputTransition.setType(2);
						}

						/* Modify resulting network for the transition */
						
						/* If the output for the action is non-empty, then apply the inputs to the module, 
						 * discarding all other actions in the summation, and also remove the module 
						 * constant identifier (it now exists in an intermediate state, and does not match
						 * up with our defined state constants) */
						if(currentAction.getNoOfOutputs()>0){
							NamedModule resultingModule = resultingNetwork.getModuleInstance(i);
							IOAction resultingAction = resultingModule.getModuleState().getAction(j);
							resultingAction.clearInputs();
							resultingAction.setIntermediate();				
							
							resultingModule.setModuleName("");
							resultingModule.getModuleState().clearActions();
							resultingModule.getModuleState().addAction(resultingAction);
						}

						/* If the output was empty, immediately perform a rewrite and replace the named module 
						 * with the target state's constant, while propagating the module label */
						else{
							resultingNetwork.replaceModuleInstance(i, new NamedModule(currentAction.getResult(),currentModule.getModuleLabel()));
						}
						
						inputTransition.setTargetStateTerm(resultingNetwork);
						availableTransitions.addElement(inputTransition);
					}
				}
			}
		}
		return availableTransitions;
	}

	/* Calculates all possible "output" transitions available for a network term. Note that if an action has an empty input set, then
	 * it necessarily produces a non-empty output set, as all actions which have empty output sets are rewritten as soon as the input set
	 * is cleared. Therefore an empty input set guarantees a non-empty output set when examining actions using this method */
	private static Vector<Transition> calculateOutputTransitions(PartiallyVisibleNetwork networkDefinition, Vector<Transition> availableTransitions) {
		
		/* Iterate through every named module in the term */
		for(int i=0;i<networkDefinition.getNoOfModuleInstances();i++){
			NamedModule currentModule = networkDefinition.getModuleInstance(i);
			
			/* Iterate through all actions in the named module */
			for(int j=0;j<currentModule.getModuleState().getNoOfActions();j++){
				IOAction currentAction = currentModule.getModuleState().getAction(j);
				
				/* If the action has an empty input set, then clearly it can
				 * produce outputs, therefore this action is of further interest and will lead to an output transition */
				if(currentAction.getNoOfInputs()==0){

					StringBuffer involvedModuleDetails = new StringBuffer();
					involvedModuleDetails.append("(");
					if(!currentModule.getModuleName().equals("")){
						involvedModuleDetails.append(currentModule.getModuleName());
					}
					else{
						involvedModuleDetails.append(currentModule.printModuleLine());
					}
					involvedModuleDetails.append("):"+currentModule.getModuleLabel());
					
					ConsoleWindow.output("Output transition available involving module "+involvedModuleDetails.toString()+
							" via outputs "+currentAction.getOutputSet().printPorts());
					
					/* Creates the actual transition object */
					Transition outputTransition = new Transition();
					
					/* Set the source of the transition as the old network term */
					outputTransition.setSourceStateTerm(networkDefinition);
					
					/* For now, let the target be the same as the source */
					PartiallyVisibleNetwork resultingNetwork = networkDefinition.copy();
					PortSet actionOutputs =currentAction.getOutputSet();
					
					/* Work out the set of labels to put on the transition (the same as the output set of the action,
					 * but with module label affixed */
					NamedPortSet producedOutputs = new NamedPortSet();
					for(int k=0;k<actionOutputs.getNoOfPorts();k++){
						producedOutputs.add(new NamedPort(actionOutputs.getPort(k),currentModule.getModuleLabel()));
					}
					
					/* Add all non-hidden labels to the transition */
					NamedPortSet sideEffects = new NamedPortSet();
					for(int k=0;k<producedOutputs.getNoOfPorts();k++){
						if(!networkDefinition.getHiddenPorts().contains(producedOutputs.getPort(k))){
							sideEffects.add(producedOutputs.getPort(k).deepCopy());
						}
					}
					if(sideEffects.getNoOfPorts()>0){
						outputTransition.setTransitionLabel(sideEffects);
						outputTransition.setType(1);
					}
					
					/* Set the transition type to tau if the transition label is now empty after
					 * stripping out hidden ports */
					else{
						outputTransition.setType(2);
					}
					NamedPortSet relabelledOutputs = new NamedPortSet();
						
					/* Relabel each output port to the new port specified by the target of the wire function */
					for(int k=0;k<actionOutputs.getNoOfPorts();k++){
						relabelledOutputs.add(networkDefinition.getBus().getWireFunction().getTarget
						(new NamedPort(actionOutputs.getPort(k),currentModule.getModuleLabel())));
					}
					resultingNetwork.getBus().addMultipleSignals(relabelledOutputs);
				
					/* Perform a rewrite and replace the named module 
					 * with the target state's constant, while propagating the module label */
					NamedModule resultingModule = resultingNetwork.getModuleInstance(i);
					resultingNetwork.replaceModuleInstance(i,new NamedModule
							(currentAction.getResult(),resultingModule.getModuleLabel()));
					outputTransition.setTargetStateTerm(resultingNetwork);
					availableTransitions.addElement(outputTransition);
				}
			}
		}
		return availableTransitions;
	}

}
