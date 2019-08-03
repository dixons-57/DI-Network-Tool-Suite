package GUIListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import EnvironmentOperations.NonArbGeneration;
import EnvironmentOperations.UncertaintyGeneration;
import GUI.ConsoleWindow;
import GUI.EnvironmentTab;
import InputOutputOperations.GeneralOperations;
import InputOutputOperations.ParseSetNotationModule;
import InputOutputOperations.SetNotationModulePresets;
import SetNotationStructure.SetNotationModule;

/* Process GUI events from the Environment Generation tab. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class EnvironmentTabListener implements ActionListener {

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static EnvironmentTabListener instance = new EnvironmentTabListener();

	/* Records which environment generation algorithm can be used (non-arb or general) */
	private int algorithmType=0;  
	
	/* Records whether the Set Notation module definition is currently being edited */
	private boolean editingSetModule=false;
	
	/* Records whether the stored module is auto-clashing */
	private boolean setModuleAutoClash=false;

	/* Records whether the stored module is auto-firing */
	private boolean setModuleAutoFire=false;
	
	/* Records whether the stored module is 1-step consistent */
	private boolean setModuleOneStepConsistent=false;

	/* Records whether the stored module is stable */
	private boolean setModuleStable=false;
	
	/* Stores the generated environment */
	private SetNotationModule storedEnvironment = null;
	
	/* Stores the parsed Set Notation module */
	private SetNotationModule storedSetModule = null;
	
	/* Records whether a valid Set Notation module has been parsed */
	private boolean validSetModule=false;

	/* Handles JButton events, disabling/re-enabling GUI elements appropriately. */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		/* Initiates/finalises user editing of the module definition, parsing of the definition data is attempted 
		 * if this button is clicked when the user is already editing. */
		if(command.equals("EditSet")){
			if(!editingSetModule){
				ConsoleWindow.output("Editing Set Module definition");
				editingSetModule=true;
				EnvironmentTab.instance.EditButton.setText("Parse Definition");
				EnvironmentTab.instance.ModuleDefinitionTextArea.setEditable(true);

				EnvironmentTab.instance.PresetBox.setEnabled(false);
				EnvironmentTab.instance.PresetLoadButton.setEnabled(false);
				EnvironmentTab.instance.PresetSaveButton.setEnabled(false);

				EnvironmentTab.instance.GenerateButton.setEnabled(false);
				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingSetModule){
				ConsoleWindow.output("Parsing Set Notation Module definition");
				String module = EnvironmentTab.instance.ModuleDefinitionTextArea.getText();
				loadModule(module);
			}
		}
		
		/* Displays helpful info to the user about how to enter a Set Notation module definition*/
		if(command.equals("?")){
			JOptionPane.showMessageDialog(null, "Enter a series of CCS-like state definitions separated by semi-colons. Line breaks are also allowed.\n"
					+ "Also don't worry about spaces.\nType the definitions similarly to how they appear in the thesis, and in the included preset examples. \nDo not add random brackets.\n"
					+ "Use only alpha-numeric characters (and prime operators ') for state, input, and output names.\n\n"
					+ "e.g:\n\nS0 = ({a,b,c},{x,y}).S0 + ({a,b},{z}).S1;\n"
					+ "S1 = ({c},{x}).S0 + ({a,b},{x,z}).S0;\n");
		}
		
		/* Attempts to load the selected preset which stores a Set Notation module definition */
		else if(command.equals("Load")){
			if(EnvironmentTab.instance.PresetBox.getSelectedIndex()!=-1 && !EnvironmentTab.instance.PresetBox.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected Set Notation module preset: "+EnvironmentTab.instance.PresetBox.getSelectedItem()+".snp");
				if(SetNotationModulePresets.loadPreset(EnvironmentTab.instance.PresetBox.getSelectedItem().toString(),2)){
					ConsoleWindow.output("Loaded preset: "+EnvironmentTab.instance.PresetBox.getSelectedItem()+".snp");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+EnvironmentTab.instance.PresetBox.getSelectedItem()+".snp");
				}
			}
			else{
				ConsoleWindow.output("No Set Notation module preset selected");
			}
		}
		
		/* Attempts to save the existing Set Notation module definition as a preset */
		else if(command.equals("Save")){
			if(validSetModule){
				ConsoleWindow.output("Saving current Set Notation Module definition in Environment Generation tab as a Set Notation module preset");
				while(true){
					String fileNameInput = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(fileNameInput==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(fileNameInput,true) && !fileNameInput.equals("")){
						boolean success = SetNotationModulePresets.savePreset(EnvironmentTab.instance.EnvironmentTextArea.getText(),fileNameInput);
						if(success){
							ConsoleWindow.output("Saved preset: "+fileNameInput+".snp");
							SetNotationModulePresets.loadFileList();
							ConsoleWindow.output("Reloaded Set Notation module presets");
						}
						else{
							ConsoleWindow.output("Failed to save preset: "+fileNameInput+".snp");
						}
						break;
					}
					ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
				}
			}
			else{
				ConsoleWindow.output("No valid module definition to save as a preset");
			}
		}
		
		/* Generates the environment for the stored Set Notation module definition, using whichever algorithm
		 * is required (non-arb algorithm if non-arb module, general algorithm otherwise) */
		else if(command.equals("Generate Environment")){
			if(algorithmType==1){
				ConsoleWindow.output("Beginning environment generation (non-arb algorithm)");
				storedEnvironment=NonArbGeneration.nonArb(storedSetModule);
				EnvironmentTab.instance.EnvironmentTextArea.setText(storedEnvironment.printModule());
				ConsoleWindow.output("Environment generation complete");
			}
			else if(algorithmType==2){
				ConsoleWindow.output("Beginning environment generation (uncertainty algorithm)");
				ConsoleWindow.output("A \"configuration\" refers to a triple String;Set;Set, where the String refers to a module state, "
						+ "the first set refers to a set of pending input signals of the module, and the second set refers to a set of currently "
						+ "travelling output signals from the module.");
				ConsoleWindow.output("An \"uncertainty\" refers to a finite set of configurations of the form config1:config2:config3 etc.");
				storedEnvironment=UncertaintyGeneration.generate(storedSetModule);
				EnvironmentTab.instance.EnvironmentTextArea.setText(storedEnvironment.printModule());
				ConsoleWindow.output("Environment generation complete");
			}
		}
	}

	/* Attempts to build the module object for the entered (or loaded from preset) definition. It utilises parsing functionality
	 * from ParseSetNotationModule. It then checks various module properties which are relevant to environment generation */
	public void loadModule(String module) {
		boolean ok=false;
		EnvironmentTab.instance.StatusLabel.setText("Status: N/A");
		if(module.equals("")){
			ok=true;
			validSetModule=false;
			algorithmType=0;
			storedSetModule=null;
			storedEnvironment=null;
			EnvironmentTab.instance.EnvironmentTextArea.setText("");
			ConsoleWindow.output("Set Notation Module definition cleared");
		}
		else{
			SetNotationModule parsedResult;
			try {
				parsedResult = ParseSetNotationModule.parse(module);
			} catch (Exception e1) {
				parsedResult=null;
			}
			if(parsedResult==null){
				ConsoleWindow.output("Error parsing Set Notation Module definition - please check and try again");
			}
			else{
				validSetModule=true;
				ok=true;
				storedSetModule=parsedResult;
				EnvironmentTab.instance.ModuleDefinitionTextArea.setText(parsedResult.printModule());

				ConsoleWindow.output("Set Notation Module definition successfully parsed, checking well-formedness");

				setModuleStable=false;
				setModuleAutoFire=false;
				setModuleAutoClash=false;
				setModuleOneStepConsistent=false;
				boolean arb=parsedResult.checkArb();
				if(arb){
					setModuleOneStepConsistent=parsedResult.checkOneStepConsistent();
					setModuleStable=parsedResult.checkStability();
					setModuleAutoFire = parsedResult.autoFiring();

					if(setModuleAutoFire){
						setModuleAutoClash = parsedResult.autoClashing();
					}
				}
				if(!arb){
					ConsoleWindow.output("Set Notation Module definition is non-arb (and hence stable,consistent, and non-auto-processing)"
							+ " so the efficient environmental generation algorithm can be used");
					EnvironmentTab.instance.StatusLabel.setText("Status: non-arb: Efficient algorithm available");
					algorithmType=1;
				}
				else {					
					String properties;
					if(setModuleOneStepConsistent){
						properties="1-Step Consistent, ";
					}
					else{
						properties="not 1-Step Consistent, ";
					}
					if(setModuleStable){
						properties=properties + "stable, ";
					}
					else{
						properties=properties + "unstable, ";
					}
					if(setModuleAutoFire){
						properties=properties+"auto-firing";
						if(setModuleAutoClash){
							properties=properties+", and auto-clashing";
						}
					}
					else{
						properties=properties+"non-auto-processing";
					}
					ConsoleWindow.output("Set Notation Module definition is arb, "+properties+", so uncertainty algorithm is needed");
					EnvironmentTab.instance.StatusLabel.setText("Status: arb: Uncertainty algorithm required");
					algorithmType=2;
				}
			}
		}

		if(ok){
			editingSetModule=false;
			EnvironmentTab.instance.EditButton.setText("Edit Definition");
			EnvironmentTab.instance.ModuleDefinitionTextArea.setEditable(false);
			EnvironmentTab.instance.PresetBox.setEnabled(true);
			EnvironmentTab.instance.PresetLoadButton.setEnabled(true);
			EnvironmentTab.instance.PresetSaveButton.setEnabled(true);
			EnvironmentTab.instance.GenerateButton.setEnabled(true);
			ConsoleWindow.output("GUI controls re-enabled");
		}
	}

}
