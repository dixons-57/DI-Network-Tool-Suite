package GUIListeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import DISetAlgebraLTSStructure.LTSDefinition;
import DISetAlgebraLTSStructure.Simulation;
import DISetAlgebraOperations.LTSAnalysis;
import DISetAlgebraOperations.LTSGeneration;
import DISetAlgebraOperations.SimulationVerification;
import GUI.ConsoleWindow;
import GUI.DISetAlgebraLTSScreen;
import GUI.DISetAlgebraTab;
import InputOutputOperations.GeneralOperations;
import InputOutputOperations.ParseDISetAlgebraSimulation;
import InputOutputOperations.SimulationPresets;

/* Process GUI events from the DI-Set Algebra tab's LTS screen. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class DISetAlgebraLTSListener implements ActionListener {

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static DISetAlgebraLTSListener instance = new DISetAlgebraLTSListener();

	/* Records whether the simulation definition is currently being edited */
	private boolean editingSimulation=false;
	
	/* Stores the left DI-Set algebra network's LTS */
	private LTSDefinition storedNetwork1LTS=null;
	
	/* Stores the right DI-Set algebra network's LTS */
	private LTSDefinition storedNetwork2LTS=null;
	
	/* Stores the simulation relation definition */
	private Simulation storedSimulation= null;

	/* Records whether a valid LTS definition has been generated for the left network */
	private boolean validNetwork1LTS=false;
	
	/* Records whether a valid LTS definition has been generated for the right network */
	private boolean validNetwork2LTS=false;
	
	/* Records whether a valid simulation relation definition has been parsed and stored */
	private boolean validSimulation=false;

	/* Handles JButton events, disabling/re-enabling GUI elements appropriately. */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		/* Generates the LTS for the left stored DI-Set algebra definition */
		if(command.equals("GenerateLTS1Button")){
			try {
				ConsoleWindow.output("Generating LTS for network 1");
				storedNetwork1LTS=LTSGeneration.computeLTS(DISetAlgebraMainListener.instance.getStoredNetwork(1),DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck1.isSelected());
				ConsoleWindow.output("LTS generation complete");
			} catch (Exception e1) {
			}
			if(storedNetwork1LTS!=null){
				validNetwork1LTS=true;
				DISetAlgebraLTSScreen.instance.Network1TextArea.setText(storedNetwork1LTS.printAll());
				DISetAlgebraLTSScreen.instance.AnalyseLTS1Button.setEnabled(true);
				DISetAlgebraLTSScreen.instance.GenerateLTS1Button.setEnabled(false);
				if(validNetwork2LTS){
					DISetAlgebraLTSScreen.instance.ManualInputButton.setEnabled(true);
					DISetAlgebraLTSScreen.instance.PresetLoadButton.setEnabled(true);
					DISetAlgebraLTSScreen.instance.PresetSaveButton.setEnabled(true);
				}
			}
		}	
		
		/* Analyses properties of the left network's LTS (clashing and safety) */
		else if(command.equals("AnalyseLTS1Button")){
			ConsoleWindow.output("Analysing properties of network 1 LTS");
			boolean[] LTSProperties = LTSAnalysis.analyseProperties(storedNetwork1LTS);
			JOptionPane.showMessageDialog(null, "Clashes: "+LTSProperties[0]+"\nAlways safe: "+LTSProperties[1]);
		}
		
		/* Generates the LTS for the left stored DI-Set algebra definition */
		else if(command.equals("GenerateLTS2Button")){
			try {
				ConsoleWindow.output("Generating LTS for network 2");
				storedNetwork2LTS=LTSGeneration.computeLTS(DISetAlgebraMainListener.instance.getStoredNetwork(2),DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck2.isSelected());
				ConsoleWindow.output("LTS generation complete");
			} catch (Exception e1) {
			}
			if(storedNetwork2LTS!=null){
				validNetwork2LTS=true;
				DISetAlgebraLTSScreen.instance.Network2TextArea.setText(storedNetwork2LTS.printAll());
				DISetAlgebraLTSScreen.instance.AnalyseLTS2Button.setEnabled(true);
				DISetAlgebraLTSScreen.instance.GenerateLTS2Button.setEnabled(false);
				if(validNetwork1LTS){
					DISetAlgebraLTSScreen.instance.ManualInputButton.setEnabled(true);
					DISetAlgebraLTSScreen.instance.PresetLoadButton.setEnabled(true);
					DISetAlgebraLTSScreen.instance.PresetSaveButton.setEnabled(true);
				}
			}
		}
		
		/* Analyses properties of the left network's LTS (clashing and safety) */
		else if(command.equals("AnalyseLTS2Button")){
			ConsoleWindow.output("Analysing properties of network 2 LTS");
			boolean[] results = LTSAnalysis.analyseProperties(storedNetwork2LTS);
			JOptionPane.showMessageDialog(null, "Clashes: "+results[0]+"\nAlways safe: "+results[1]);
		}
		
		/* Initiates/finalises user editing of the simulation relation definition, parsing of the definition data is attempted 
		 * if this button is clicked when the user is already editing. */
		else if(command.equals("Manual")){
			if(!editingSimulation){
				editingSimulation=true;
				ConsoleWindow.output("Editing Simulation definition");
				DISetAlgebraLTSScreen.instance.ManualInputButton.setText("Parse Definition");
				DISetAlgebraLTSScreen.instance.SimulationTextArea.setEditable(true);

				DISetAlgebraLTSScreen.instance.PresetLoadButton.setEnabled(false);
				DISetAlgebraLTSScreen.instance.PresetSaveButton.setEnabled(false);
				DISetAlgebraLTSScreen.instance.AnalyseLTS1Button.setEnabled(false);
				DISetAlgebraLTSScreen.instance.AnalyseLTS2Button.setEnabled(false);

				ConsoleWindow.output("Other GUI controls disabled");
			}
			else if(editingSimulation){
				ConsoleWindow.output("Parsing Simulation definition");
				String simulationText = DISetAlgebraLTSScreen.instance.SimulationTextArea.getText();
				loadSimulation(simulationText);
			}	
		}
		
		/* Displays helpful info to the user about how to enter a Set Notation module definition*/
		if(command.equals("?")){
			JOptionPane.showMessageDialog(null, "Enter a series of state pairs separated by semi-colons. Line breaks are also allowed.\n"
					+ "Also don't worry about spaces.\n\n"
					+ "e.g:\n\n(0,0);(1,1);(2,2);(2,3);(3,4);(4,5);\n\n"
					+ "Unlike the theoretical basis of the relation defined in the thesis, when defining state\n" 
					+ "pairs, the software requires that the left network (also known as Network 1) is always on\n"
					+ "the left side of the relation, and right network (also known as Network 2) is always on the\n"
					+ "right side of the relation. Hence (s,s') is the format of all pairs, where s is from the\n"
					+ "left LTS/network, and s' is from the right LTS/network.\n\n"
					+ "If you enter a numerical value which is not a valid state of the appropriate LTS, the\n"
					+ "software will complain and you will have to modify the definition.");
		}
		
		/* Analyses the stored simulation relation definition to check whether it is valid. It checks
		 * whether it is a valid bisimulation, simulation for network 1 simulating network 2, or simulation
		 * for network 2 simulating network 1, depending on what is selected in the GUI */
		else if(command.equals("Verify")){
			int simulationType;
			if(DISetAlgebraLTSScreen.instance.SimulationTypeOption1.isSelected()){
				simulationType=1;
			}
			else if(DISetAlgebraLTSScreen.instance.SimulationTypeOption2.isSelected()){
				simulationType=2;
			}
			else{
				simulationType=3;
			}
			boolean storedSimulations = SimulationVerification.validSimulation(storedSimulation,simulationType);
			String initialPresence="";
			if(!storedSimulation.pairPresent(0, 0)){
				initialPresence=", but the initial state is not present for some reason";
			}
			if(simulationType==1){
				if(storedSimulations){
					JOptionPane.showMessageDialog(null, "Simulation is a valid bisimulation"+initialPresence);
				}
				else{
					JOptionPane.showMessageDialog(null, "Simulation not a valid bisimulation"+initialPresence);
				}
			}
			else if(simulationType==2){
				if(storedSimulations){
					JOptionPane.showMessageDialog(null, "Simulation is a valid simulation for network 1 simulating network 2"+initialPresence);
				}
				else{
					JOptionPane.showMessageDialog(null, "Simulation not a valid simulation for network 1 simulating network 2"+initialPresence);
				}
			}
			else if(simulationType==3){
				if(storedSimulations){
					JOptionPane.showMessageDialog(null, "Simulation is a valid simulation for network 2 simulating network 1"+initialPresence);
				}
				else{
					JOptionPane.showMessageDialog(null, "Simulation not a valid simulation for network 2 simulating network 1"+initialPresence);
				}
			}
		}
		
		/* Closes the LTS screen and returns the DI-Set Algebra tab to the Main screen */
		else if(command.equals("Cancel")){
			DISetAlgebraTab.instance.showMainScreen();
		}
		
		/* Attempts to load the selected preset which stores a simulation relation definition */
		if(command.equals("Load")){
			if(DISetAlgebraLTSScreen.instance.PresetBox.getSelectedIndex()!=-1 && !DISetAlgebraLTSScreen.instance.PresetBox.getSelectedItem().toString().equals("")){
				ConsoleWindow.output("Loading selected Simulation preset: "+DISetAlgebraLTSScreen.instance.PresetBox.getSelectedItem()+".bsp");
				if(SimulationPresets.loadPreset(DISetAlgebraLTSScreen.instance.PresetBox.getSelectedItem().toString())){
					ConsoleWindow.output("Loaded preset: "+DISetAlgebraLTSScreen.instance.PresetBox.getSelectedItem()+".bsp");
				}
				else{
					ConsoleWindow.output("Failed to load preset: "+DISetAlgebraLTSScreen.instance.PresetBox.getSelectedItem()+".bsp");
				}
			}
			else{
				ConsoleWindow.output("No Simulation preset selected");
			}
		}
		
		/* Attempts to save the existing simulation relation definition as a preset */
		else if(command.equals("Save")){
			if(validSimulation){
				ConsoleWindow.output("Saving Simulation preset");
				while(true){
					String input = JOptionPane.showInputDialog("Please enter a filename using only alphanumeric characters");
					if(input==null){
						ConsoleWindow.output("Saving of preset cancelled by user");
						break;
					}
					else if(GeneralOperations.alphaNumeric(input,true) && !input.equals("")){
						boolean success = SimulationPresets.savePreset(input);
						if(success){
							ConsoleWindow.output("Saved preset: "+input+".bsp");
							SimulationPresets.loadFileList();
							ConsoleWindow.output("Reloaded Simulation Presets");
						}
						else{
							ConsoleWindow.output("Failed to save preset: "+input+".bsp");
						}
						break;
					}
					ConsoleWindow.output("Invalid alphanumeric input. Please try again or cancel");
				}
			}
			else{
				ConsoleWindow.output("No valid simulation definition to save as a preset");
			}
		}
	}

	/* Attempts to build the simulation relation definition object for the entered (or loaded from preset) definition.
	 * It utilises parsing functionality from ParseDISetAlgebraSimulation. */
	public void loadSimulation(String input) {
		boolean ok=false;

		if(input.equals("")){
			storedSimulation=null;
			validSimulation=false;
			ok=true;
			DISetAlgebraLTSScreen.instance.VerifyButton.setEnabled(false);
			ConsoleWindow.output("Simulation definition cleared");
		}
		else{
			Simulation parsedResult;
			try {
				parsedResult = ParseDISetAlgebraSimulation.parseDefinition(input,storedNetwork1LTS,storedNetwork2LTS);
			} 
			catch (Exception e1) {
				parsedResult=null;
			}
			if(parsedResult==null){
				ConsoleWindow.output("Error parsing stored Simulation definition - please check and try again");

				/*If this method is called by preset loader */
				if(!editingSimulation){
					JOptionPane.showMessageDialog(null,"Selected preset is incompatible with current LTSs due to states in pairs not existing in LTSs");
					ConsoleWindow.output("Selected preset is incompatible with current LTSs due to states in pairs not existing in current LTSs");
				}
			}
			else{				
				ok=true;
				DISetAlgebraLTSScreen.instance.VerifyButton.setEnabled(true);
				validSimulation=true;
				storedSimulation=parsedResult;
				DISetAlgebraLTSScreen.instance.SimulationTextArea.setText(parsedResult.printDefinition());
				
				ConsoleWindow.output("Simulation definition successfully parsed");
			}
		}
		if(ok){
			editingSimulation=false;
			DISetAlgebraLTSScreen.instance.SimulationTextArea.setEditable(false);
			DISetAlgebraLTSScreen.instance.ManualInputButton.setText("Edit Definition");
			DISetAlgebraLTSScreen.instance.AnalyseLTS1Button.setEnabled(true);
			DISetAlgebraLTSScreen.instance.AnalyseLTS2Button.setEnabled(true);			
			DISetAlgebraLTSScreen.instance.PresetLoadButton.setEnabled(true);
			DISetAlgebraLTSScreen.instance.PresetSaveButton.setEnabled(true);

			ConsoleWindow.output("GUI controls re-enabled");
		}
	}

	/* Clears all LTS objects and the simulation definition and resets the DI-Set algebra tab's LTS screen */
	public void resetLTSScreen(){
		DISetAlgebraLTSScreen.instance.AnalyseLTS1Button.setEnabled(false);
		DISetAlgebraLTSScreen.instance.AnalyseLTS2Button.setEnabled(false);
		DISetAlgebraLTSScreen.instance.PresetLoadButton.setEnabled(false);
		DISetAlgebraLTSScreen.instance.PresetSaveButton.setEnabled(false);
		DISetAlgebraLTSScreen.instance.ManualInputButton.setEnabled(false);
		DISetAlgebraLTSScreen.instance.VerifyButton.setEnabled(false);
		DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck1.setSelected(true);
		DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck2.setSelected(true);
		DISetAlgebraLTSScreen.instance.SimulationTextArea.setEditable(false);
		DISetAlgebraLTSScreen.instance.ManualInputButton.setText("Edit Definition");
		
		validNetwork1LTS=false;
		validNetwork2LTS=false;
		validSimulation=false;
		editingSimulation=false;
		
		DISetAlgebraLTSScreen.instance.Network1TextArea.setText("");
		DISetAlgebraLTSScreen.instance.Network2TextArea.setText("");
		DISetAlgebraLTSScreen.instance.SimulationTextArea.setText("");

		if(DISetAlgebraMainListener.instance.getNetworkValid(1)){
			DISetAlgebraLTSScreen.instance.GenerateLTS1Button.setEnabled(true);
			DISetAlgebraLTSScreen.instance.Network1TextArea.setBackground(Color.WHITE);
			DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck1.setEnabled(true);
		}
		else{
			DISetAlgebraLTSScreen.instance.GenerateLTS1Button.setEnabled(false);
			DISetAlgebraLTSScreen.instance.Network1TextArea.setBackground(Color.GRAY);
			DISetAlgebraLTSScreen.instance.SimulationTextArea.setBackground(Color.GRAY);
			DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck1.setEnabled(false);
		}
		if(DISetAlgebraMainListener.instance.getNetworkValid(2)){
			DISetAlgebraLTSScreen.instance.GenerateLTS2Button.setEnabled(true);
			DISetAlgebraLTSScreen.instance.Network2TextArea.setBackground(Color.WHITE);
			DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck2.setEnabled(true);
		}
		else{
			DISetAlgebraLTSScreen.instance.GenerateLTS2Button.setEnabled(false);
			DISetAlgebraLTSScreen.instance.Network2TextArea.setBackground(Color.GRAY);
			DISetAlgebraLTSScreen.instance.SimulationTextArea.setBackground(Color.GRAY);
			DISetAlgebraLTSScreen.instance.InfiniteDetectionCheck2.setEnabled(false);
		}
		if(DISetAlgebraMainListener.instance.getNetworkValid(1) && DISetAlgebraMainListener.instance.getNetworkValid(2)){
			DISetAlgebraLTSScreen.instance.SimulationTextArea.setBackground(Color.WHITE);
		}
	}

}
