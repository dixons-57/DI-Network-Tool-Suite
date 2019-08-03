package GUIListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import ConversionOperations.SeqToSetConversion;
import ConversionOperations.SetToSeqConversion;
import GUI.ConsoleWindow;
import GUI.ConversionTab;
//import InputOutputOperations.ConversionPresets;
import InputOutputOperations.GeneralOperations;
import InputOutputOperations.ParseSequentialMachine;
import InputOutputOperations.ParseSetNotationModule;
import InputOutputOperations.SequentialMachinePresets;
import InputOutputOperations.SetNotationModulePresets;
import SequentialMachineStructure.NDSequentialMachine;
import SetNotationStructure.SetNotationModule;

/* Process GUI events from the Conversion tab. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class ConversionTabListener implements ActionListener{

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static ConversionTabListener instance = new ConversionTabListener();

	/* Records whether the sequential machine module definition is currently being edited */
	private boolean editingSeqModule=false;
	
	/* Records whether the Set Notation module definition is currently being edited */
	private boolean editingSetModule=false;
	
	/* Stores the parsed sequential machine module */
	private NDSequentialMachine storedSeqModule = null;
	
	/* Stores the parsed Set Notation module */
	private SetNotationModule storedSetModule = null;
	
	/* Stores whether a valid sequential machine module has been parsed */
	private boolean validSeqModule=false;
	
	/* Stores whether a valid Set Notation module has been parsed */
	private boolean validSetModule=false;
	
	/* Handles JButton events, disabling/re-enabling GUI elements appropriately. */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		/* Initiates/finalises user editing of the sequential machine module definition, 
		 * parsing of the definition data is attempted if this button is clicked when the user is already editing. */
		if(command.equals("EditSeq")){
			if(!editingSeqModule){
				editingSeqModule=true;
				ConsoleWindow.output("Editing Sequential Machine definition");
				ConversionTab.instance.EditSeqButton.setText("Parse Definition");
				ConversionTab.instance.SeqTextArea.setEditable(true);
				ConversionTab.instance.AFuncTextArea.setEditable(true);

				ConversionTab.instance.SetNotationPresetBox.setEnabled(false);
				ConversionTab.instance.SetNotationPresetLoadButton.setEnabled(false);
				ConversionTab.instance.SetNotationPresetSaveButton.setEnabled(false);

				ConversionTab.instance.EditSetButton.setEnabled(false);
				ConversionTab.instance.ConvertToSetButton.setEnabled(false);
				ConversionTab.instance.ConvertToSeqButton.setEnabled(false);
				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingSeqModule){
				ConsoleWindow.output("Parsing Sequential Machine definition");
				String module = ConversionTab.instance.SeqTextArea.getText();
				String Afunction = ConversionTab.instance.AFuncTextArea.getText();
				loadSeq(module,Afunction);
			}
		}
		
		/* Displays helpful info to the user about how to enter a sequential machine module definition*/
		if(command.equals("SeqHelp")){
			JOptionPane.showMessageDialog(null, "Enter a series of CCS-like state definitions separated by semi-colons. Line breaks are also allowed.\n"
					+ "Also don't worry about spaces.\n"
					+ "Type the definitions similarly to how they appear in the thesis, and in the included preset examples. Do not add random brackets.\n"
					+ "Empty sets are represented using set brackets {}. Use only alpha-numeric characters (and prime operators ') for state, input, \nand output names.\n\n"
					+ "e.g:\n\nS0 = (a,{}).S1 + (b,{}).S1;\n"
					+ "S1 = (b,{x,y}).S0 + (a,{x,y}).S0;\n\n"
					+ "Regarding the A function definition: Simply type the name of the state for which you wish to define the corresponding entry,\n"
					+ "then an equals sign, and then the entry itself in set brackets {} (followed by a semi-colon as above). Within these brackets\n"
					+ "will be one or more sets written in full, including the brackets around each set.\n\n"
					+ "e,g:\n\nS0 = {{a,b}};\n"
					+ "S1 = {{a},{b}}\n\n"
					+ "If you do not define A function entries for certain states or input names, then the software will auto-complete this for you,\n"
					+ "by assuming serial (singleton set) behaviour. Do not define inputs in an A function entry that do not have corresponding actions\n"
					+ "defined in the same state of the main definition, or this will cause a parsing error.");
		}
		
		/* Initiates/finalises user editing of the Set Notation module definition, 
		 * parsing of the definition data is attempted if this button is clicked when the user is already editing. */
		else if(command.equals("EditSet")){
			if(!editingSetModule){
				editingSetModule=true;
				ConsoleWindow.output("Editing Set Module definition");
				ConversionTab.instance.EditSetButton.setText("Parse Definition");
				ConversionTab.instance.SetTextArea.setEditable(true);

				ConversionTab.instance.SetNotationPresetBox.setEnabled(false);
				ConversionTab.instance.SetNotationPresetLoadButton.setEnabled(false);
				ConversionTab.instance.SetNotationPresetSaveButton.setEnabled(false);

				ConversionTab.instance.EditSeqButton.setEnabled(false);
				ConversionTab.instance.ConvertToSetButton.setEnabled(false);
				ConversionTab.instance.ConvertToSeqButton.setEnabled(false);
				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingSetModule){
				ConsoleWindow.output("Parsing Set Notation Module definition");
				String module = ConversionTab.instance.SetTextArea.getText();	
				loadSet(module);
			}
		}
		
		/* Displays helpful info to the user about how to enter a Set Notation module definition*/
		if(command.equals("SetHelp")){
			JOptionPane.showMessageDialog(null, "Enter a series of CCS-like state definitions separated by semi-colons. Line breaks are also allowed.\n"
					+ "Also don't worry about spaces.\n"
					+ "Type the definitions similarly to how they appear in the thesis, and in the included preset examples. \nDo not add random brackets.\n"
					+ "Use only alpha-numeric characters (and prime operators ') for state, input, and output names.\n\n"
					+ "e.g:\n\nS0 = ({a,b,c},{x,y}).S0 + ({a,b},{z}).S1;\n"
					+ "S1 = ({c},{x}).S0 + ({a,b},{x,z}).S0;\n");
		}
		
		/* Attempts to load the selected sequential machine module preset */
		else if(command.equals("Load1")){
			if(ConversionTab.instance.SeqModulePresetBox.getSelectedIndex()!=-1 && !ConversionTab.instance.SeqModulePresetBox.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected sequential machine module preset: "+ConversionTab.instance.SeqModulePresetBox.getSelectedItem()+".sqmp");
				if(SequentialMachinePresets.loadPreset(ConversionTab.instance.SeqModulePresetBox.getSelectedItem().toString())){
					ConsoleWindow.output("Loaded preset: "+ConversionTab.instance.SeqModulePresetBox.getSelectedItem()+".sqmp");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+ConversionTab.instance.SeqModulePresetBox.getSelectedItem()+".sqmp");
				}
			}
			else{
				ConsoleWindow.output("No sequential machine module preset selected");
			}
		}
		
		/* Attempts to save the existing sequential machine module definition as a preset */
		else if(command.equals("Save1")){
			if(validSeqModule){
					ConsoleWindow.output("Saving current sequential machine module definition in Conversion tab as a sequential machine module preset");
				while(true){
					String fileNameInput = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(fileNameInput==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(fileNameInput,true) && !fileNameInput.equals("")){
						boolean success = SequentialMachinePresets.savePreset(fileNameInput);
						if(success){
							ConsoleWindow.output("Saved preset: "+fileNameInput+".sqmp");
							SequentialMachinePresets.loadFileList();
							ConsoleWindow.output("Reloaded sequential machine module presets");
						}
						else{
							ConsoleWindow.output("Failed to save preset: "+fileNameInput+".sqmp");
						}
						break;
					}
					ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
				}
			}
			else{
				ConsoleWindow.output("No valid sequential machine module definition to save as a preset");
			}
		}
		
		/* Attempts to load the selected Set Notation module preset */
		else if(command.equals("Load")){
			if(ConversionTab.instance.SetNotationPresetBox.getSelectedIndex()!=-1 && !ConversionTab.instance.SetNotationPresetBox.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected Set Notation moodule preset: "+ConversionTab.instance.SetNotationPresetBox.getSelectedItem()+".snp");
				if(SetNotationModulePresets.loadPreset(ConversionTab.instance.SetNotationPresetBox.getSelectedItem().toString(),0)){
					ConsoleWindow.output("Loaded preset: "+ConversionTab.instance.SetNotationPresetBox.getSelectedItem()+".snp");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+ConversionTab.instance.SetNotationPresetBox.getSelectedItem()+".snp");
				}
			}
			else{
				ConsoleWindow.output("No Set Notation module preset selected");
			}
		}
		
		/* Attempts to save the existing Set Notation module definition as a preset */
		else if(command.equals("Save")){
			if(validSetModule){
					ConsoleWindow.output("Saving current Set Notation module definition in Conversion tab as a Set Notation module preset");
				while(true){
					String fileNameInput = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(fileNameInput==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(fileNameInput,true) && !fileNameInput.equals("")){
						boolean success = SetNotationModulePresets.savePreset(ConversionTab.instance.SetTextArea.getText(),fileNameInput);
						if(success){
							ConsoleWindow.output("Saved preset: "+fileNameInput+".ncp");
							SetNotationModulePresets.loadFileList();
							ConsoleWindow.output("Reloaded Set Notation module presets");
						}
						else{
							ConsoleWindow.output("Failed to save preset: "+fileNameInput+".ncp");
						}
						break;
					}
					ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
				}
			}
			else{
				ConsoleWindow.output("No valid Set Notation module definition to save as a preset");
			}
		}
		
		/* Converts the stored (ND) sequential machine definition to a Set Notation module definition */
		else if(command.equals("Convert to Set Notation")){
			ConsoleWindow.output("Converting Sequential Machine definition to Set Notation Module definition");
			SetNotationModule conversionResult =SeqToSetConversion.convertToSet(storedSeqModule);
			ConversionTab.instance.SetTextArea.setText(conversionResult.printModule());
			ConversionTab.instance.ConvertToSeqButton.setEnabled(true);
			storedSetModule=conversionResult;
			validSetModule=true;
			ConsoleWindow.output("Successfully converted to Set Notation Module");
		}
		
		/* Converts the stored Set Notation module definition to a (ND) sequential machine definition */
		else if(command.equals("Convert to (ND) Sequential Machine")){
			ConsoleWindow.output("Converting Set Notation Module definition to Sequential Machine definition");
			NDSequentialMachine conversionResult =SetToSeqConversion.convertAnyToSeq(storedSetModule);
			ConversionTab.instance.SeqTextArea.setText(conversionResult.printMainOnly(false));
			ConversionTab.instance.AFuncTextArea.setText(conversionResult.printAFunction());
			ConversionTab.instance.ConvertToSetButton.setEnabled(true);
			storedSeqModule=conversionResult;
			validSeqModule=true;
			ConsoleWindow.output("Successfully converted to sequential machine");
		}
	}

	/* Clears the stored sequential machine definition */
	public void clearSeqModule() {
		storedSeqModule=null;
		validSeqModule=false;
	}

	/* Clears the stored Set Notation module definition */
	public void clearSetModule() {
		storedSetModule=null;
		validSetModule=false;
	}
	
	/* Returns whether a valid sequential machine definition is stored */
	public boolean getSeqValid(){
		return validSeqModule;
	}
	
	/* Returns whether a valid Set Notation module definition is stored */
	public boolean getSetValid(){
		return validSetModule;
	}

	/* Attempts to build the module object for the entered (or loaded from preset) sequential machine definition. 
	 * It utilises parsing functionality from ParseSequentialMachine. */
	public void loadSeq(String module, String afunction) {
		boolean ok=false;
		
		if(module.equals("") && afunction.equals("")){
			validSeqModule=false;
			storedSeqModule=null;
			ok=true;
			ConsoleWindow.output("Sequential Machine definition empty. Stored definition cleared");
		}
		else{
			NDSequentialMachine parsedResult;
			try {
				parsedResult = ParseSequentialMachine.parse(module,afunction);
			} 
			catch (Exception e1) {
				parsedResult=null;
			}
			if(parsedResult==null){
				ConsoleWindow.output("Error parsing Sequential Machine definition - please check and try again");
			}
			else{
				validSeqModule=true;
				ok=true;
				storedSeqModule=parsedResult;
				ConversionTab.instance.SeqTextArea.setText(parsedResult.printMainOnly(false));
				ConversionTab.instance.AFuncTextArea.setText(parsedResult.printAFunction());
				ConsoleWindow.output("Sequential Machine definition successfully parsed");
			}
		}
		
		if(ok){
			editingSeqModule=false;
			ConversionTab.instance.EditSeqButton.setText("Edit Definition");
			ConversionTab.instance.SeqTextArea.setEditable(false);
			ConversionTab.instance.AFuncTextArea.setEditable(false);
			ConversionTab.instance.SetNotationPresetBox.setEnabled(true);
			ConversionTab.instance.SetNotationPresetLoadButton.setEnabled(true);
			ConversionTab.instance.SetNotationPresetSaveButton.setEnabled(true);

			ConversionTab.instance.EditSetButton.setEnabled(true);
			ConversionTab.instance.ConvertToSetButton.setEnabled(true);
			ConversionTab.instance.ConvertToSeqButton.setEnabled(true);
			ConsoleWindow.output("GUI controls re-enabled");
		}
	}
	
	/* Attempts to build the module object for the entered (or loaded from preset) Set Notation module definition. 
	 * It utilises parsing functionality from ParseSetNotationModule. It also checks a module can be converted
	 * to a sequential machine, or whether it can only be converted to a ND sequential machine */
	public void loadSet(String module) {
		boolean ok=false;
		ConversionTab.instance.SetStatusLabel.setText("Status: N/A");
		
		if(module.equals("")){
			validSetModule=false;
			storedSetModule=null;
			ok=true;
			ConversionTab.instance.SetStatusLabel.setText("Status: N/A");
			ConsoleWindow.output("Set Notation Module definition cleared");
		}
		else{
			SetNotationModule parsedResult;
			try {
				parsedResult = ParseSetNotationModule.parse(module);
			} 
			catch (Exception e1) {
				parsedResult=null;
			}
			if(parsedResult==null){
				ConsoleWindow.output("Error parsing Set Notation module definition - please check and try again");
			}
			else{
				validSetModule=true;
				ok=true;
				storedSetModule=parsedResult;
				ConversionTab.instance.SetTextArea.setText(parsedResult.printModule());
				ConsoleWindow.output("Set Notation module definition successfully parsed, checking convertibility");
				
				int convertType=SetToSeqConversion.convertibleToSeq(parsedResult);
				if(convertType==1){
					ConsoleWindow.output("Set Notation module definition satisfies conditions and can be converted to standard sequential machine definition");
					ConversionTab.instance.SetStatusLabel.setText("Status: Convertible to sequential machine");
				}
				else if(convertType==2){
					ConsoleWindow.output("Set Notation module definition satisfies conditions and can be converted to (only) ND sequential machine definition");
					ConversionTab.instance.SetStatusLabel.setText("Status: Convertible to ND sequential machine");
				}
			}
		}
		
		if(ok){
			editingSetModule=false;
			ConversionTab.instance.EditSetButton.setText("Edit Definition");
			ConversionTab.instance.SetTextArea.setEditable(false);
			ConversionTab.instance.SetNotationPresetBox.setEnabled(true);
			ConversionTab.instance.SetNotationPresetLoadButton.setEnabled(true);
			ConversionTab.instance.SetNotationPresetSaveButton.setEnabled(true);
			ConversionTab.instance.EditSeqButton.setEnabled(true);
			ConversionTab.instance.ConvertToSetButton.setEnabled(true);
			ConversionTab.instance.ConvertToSeqButton.setEnabled(true);
			ConsoleWindow.output("GUI controls re-enabled");
		}
	}

	/* Sets the flag indicating whether the stored sequential machine definition
	 * is valid, depending on what value is given */
	public void setSeqValid(boolean value) {
		validSeqModule=value;
	}

	/* Sets the flag indicating whether the stored Set Notation module definition
	 * is valid, depending on what value is given */
	public void setSetValid(boolean value) {
		validSetModule=value;
	}
}
