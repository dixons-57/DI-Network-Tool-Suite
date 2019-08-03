package GUIListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import DISetAlgebraStructure.PartiallyVisibleNetwork;
import GUI.ConsoleWindow;
import GUI.DISetAlgebraMainScreen;
import GUI.DISetAlgebraTab;
import InputOutputOperations.DISetAlgebraPresets;
import InputOutputOperations.GeneralOperations;
import InputOutputOperations.ParseDISetAlgebra;

/* Process GUI events from the DI-Set Algebra tab's Main screen. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class DISetAlgebraMainListener implements ActionListener{

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static DISetAlgebraMainListener instance= new DISetAlgebraMainListener();

	/* Records whether the left network definition is currently being edited */
	private boolean editingNetwork1=false;
	
	/* Records whether the right network definition is currently being edited */
	private boolean editingNetwork2=false;
	
	/* Stores the left parsed DI-Set algebra network definition */
	private PartiallyVisibleNetwork network1 = null;
	
	/* Stores the right parsed DI-Set algebra network definition */
	private PartiallyVisibleNetwork network2 = null;
	
	/* Records whether a valid DI-Set algebra network definition has been parsed on the left */
	private boolean validNetwork1=false;
	
	/* Records whether a valid DI-Set algebra network definition has been parsed on the right */
	private boolean validNetwork2=false;
	
	/* Handles JButton events, disabling/re-enabling GUI elements appropriately. */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		/* Attempts to load the selected preset which into the right network definition */
		if(command.equals("Load1")){
			if(DISetAlgebraMainScreen.instance.Preset1Box.getSelectedIndex()!=-1 && 
					!DISetAlgebraMainScreen.instance.Preset1Box.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected DI-Set algebra preset into the left network definition: "+
						DISetAlgebraMainScreen.instance.Preset1Box.getSelectedItem()+".dsa");
				if(DISetAlgebraPresets.loadPreset(DISetAlgebraMainScreen.instance.Preset1Box.getSelectedItem().toString(),1)){
					ConsoleWindow.output("Loaded preset: "+DISetAlgebraMainScreen.instance.Preset1Box.getSelectedItem()+".dsa");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+DISetAlgebraMainScreen.instance.Preset1Box.getSelectedItem()+".dsa");
				}
			}
			else{
				ConsoleWindow.output("No DI-Set algebra preset selected");
			}
		}
		
		/* Attempts to save the right DI-Set algebra network definition as a preset */
		else if(command.equals("Save1")){
			if(validNetwork1){
				ConsoleWindow.output("Saving left network as a DI-Set algebra preset");
				while(true){
					String fileNameInput = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(fileNameInput==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(fileNameInput,true) && !fileNameInput.equals("")){
						boolean success = DISetAlgebraPresets.savePreset(fileNameInput,1);
						if(success){
							ConsoleWindow.output("Saved preset: "+fileNameInput+".dsa");
							DISetAlgebraPresets.loadFileList();
							ConsoleWindow.output("Reloaded DI-Set algebra presets");
						}
						else{
							ConsoleWindow.output("Failed to save preset: "+fileNameInput+".dsa");
						}
						break;
					}
					ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
				}
			}
			else{
				ConsoleWindow.output("No valid definitions to save as a preset");
			}
		}
		
		/* Attempts to load the selected preset which into the right network definition */
		if(command.equals("Load")){
			if(DISetAlgebraMainScreen.instance.Preset2Box.getSelectedIndex()!=-1 && 
					!DISetAlgebraMainScreen.instance.Preset2Box.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected DI-Set algebra preset into the right network definition: "+
						DISetAlgebraMainScreen.instance.Preset2Box.getSelectedItem()+".dsa");
				if(DISetAlgebraPresets.loadPreset(DISetAlgebraMainScreen.instance.Preset2Box.getSelectedItem().toString(),2)){
					ConsoleWindow.output("Loaded preset: "+DISetAlgebraMainScreen.instance.Preset2Box.getSelectedItem()+".dsa");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+DISetAlgebraMainScreen.instance.Preset2Box.getSelectedItem()+".dsa");
				}
			}
			else{
				ConsoleWindow.output("No DI-Set algebra preset selected");
			}
		}
		
		/* Attempts to save the right DI-Set algebra network definition as a preset */
		else if(command.equals("Save")){
			if(validNetwork2){
				ConsoleWindow.output("Saving right network as a DI-Set algebra preset");
				while(true){
					String fileNameInput = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(fileNameInput==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(fileNameInput,true) && !fileNameInput.equals("")){
						boolean success = DISetAlgebraPresets.savePreset(fileNameInput,2);
						if(success){
							ConsoleWindow.output("Saved preset: "+fileNameInput+".dsa");
							DISetAlgebraPresets.loadFileList();
							ConsoleWindow.output("Reloaded DI-Set algebra presets");
						}
						else{
							ConsoleWindow.output("Failed to save preset: "+fileNameInput+".dsa");
						}
						break;
					}
					ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
				}
			}
			else{
				ConsoleWindow.output("No valid definitions to save as a preset");
			}
		}
		
		/* Initiates/finalises user editing of the left network definition, parsing of the definition data is attempted 
		 * if this button is clicked when the user is already editing. */
		else if(command.equals("Manual Input1")){
			if(!editingNetwork1){
				ConsoleWindow.output("Editing Network 1 definition");
				editingNetwork1=true;
				DISetAlgebraMainScreen.instance.ManualInput1Button.setText("Parse Definition");
				DISetAlgebraMainScreen.instance.Network1TextArea.setEditable(true);

				DISetAlgebraMainScreen.instance.Preset1Box.setEnabled(false);
				DISetAlgebraMainScreen.instance.Preset1LoadButton.setEnabled(false);
				DISetAlgebraMainScreen.instance.Preset1SaveButton.setEnabled(false);

				DISetAlgebraMainScreen.instance.GUIInput1Button.setEnabled(false);
				DISetAlgebraMainScreen.instance.InteractiveExecution1Button.setEnabled(false);

				DISetAlgebraMainScreen.instance.ManualInput2Button.setEnabled(false);
				DISetAlgebraMainScreen.instance.GUIInput2Button.setEnabled(false);
				DISetAlgebraMainScreen.instance.InteractiveExecution2Button.setEnabled(false);

				DISetAlgebraMainScreen.instance.LTSSimulationButton.setEnabled(false);
				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingNetwork1){
				ConsoleWindow.output("Parsing Network 1 DI-Set algebra definition");
				String module = DISetAlgebraMainScreen.instance.Network1TextArea.getText();
				loadNetwork(module,1);
			}	
		}
		
		/* Initiates/finalises user editing of the right network definition, parsing of the definition data is attempted 
		 * if this button is clicked when the user is already editing. */
		else if(command.equals("Manual Input2")){
			if(!editingNetwork2){
				editingNetwork2=true;
				
				ConsoleWindow.output("Editing Network 2 definition");
				DISetAlgebraMainScreen.instance.ManualInput2Button.setText("Parse Definition");
				DISetAlgebraMainScreen.instance.Network2TextArea.setEditable(true);

				DISetAlgebraMainScreen.instance.Preset1Box.setEnabled(false);
				DISetAlgebraMainScreen.instance.Preset1LoadButton.setEnabled(false);
				DISetAlgebraMainScreen.instance.Preset1SaveButton.setEnabled(false);

				DISetAlgebraMainScreen.instance.GUIInput1Button.setEnabled(false);
				DISetAlgebraMainScreen.instance.InteractiveExecution1Button.setEnabled(false);

				DISetAlgebraMainScreen.instance.ManualInput1Button.setEnabled(false);
				DISetAlgebraMainScreen.instance.GUIInput2Button.setEnabled(false);
				DISetAlgebraMainScreen.instance.InteractiveExecution2Button.setEnabled(false);

				DISetAlgebraMainScreen.instance.LTSSimulationButton.setEnabled(false);
				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingNetwork2){
				ConsoleWindow.output("Parsing Network 2 DI-Set algebra definition");
				String module = DISetAlgebraMainScreen.instance.Network2TextArea.getText();
				loadNetwork(module,2);
			}
		}
		
		/* Displays the GUI Input screen for the left network definition */
		else if(command.equals("GUI Input1")){
			DISetAlgebraTab.instance.showInputScreen(1);
		}
		
		/* Displays the GUI Input screen for the right network definition */
		else if(command.equals("GUI Input2")){
			DISetAlgebraTab.instance.showInputScreen(2);
		}
		
		/* Displays the Interactive Execution screen for the left network definition */
		else if(command.equals("Interactive1")){
			DISetAlgebraTab.instance.showExecutionScreen(1);
		}
		
		/* Displays the Interactive Execution screen for the right network definition */
		else if(command.equals("Interactive2")){
			DISetAlgebraTab.instance.showExecutionScreen(2);
		}
		
		/* Displays helpful info to the user about how to enter a DI-Set algebra network definition*/
		if(command.equals("?")){
			JOptionPane.showMessageDialog(null, "Enter a series of CCS-like state constant definitions separated by semi-colons. Line breaks are also allowed.\n"
					+ "Also don't worry about spaces.\n"
					+ "Type the definitions similarly to how they appear in the thesis, and in the included preset examples. Do not add random brackets.\n"
					+ "Use only alpha-numeric characters (and prime operators ') for constant, input, and output names.\n\n"
					+ "e.g:       S0 = ({a,b,c},{x,y}).S0 + ({a,b},{z}).S1;\n"
					+ "              S1 = ({c},{x}).S0 + ({a,b},{x,z}).S0;\n\n"
					+ "Following this, enter the wire function of the form \n\nw = { (port1 : label1, port2 : label2), (port3 : label3, port4 : label4), (portm : labelm, portn : labeln) };\n\n"
					+ "Note that the line ends with a semi-colon, and colons are used instead of the center-dot used in the thesis version.\n"
					+ "Use only alpha-numeric characters (and prime operators ') for port names and labels.\n"
					+ "Also only use w as the identifier for the wire function, do not use w2, w3 etc.\n\n"
					+ "Finally, enter a network definition of the form         Sn = (P1) : label1 | (P2) : label2 | (Pn) : labeln || {}w - {};\n\n"
					+ "Note that the line again ends with a semi-colon and colons are again used instead of the center-dot.\n"
					+ "Sn can be any alphanumeric (and prime opertator ') name. P1...Pn must be the names of above-defined constant definitions \n"
					+ "(no direct lists of actions) and these must be surrounded by brackets as depicted\n"
					+ "Use only alpha-numeric characters (and prime operators ') for label names (these should correspond to w anyway)\n\n"
					+ "Bus contents and hidden ports cannot be pre-defined using a variable, they must be written in full. Hence:\n"
					+ "bus contents need to be written as a list of named ports within the brackets of {}w and\n"
					+ "hidden ports need to be written as a list of named ports within the brackets of -{} except you may also use the wildcard operator *\n"
					+ "of the form *:labelm instead of a traditional named port, which will hide all named ports which are labelled with labelm\n\n"
					+ "e.g.      - {port1 : label1, port2: label2, * : label3}");
		}
		
		/* Displays the LTS screen*/
		else if(command.equals("LTS Analysis and (Bi)Simulation")){
			DISetAlgebraTab.instance.showLTSScreen();
		}
	}
	
	/* Clears either the left or right stored DI-Set algebra network definition */
	public void clearNetwork(int networkNumber) {
		if(networkNumber==1){
			network1=null;
		}
		else{
			network2=null;
		}
	}

	/* Returns whether the left or right stored DI-Set algebra network definition
	 * is valid, depending on what value is given */
	public boolean getNetworkValid(int networkNumber) {
		if(networkNumber==1){
			return validNetwork1;
		}
		else{
			return validNetwork2;
		}
	}

	/* Returns the left or right stored DI-Set algebra network definition,
	 * depending on what value is given */
	public PartiallyVisibleNetwork getStoredNetwork(int networkNumber) {
		if(networkNumber==1){
			return network1;
		}
		else{
			return network2;
		}
	}

	/* Attempts to build the DI-Set algebra network term object for the entered (or loaded from preset) definition, into the left
	 * or right network given by networkNumber. It utilises parsing functionality from ParseDISetAlgebra. */
	public void loadNetwork(String definitionString, int networkNumber){
		boolean ok=false;

		if(definitionString.equals("")){
			if(networkNumber==1){
				validNetwork1=false;
				network1=null;
			}
			else{
				validNetwork2=false;
				network2=null;
			}
			ok=true;
			ConsoleWindow.output("Network " +networkNumber+ " definition cleared");
		}
		else{
			PartiallyVisibleNetwork parsedResult;
			try {
				parsedResult = ParseDISetAlgebra.parseEntireDefinition(definitionString);
			} catch (Exception e1) {
				parsedResult=null;
			}
			if(parsedResult==null){
				ConsoleWindow.output("Error parsing Network " +networkNumber+ " definition - please check and try again");
			}
			else{				
				ok=true;
				if(networkNumber==1){
					validNetwork1=true;
					network1=parsedResult;
					DISetAlgebraMainScreen.instance.Network1TextArea.setText(parsedResult.printEntireDefinition());
				}
				else{
					validNetwork2=true;
					network2=parsedResult;
					DISetAlgebraMainScreen.instance.Network2TextArea.setText(parsedResult.printEntireDefinition());
				}
				ConsoleWindow.output("Network " + networkNumber +" definition successfully parsed");
			}
		}
		if(ok){
			DISetAlgebraMainScreen.instance.Preset1Box.setEnabled(true);
			DISetAlgebraMainScreen.instance.Preset1LoadButton.setEnabled(true);
			DISetAlgebraMainScreen.instance.Preset1SaveButton.setEnabled(true);
			DISetAlgebraMainScreen.instance.GUIInput1Button.setEnabled(true);				
			DISetAlgebraMainScreen.instance.GUIInput2Button.setEnabled(true);
			
			if(networkNumber==1){
				editingNetwork1=false;
				DISetAlgebraMainScreen.instance.ManualInput1Button.setText("Edit Definition");
				DISetAlgebraMainScreen.instance.Network1TextArea.setEditable(false);
				DISetAlgebraMainScreen.instance.ManualInput2Button.setEnabled(true);	
			}
			else{
				editingNetwork2=false;
				DISetAlgebraMainScreen.instance.ManualInput2Button.setText("Edit Definition");
				DISetAlgebraMainScreen.instance.Network2TextArea.setEditable(false);
				DISetAlgebraMainScreen.instance.ManualInput1Button.setEnabled(true);	
			}
			if(validNetwork1){
				DISetAlgebraMainScreen.instance.InteractiveExecution1Button.setEnabled(true);
			}
			if(validNetwork2){
				DISetAlgebraMainScreen.instance.InteractiveExecution2Button.setEnabled(true);
			}
			if(validNetwork1 || validNetwork2){
				DISetAlgebraMainScreen.instance.LTSSimulationButton.setEnabled(true);
			}
			ConsoleWindow.output("GUI controls re-enabled");
		}
	}

	/* Clears all network definitions and resets the DI-Set algebra tab's Main screen */
	public void resetMainScreen(){
		network1=null;
		network2=null;
		editingNetwork1=false;
		editingNetwork2=false;
		DISetAlgebraMainScreen.instance.Network1TextArea.setEditable(false);
		DISetAlgebraMainScreen.instance.Network2TextArea.setEditable(false);
		DISetAlgebraMainScreen.instance.Network1TextArea.setText("");
		DISetAlgebraMainScreen.instance.Network2TextArea.setText("");
		DISetAlgebraMainScreen.instance.InteractiveExecution1Button.setEnabled(false);
		DISetAlgebraMainScreen.instance.InteractiveExecution2Button.setEnabled(false);
		DISetAlgebraMainScreen.instance.LTSSimulationButton.setEnabled(false);

		DISetAlgebraMainScreen.instance.Preset1LoadButton.setEnabled(true);
		DISetAlgebraMainScreen.instance.Preset1SaveButton.setEnabled(true);
		DISetAlgebraMainScreen.instance.Preset1Box.setEnabled(true);
		DISetAlgebraMainScreen.instance.ManualInput1Button.setEnabled(true);
		DISetAlgebraMainScreen.instance.ManualInput2Button.setEnabled(true);

		DISetAlgebraMainScreen.instance.GUIInput1Button.setEnabled(true);
		DISetAlgebraMainScreen.instance.GUIInput2Button.setEnabled(true);
	}

	/* Sets the flag indicating whether the left or right stored DI-Set algebra network definition
	 * is valid, depending on what value is given */
	public void setNetworkValid(int networkNumber, boolean value) {
		if(networkNumber==1){
			validNetwork1=value;
		}
		else{
			validNetwork2=value;
		}
	}
	
}
