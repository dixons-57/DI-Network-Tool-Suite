package GUIListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import GUI.ConsoleWindow;
import GUI.DISetAlgebraGUIInputScreen;
import GUI.DISetAlgebraTab;
import InputOutputOperations.GeneralOperations;

/* Process GUI events from the DI-Set Algebra tab's GUI Input screen. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class DISetAlgebraGUIInputListener implements ActionListener, ListSelectionListener {

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static DISetAlgebraGUIInputListener instance = new DISetAlgebraGUIInputListener();
	
	/* Records whether the left network term or right network term is being build by the GUI Input screen */
	private int buildingNetwork=0;
	
	/* Handles JButton events, disabling/re-enabling GUI elements appropriately. */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		/* Closes the GUI Input screen and returns the DI-Set Algebra tab to the Main screen */
		if(command.equals("Cancel")){
			DISetAlgebraTab.instance.showMainScreen();
		}
		
		/* Adds the module selected in the GUI list to the DI-Set algebra network term, after prompting for a label from the user */
		else if(command.equals("Add Module")){
			while(true){
				String moduleLabelInput = JOptionPane.showInputDialog("Please enter a module identifier using only alphanumeric characters");
				if(moduleLabelInput==null){
					ConsoleWindow.output("Adding of module cancelled by user");
					break;
				}
				else if(GeneralOperations.alphaNumeric(moduleLabelInput,false) && !moduleLabelInput.equals("")){
					String labelledModuleEntry =DISetAlgebraGUIInputScreen.instance.SelectModulesList.getSelectedValue()+":"+moduleLabelInput;
					DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.addElement(labelledModuleEntry);
					DISetAlgebraGUIInputScreen.instance.SourceModuleListModel.addElement(labelledModuleEntry);
					DISetAlgebraGUIInputScreen.instance.TargetModuleListModel.addElement(labelledModuleEntry);
					DISetAlgebraGUIInputScreen.instance.BuildNetworkButton.setEnabled(true);
					break;
				}
				ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
			}
		}
		
		/* Removes the named module selected in the GUI list from the DI-Set algebra network term */
		else if(command.equals("Remove Module")){
			int chosenIndex = DISetAlgebraGUIInputScreen.instance.ChosenModulesList.getSelectedIndex();
			if(chosenIndex==DISetAlgebraGUIInputScreen.instance.SourceModuleList.getSelectedIndex()){
				DISetAlgebraGUIInputScreen.instance.SourceOutputListModel.clear();
				DISetAlgebraGUIInputScreen.instance.ConnectButton.setEnabled(false);
				DISetAlgebraGUIInputScreen.instance.HideOutputButton.setEnabled(false);
			}
			if(chosenIndex==DISetAlgebraGUIInputScreen.instance.TargetModuleList.getSelectedIndex()){
				DISetAlgebraGUIInputScreen.instance.TargetInputListModel.clear();
				DISetAlgebraGUIInputScreen.instance.ConnectButton.setEnabled(false);
				DISetAlgebraGUIInputScreen.instance.HideInputButton.setEnabled(false);
			}
			DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.removeElementAt(chosenIndex);
			DISetAlgebraGUIInputScreen.instance.SourceModuleListModel.removeElementAt(chosenIndex);
			DISetAlgebraGUIInputScreen.instance.TargetModuleListModel.removeElementAt(chosenIndex);
			DISetAlgebraGUIInputScreen.instance.ChosenModulesList.setSelectedIndex(-1);
			DISetAlgebraGUIInputScreen.instance.RemoveModuleButton.setEnabled(false);		
		}
		
		/* Connects the two selected named ports (one an output of a selected named module, the other an input
		 * of a selected named module) together by adding a generated entry to the wire function */
		else if(command.equals("Connect")){
			if(!DISetAlgebraGUIInputScreen.instance.SourceModuleList.isSelectionEmpty() && 
					!DISetAlgebraGUIInputScreen.instance.SourceOutputList.isSelectionEmpty() && 
					!DISetAlgebraGUIInputScreen.instance.TargetModuleList.isSelectionEmpty() && 
					!DISetAlgebraGUIInputScreen.instance.TargetInputList.isSelectionEmpty()){
				String leftPortName = DISetAlgebraGUIInputScreen.instance.SourceOutputList.getSelectedValue();
				String leftPortLabel = DISetAlgebraGUIInputScreen.instance.SourceModuleList.getSelectedValue().split(":")[1];
				String rightPortName = DISetAlgebraGUIInputScreen.instance.TargetInputList.getSelectedValue();
				String rightPortLabel = DISetAlgebraGUIInputScreen.instance.TargetModuleList.getSelectedValue().split(":")[1];
				DISetAlgebraGUIInputScreen.instance.WireFunctionListModel.addElement("("+leftPortName+":"+leftPortLabel+","+rightPortName+":"+rightPortLabel+")");
			}
		}
		
		/* Removes the selected connection entry from the wire function */
		else if(command.equals("Remove Connection")){
			if(!DISetAlgebraGUIInputScreen.instance.WireFunctionList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.RemoveConnectionButton.setEnabled(false);	
				DISetAlgebraGUIInputScreen.instance.PlaceSignalButton.setEnabled(false);	
				DISetAlgebraGUIInputScreen.instance.WireFunctionListModel.removeElementAt(DISetAlgebraGUIInputScreen.instance.WireFunctionList.getSelectedIndex());
				DISetAlgebraGUIInputScreen.instance.WireFunctionList.setSelectedIndex(-1);
			}
		}
		
		/* Adds the selected entry in the wire function, to the contents of the bus definition. In effect this "places"
		 * a signal of the selected named port in the bus contents */
		else if(command.equals("Place Signal")){
			if(!DISetAlgebraGUIInputScreen.instance.WireFunctionList.isSelectionEmpty()){
				String selectedPair =DISetAlgebraGUIInputScreen.instance.WireFunctionList.getSelectedValue();
				String[] splitAtComma = selectedPair.split(",");
				String rightComponent = splitAtComma[1].substring(0,splitAtComma[1].length()-1);
				DISetAlgebraGUIInputScreen.instance.BusContentsListModel.addElement(rightComponent);
			}
		}
		
		/* Hides the selected named output port to the list of hidden ports in the term */
		else if(command.equals("Hide1")){
			if(!DISetAlgebraGUIInputScreen.instance.SourceOutputList.isSelectionEmpty() &&
					!DISetAlgebraGUIInputScreen.instance.SourceModuleList.isSelectionEmpty()){
				String selectedModule =DISetAlgebraGUIInputScreen.instance.SourceModuleList.getSelectedValue();
				String selectedPort =DISetAlgebraGUIInputScreen.instance.SourceOutputList.getSelectedValue();
				String[] splitAtColon = selectedModule.split(":");
				String portLabel = splitAtColon[1];
				DISetAlgebraGUIInputScreen.instance.HiddenListModel.addElement(selectedPort+":"+portLabel);
			}
		}
		
		/* Hides the selected named input port to the list of hidden ports in the term */
		else if(command.equals("Hide2")){
			if(!DISetAlgebraGUIInputScreen.instance.TargetInputList.isSelectionEmpty() &&
					!DISetAlgebraGUIInputScreen.instance.TargetModuleList.isSelectionEmpty()){
				String selectedModule =DISetAlgebraGUIInputScreen.instance.TargetModuleList.getSelectedValue();
				String selectedPort =DISetAlgebraGUIInputScreen.instance.TargetInputList.getSelectedValue();
				String[] splitAtColon = selectedModule.split(":");
				String portLabel = splitAtColon[1];
				DISetAlgebraGUIInputScreen.instance.HiddenListModel.addElement(selectedPort+":"+portLabel);
			}
		}
		
		/* Removes the selected named port from the list of hidden ports in the term */
		else if(command.equals("Remove Hiding")){
			if(!DISetAlgebraGUIInputScreen.instance.HiddenList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.RemoveHidingButton.setEnabled(false);	
				DISetAlgebraGUIInputScreen.instance.HiddenListModel.removeElementAt(DISetAlgebraGUIInputScreen.instance.HiddenList.getSelectedIndex());
				DISetAlgebraGUIInputScreen.instance.HiddenList.setSelectedIndex(-1);
			}
		}
		
		/* Removes the selected named port from the list of signals present in the bus of the term */
		else if(command.equals("Remove Signal")){
			if(!DISetAlgebraGUIInputScreen.instance.BusContentsList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.RemoveSignalButton.setEnabled(false);	
				DISetAlgebraGUIInputScreen.instance.BusContentsListModel.removeElementAt(DISetAlgebraGUIInputScreen.instance.BusContentsList.getSelectedIndex());
				DISetAlgebraGUIInputScreen.instance.BusContentsList.setSelectedIndex(-1);
			}
		}
		
		/* Initialises generation of the actual syntactic term, produced from the chosen parameters on screen */
		else if(command.equals("Build Network")){
			ConsoleWindow.output("Building DI-Syntax term from GUI information");
			buildNetwork();
			ConsoleWindow.output("Network successfully built and parsed");
			DISetAlgebraTab.instance.showMainScreen();
		}
	}

	/* Generates the entire network definition, including module constants, the wire function, and the top-level
	 * network term, based on the chosen parameters */
	private void buildNetwork() {
		StringBuffer output = new StringBuffer();
		output=addRelevantConstantDefinitions(output);
		output=addWireFunctionDefinition(output);
		output=addNetworkDefinition(output);
		DISetAlgebraMainListener.instance.loadNetwork(output.toString(), buildingNetwork);
	}

	/* Appends any relevant syntactic module constant definitions to the beginning of the network definition, 
	 * which are needed by the top-level network term. It utilises the recursive method defined below. */
	private StringBuffer addRelevantConstantDefinitions(StringBuffer output) {
		Vector<Integer> moduleDefinitionsAdded = new Vector<Integer>();
		for(int i=0;i<DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.size();i++){
			String moduleNameAndLabel = DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.getElementAt(i);
			String moduleName = moduleNameAndLabel.split(":")[0];
			recursivelyAddConstantDefinitions(moduleDefinitionsAdded,findModuleIndex(moduleName),output);
		}
		return output;
	}

	/* This recursively searches through the definitions of any modules which have been chosen by the user to be part of the network,
	 * and retrieves the list of module constant definitions that it needs (i.e. all derivative states of the chosen modules)
	 * in order to have all needed to define the term. */
	private void recursivelyAddConstantDefinitions(Vector<Integer> moduleDefinitionsAdded, int definitionIndex, StringBuffer output) {
		boolean found=false;
		for(int i=0;i<moduleDefinitionsAdded.size();i++){
			if(moduleDefinitionsAdded.get(i).intValue()==definitionIndex){
				found=true;
				break;
			}
		}
		if(!found){
			moduleDefinitionsAdded.add(new Integer(definitionIndex));
			Vector<Integer> resultStatesToTryToAdd = new Vector<Integer>();

			output.append(DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[definitionIndex][0][0]+"=");
			for(int j=0;j<DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[definitionIndex][3].length;j++){
				String currentAction = DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[definitionIndex][3][j];
				output.append(currentAction);
				String resultState = currentAction.split("\\Q.\\E")[1];
				resultStatesToTryToAdd.add(findModuleIndex(resultState));
				if(j<DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[definitionIndex][3].length-1){
					output.append("+");
				}
			}
			output.append(";\n");
			for(int j=0;j<resultStatesToTryToAdd.size();j++){
				recursivelyAddConstantDefinitions(moduleDefinitionsAdded,resultStatesToTryToAdd.get(j),output);
			}
		}
	}
	
	/* Appends the syntactic definition of the wire function to the network definition, 
	 * immediately prior to the top-level network term, based on the wire function defined in the GUI by the user */
	private StringBuffer addWireFunctionDefinition(StringBuffer output) {
		output.append("w={");
		int noOfWireFunctionItems= DISetAlgebraGUIInputScreen.instance.WireFunctionListModel.size();
		for(int i=0;i<noOfWireFunctionItems;i++){
			output.append(DISetAlgebraGUIInputScreen.instance.WireFunctionListModel.getElementAt(i));
			if(i<noOfWireFunctionItems-1){
				output.append(",");
			}
		}
		output.append("};\n");
		return output;
	}
	
	/* Finds the index of the given constant definition (identified by its name) in the software's overall
	 * list of pre-included constant definitions */
	private int findModuleIndex(String moduleName) {
		for(int i=0;i<DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions.length;i++){
			if(DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[i][0][0].equals(moduleName)){
				return i;
			}
		}
		return -1;
	}

	/* Generates the actual syntactic top-level term based on the chosen parameters, and appends it to the overall
	 * network definition. It simply iterates through all named modules and bus contents, appending them to the same string */
	private StringBuffer addNetworkDefinition(StringBuffer output) {
		output.append("Network = ");
		int noOfLabelledModules=DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.size();
		for(int i=0;i<noOfLabelledModules;i++){
			String moduleAndLabel = DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.getElementAt(i);
			String[] splitAtColon = moduleAndLabel.split(":");
			String labelledModuleString = "("+splitAtColon[0]+"):"+splitAtColon[1];
			output.append(labelledModuleString);
			if(i<noOfLabelledModules-1){
				output.append("|");
			}
		}
		output.append("||{");
		int noOfBusItems = DISetAlgebraGUIInputScreen.instance.BusContentsListModel.size();
		for(int i=0;i<noOfBusItems;i++){
			output.append(DISetAlgebraGUIInputScreen.instance.BusContentsListModel.getElementAt(i));
			if(i<noOfBusItems-1){
				output.append(",");
			}
		}
		output.append("}");
		output.append("w-{");
		int noOfHiddenItems = DISetAlgebraGUIInputScreen.instance.HiddenListModel.size();
		for(int i=0;i<noOfHiddenItems;i++){
			output.append(DISetAlgebraGUIInputScreen.instance.HiddenListModel.getElementAt(i));
			if(i<noOfHiddenItems-1){
				output.append(",");
			}
		}
		output.append("}");
		return output;
	}
	
	/* Clears all selected items, and resets the DI-Set Algebra tab's GUI Input screen. This is called
	 * whenever the screen is loaded by the user */
	public void resetInputScreen(int networkNumber){
		buildingNetwork=networkNumber;
		DISetAlgebraGUIInputScreen.instance.SelectModulesList.clearSelection();
		DISetAlgebraGUIInputScreen.instance.ChosenModulesListModel.clear();

		DISetAlgebraGUIInputScreen.instance.SourceModuleListModel.clear();
		DISetAlgebraGUIInputScreen.instance.SourceOutputListModel.clear();
		
		DISetAlgebraGUIInputScreen.instance.TargetModuleListModel.clear();
		DISetAlgebraGUIInputScreen.instance.TargetInputListModel.clear();
		
		DISetAlgebraGUIInputScreen.instance.WireFunctionListModel.clear();
		DISetAlgebraGUIInputScreen.instance.HiddenListModel.clear();
		DISetAlgebraGUIInputScreen.instance.BusContentsListModel.clear();
		
		DISetAlgebraGUIInputScreen.instance.AddModuleButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.RemoveModuleButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.BuildNetworkButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.ConnectButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.RemoveConnectionButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.PlaceSignalButton.setEnabled(false);
		
		DISetAlgebraGUIInputScreen.instance.HideOutputButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.HideInputButton.setEnabled(false);
		DISetAlgebraGUIInputScreen.instance.RemoveHidingButton.setEnabled(false);
		
		DISetAlgebraGUIInputScreen.instance.RemoveSignalButton.setEnabled(false);
		
		DISetAlgebraGUIInputScreen.instance.BusContentsLabel.setText("Bus contents");
	}

	/* Adds all given strings to the given "list model" (which is like a pack of contents, displayable
	 * in one or more on-screen lists). This is a general method used to populate many of the on-screen
	 * lists of items */
	public void setList(DefaultListModel<String> model, String[] strings) {
		model.clear();
		for(int i=0;i<strings.length;i++){
			model.addElement(strings[i]);
		}
	}

	/* Handles list selection events, disabling/re-enabling GUI elements, and adding or removing items from lists appropriately. */
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.SelectModulesList)){
			DISetAlgebraGUIInputScreen.instance.AddModuleButton.setEnabled(true);
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.ChosenModulesList)){
			DISetAlgebraGUIInputScreen.instance.RemoveModuleButton.setEnabled(true);
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.SourceModuleList)){
			if(!DISetAlgebraGUIInputScreen.instance.SourceModuleList.isSelectionEmpty()){
				String moduleName = DISetAlgebraGUIInputScreen.instance.SourceModuleList.getSelectedValue().split(":")[0];
				int moduleDefinitionIndex =findModuleIndex(moduleName);
				setList(DISetAlgebraGUIInputScreen.instance.SourceOutputListModel,
						DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[moduleDefinitionIndex][2]);
			}
			else{
				DISetAlgebraGUIInputScreen.instance.SourceOutputListModel.clear();
			}
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.TargetModuleList)){
			if(!DISetAlgebraGUIInputScreen.instance.TargetModuleList.isSelectionEmpty()){
				String moduleName = DISetAlgebraGUIInputScreen.instance.TargetModuleList.getSelectedValue().split(":")[0];
				int moduleDefinitionIndex =findModuleIndex(moduleName);
				setList(DISetAlgebraGUIInputScreen.instance.TargetInputListModel,
						DISetAlgebraGUIInputScreen.instance.builtInModuleDefinitions[moduleDefinitionIndex][1]);
			}
			else{
				DISetAlgebraGUIInputScreen.instance.TargetInputListModel.clear();
			}
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.SourceOutputList) ||
				event.getSource().equals(DISetAlgebraGUIInputScreen.instance.TargetInputList)){
			if(!DISetAlgebraGUIInputScreen.instance.SourceOutputList.isSelectionEmpty() &&
					!DISetAlgebraGUIInputScreen.instance.TargetInputList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.ConnectButton.setEnabled(true);
			}
			if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.SourceOutputList)){
				DISetAlgebraGUIInputScreen.instance.HideOutputButton.setEnabled(true);
			}
			if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.TargetInputList)){
				DISetAlgebraGUIInputScreen.instance.HideInputButton.setEnabled(true);
			}
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.WireFunctionList)){
			if(!DISetAlgebraGUIInputScreen.instance.WireFunctionList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.RemoveConnectionButton.setEnabled(true);
				DISetAlgebraGUIInputScreen.instance.PlaceSignalButton.setEnabled(true);
			}
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.HiddenList)){
			if(!DISetAlgebraGUIInputScreen.instance.HiddenList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.RemoveHidingButton.setEnabled(true);
			}
		}
		else if(event.getSource().equals(DISetAlgebraGUIInputScreen.instance.BusContentsList)){
			if(!DISetAlgebraGUIInputScreen.instance.BusContentsList.isSelectionEmpty()){
				DISetAlgebraGUIInputScreen.instance.RemoveSignalButton.setEnabled(true);
			}
		}
	}
	
	
	
}
