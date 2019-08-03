package GUIListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JOptionPane;

import DISetAlgebraLTSStructure.Transition;
import DISetAlgebraOperations.CalculateTransitions;
import DISetAlgebraStructure.PartiallyVisibleNetwork;
import GUI.ConsoleWindow;
import GUI.DISetAlgebraInteractiveExecutionScreen;
import GUI.DISetAlgebraTab;

/* Process GUI events from the DI-Set Algebra tab's Interactive Execution screen. It also acts as an intermediate 
 * between the GUI aspects of the software and the various back-end algorithms and operations */
public class DISetAlgebraInteractiveExecutionListener implements ActionListener{

	/* The static singleton instance of this class that is accessed by the GUI package */
	public static DISetAlgebraInteractiveExecutionListener instance = new DISetAlgebraInteractiveExecutionListener();
	
	/* Stores the list of available transitions which may be chosen by the user, for the current
	 * DI-Set algebra term being displayed */
	private Vector<Transition> availableTransitions = new Vector<Transition>();
	
	/* Stores the current state of the DI-Set algebra network definition which we are executing */
	private PartiallyVisibleNetwork storedNetwork=null;

	/* Handles JButton events, disabling/re-enabling GUI elements appropriately. */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		/* Closes the Interactive Execution screen and returns the DI-Set Algebra tab to the Main screen */
		if(command.equals("Cancel")){
			DISetAlgebraTab.instance.showMainScreen();
		}
		
		/* Applies the currently selected transition (selected numerically in the GUI) to the current
		 * stored the DI-Set algebra network term, then recalculates the list of available transitions from
		 * the new term and displays them to the user */
		else if(command.equals("Apply") || command.equals("Enter")){
			boolean ok=false;
			int selection=0;
			try{
				selection = Integer.parseInt(DISetAlgebraInteractiveExecutionScreen.instance.InputField.getText());
				if(selection>0 && selection<=availableTransitions.size()){
					ok=true;
				}
			}
			catch(Exception e1){
				ok=false;
			}
			if(ok){
				ConsoleWindow.output(selection+"th transition selected by user");
				DISetAlgebraInteractiveExecutionScreen.instance.InputField.setText("");
				DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.append("\nTransition "+selection+" selected, resulting network:\n");
				storedNetwork=availableTransitions.get(selection-1).getTargetStateTerm();
				DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.append("    "+storedNetwork.printNetworkWithoutName()+"\n\n");	
				ConsoleWindow.output("Calculating list of available transitions from the current state");
				availableTransitions=CalculateTransitions.calculateTransitions(storedNetwork);
				if(availableTransitions.size()>0){
					DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.append(printTransitionArrows()+"\n");
				}
				else{
					DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.append("No more transitions from here");
				}
			}			
			else{
				DISetAlgebraInteractiveExecutionScreen.instance.InputField.setText("");
				JOptionPane.showMessageDialog(null,"Invalid selection");
			}
			DISetAlgebraInteractiveExecutionScreen.instance.InputField.requestFocus();
		}
	}

	/* Prints out the available transitions (already calculated) from the current network term */
	public String printTransitionArrows(){
		StringBuffer output = new StringBuffer();
		for(int i=1;i<=availableTransitions.size();i++){
			Transition currentTransition = availableTransitions.get(i-1);
			output.append(i+": "+currentTransition.printArrow());
			if(i<availableTransitions.size()){
				output.append("\n");
			}
		}
		return output.toString();
	}

	/* Clears all stored transitions and the network term, and resets the DI-Set algebra tab's Interactive Execution screen. This is called
	 * whenever the screen is loaded by the user */
	public void resetExecutionScreen(int net){
		availableTransitions.clear();
		DISetAlgebraInteractiveExecutionScreen.instance.TitleLabel.setText("Interactive Execution of Network " + net);
		if(net==1){
			storedNetwork=DISetAlgebraMainListener.instance.getStoredNetwork(1);
		}
		else{
			storedNetwork=DISetAlgebraMainListener.instance.getStoredNetwork(2);
		}
		DISetAlgebraInteractiveExecutionScreen.instance.InputField.setText("");
		DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.setText("Initial network state: \n"+storedNetwork.printNetwork()+"\n");

		availableTransitions=CalculateTransitions.calculateTransitions(storedNetwork);
		if(availableTransitions.size()>0){
			DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.append(printTransitionArrows()+"\n");
		}
		else{
			DISetAlgebraInteractiveExecutionScreen.instance.ExecutionOutputTextArea.append("No available transitions. Deadlock!");
		}
	}

}
