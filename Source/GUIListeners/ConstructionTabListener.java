package GUIListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import ConstructionOperations.ConstructionCircuit;
import ConstructionOperations.GenerateDecomposition;
import GUI.ConsoleWindow;
import GUI.ConstructionTab;
import InputOutputOperations.GeneralOperations;
import InputOutputOperations.ParseSetNotationModule;
import InputOutputOperations.SetNotationModulePresets;
import SetNotationStructure.SetNotationModule;

/* Process GUI events from the Construction tab. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class ConstructionTabListener implements ActionListener {

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static ConstructionTabListener instance = new ConstructionTabListener();

	/* Records whether the Set Notation module definition is currently being edited */
	private boolean editingSetModule=false;  
	
	/* Records whether the module can be constructed using the construction algorithms
	 * (module is required to be non-arb or eq-arb) */
	private boolean moduleConstructible=false;
	
	/* Stores the generated construction of the module */
	private ConstructionCircuit storedConstruction=null;
	
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
				editingSetModule=true;
				ConsoleWindow.output("Editing Set Module definition");
				ConstructionTab.instance.EditButton.setText("Parse Definition");
				ConstructionTab.instance.ModuleDefinitionTextArea.setEditable(true);

				ConstructionTab.instance.PresetBox.setEnabled(false);
				ConstructionTab.instance.PresetLoadButton.setEnabled(false);
				ConstructionTab.instance.PresetSaveButton.setEnabled(false);

				ConstructionTab.instance.ConstructSetButton.setEnabled(false);
				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingSetModule){
				ConsoleWindow.output("Parsing Set Notation module definition");	
				String module = ConstructionTab.instance.ModuleDefinitionTextArea.getText();		
				loadModule(module);
			}
		}
		
		/* Displays helpful info to the user about how to enter a Set Notation module definition*/
		if(command.equals("?")){
			JOptionPane.showMessageDialog(null, "Enter a series of CCS-like state definitions separated by semi-colons. Line breaks are also allowed.\n"
					+ "Also don't worry about spaces.\n"
					+ "Type the definitions similarly to how they appear in the thesis, and in the included preset examples. \nDo not add random brackets.\n"
					+ "Use only alpha-numeric characters (and prime operators ') for state, input, and output names.\n\n"
					+ "e.g:\n\nS0 = ({a,b,c},{x,y}).S0 + ({a,b},{z}).S1;\n"
					+ "S1 = ({c},{x}).S0 + ({a,b},{x,z}).S0;\n");
		}
		
		/* Attempts to load the selected preset which stores a Set Notation module definition */
		else if(command.equals("Load")){
			if(ConstructionTab.instance.PresetBox.getSelectedIndex()!=-1 && !ConstructionTab.instance.PresetBox.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected Set Notation module preset: "+ConstructionTab.instance.PresetBox.getSelectedItem()+".snp");
				if(SetNotationModulePresets.loadPreset(ConstructionTab.instance.PresetBox.getSelectedItem().toString(),1)){
					ConsoleWindow.output("Loaded preset: "+ConstructionTab.instance.PresetBox.getSelectedItem()+".snp");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+ConstructionTab.instance.PresetBox.getSelectedItem()+".snp");
				}
			}
			else{
				ConsoleWindow.output("No Set Notation module preset selected");
			}
		}
		
		/* Attempts to save the existing Set Notation module definition as a preset */
		else if(command.equals("Save")){
			if(validSetModule){
				ConsoleWindow.output("Saving current Set Notation Module definition in Construction tab as a Set Notation module preset");
				while(true){
					String fileNameInput = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(fileNameInput==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(fileNameInput,true) && !fileNameInput.equals("")){
						boolean success = SetNotationModulePresets.savePreset(ConstructionTab.instance.ModuleDefinitionTextArea.getText(),fileNameInput);
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
		
		/* Constructs the stored/parsed Set Notation module using a network of Set Notation modules*/
		else if(command.equals("Construct")){
			ConsoleWindow.output("Module is arb, so cannot be constructed");
			if(!storedSetModule.checkArb() || storedSetModule.checkEqArb()){
				ConsoleWindow.output("Constructing Set Notation Module definition using Set Notation modules");
				storedConstruction=GenerateDecomposition.generateDecomposition(storedSetModule);
				ConstructionTab.instance.NetworkListTextArea.setText(storedConstruction.printCircuit());
				ConstructionTab.instance.DISetTextArea.setText(storedConstruction.getDIAlgebraRepresentation());
				ConsoleWindow.output("Construction complete");
			}
		}
	}

	/* Attempts to build the module object for the entered (or loaded from preset) definition. It utilises parsing functionality
	 * from ParseSetNotationModule. It then checks various module properties which are relevant to construction generation */
	public void loadModule(String module) {
		boolean ok=false;
		ConstructionTab.instance.StatusLabel.setText("Status: N/A");
		if(module.equals("")){
			validSetModule=false;
			storedSetModule=null;
			storedConstruction=null;
			moduleConstructible=false;
			ConstructionTab.instance.NetworkListTextArea.setText("");
			ConstructionTab.instance.DISetTextArea.setText("");
			ok=true;
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
				ConstructionTab.instance.ModuleDefinitionTextArea.setText(parsedResult.printModule());

				ConsoleWindow.output("Set Notation Module definition successfully parsed, checking properties");

				boolean eqarb=false;
				boolean arb = parsedResult.checkArb();
				if(arb){
					eqarb = parsedResult.checkEqArb();
				}
				boolean barb = parsedResult.checkBarb();
				if(!arb && !barb){
					ConsoleWindow.output("Set Notation Module definition is non-arb non-b-arb and can be constructed with {F,J,RT,IRT} - "
							+ "construction will use arbitrary reversible-serial modules and arbitrary MxN Joins/Forks");
					ConstructionTab.instance.StatusLabel.setText("<html>Status: non-arb, non-b-arb: construction is<br> symmetric non-arb, non-b-arb</html>");
					moduleConstructible=true;
				}
				else if(!arb && barb){
					ConsoleWindow.output("Set Notation Module definition is non-arb b-arb and can be constructed with {F,J,RT,IRT,M} - "
							+ "construction will use arbitrary reversible-serial modules, arbitrary MxN Joins, and Forks and Merges");
					ConstructionTab.instance.StatusLabel.setText("<html>Status: non-arb, b-arb: construction uses<br>non-arbstage then Merge/Fork trees</html>");
					moduleConstructible=true;
				}
				else if(eqarb && !barb){
					ConsoleWindow.output("Set Notation Module definition is eq-arb non-b-arb and can be constructed with {M-1,F,J,RT,IRT} or {ATS,F,J,RT,IRT} - "
							+ "construction will use arbitrary reversible-serial modules, inverse Merges, and arbitrary MxN Joins/Forks");
					ConstructionTab.instance.StatusLabel.setText("<html>Status: eq-arb, non-b-arb: construction uses<br>modified non-arb stage, "
							+ "then standard non-b-arb construction</html>");
					moduleConstructible=true;
				}
				else if(eqarb && barb){
					ConsoleWindow.output("Set Notation Module definition is eq-arb b-arb and can be constructed with {M-1,F,J,RT,IRT,M} or {ATS,F,J,RT,IRT,M} - "
							+ "construction will use arbitrary reversible-serial modules, inverse Merges, arbitrary MxN Joins, and Forks and Merges");
					ConstructionTab.instance.StatusLabel.setText("<html>Status: eq-arb, b-arb: construction uses<br>modified"
							+ "non-arb stage, then Merge/Fork trees</html>");
					moduleConstructible=true;
				}
				else{
					ConsoleWindow.output("Set Notation Module definition is not non-arb or eq-arb and so cannot be constructed using this software. "
							+ "Converting to extended sequential machine and constructing using details in the thesis is possible");
					ConstructionTab.instance.StatusLabel.setText("<html>Status: general arb: not possible</html>");
					moduleConstructible=true;
				}
			}
		}

		if(ok){
			editingSetModule=false;
			ConstructionTab.instance.EditButton.setText("Edit Definition");
			ConstructionTab.instance.ModuleDefinitionTextArea.setEditable(false);

			ConstructionTab.instance.PresetBox.setEnabled(true);
			ConstructionTab.instance.PresetLoadButton.setEnabled(true);
			ConstructionTab.instance.PresetSaveButton.setEnabled(true);

			if(moduleConstructible){
				ConstructionTab.instance.ConstructSetButton.setEnabled(true);
			}
			ConsoleWindow.output("GUI controls re-enabled");
		}
	}

}
