package GUI;

import java.awt.Font;
import javax.swing.*;
import GUIListeners.DISetAlgebraMainListener;

/* Class for handling GUI generation of the DI-Set Algebra tab's Main screen. 
 * GUI events are handled by a static instance of DISetAlgebraMainListener. 
 * Please see that class for info. */
@SuppressWarnings("serial")
public class DISetAlgebraMainScreen extends JPanel{

public static DISetAlgebraMainScreen instance = new DISetAlgebraMainScreen();
	
public JLabel Preset1Label = new JLabel("DI-Set presets");
public JComboBox<String> Preset1Box = new JComboBox<>();
public JButton Preset1LoadButton = new JButton("Load");
public JButton Preset1SaveButton = new JButton("Save");

public JLabel Preset2Label = new JLabel("DI-Set presets");
public JComboBox<String> Preset2Box = new JComboBox<>();
public JButton Preset2LoadButton = new JButton("Load");
public JButton Preset2SaveButton = new JButton("Save");

public JLabel Network1Label = new JLabel("Network 1");
public JTextArea Network1TextArea = new JTextArea();
public JScrollPane Network1TextAreaPane = new JScrollPane(Network1TextArea);
public JButton ManualInput1Button = new JButton("Edit Definition");
public JButton GUIInput1Button = new JButton("GUI Input");
public JButton InteractiveExecution1Button = new JButton("Interactive Execution");

public JLabel Network2Label = new JLabel("Network 2");
public JTextArea Network2TextArea = new JTextArea();
public JScrollPane Network2TextAreaPane = new JScrollPane(Network2TextArea);
public JButton ManualInput2Button = new JButton("Edit Definition");
public JButton GUIInput2Button = new JButton("GUI Input");
public JButton InteractiveExecution2Button = new JButton("Interactive Execution");

public JButton HelpButton = new JButton("?");
public JButton LTSSimulationButton = new JButton("LTS Analysis and (Bi)Simulation");
                 
	public DISetAlgebraMainScreen() {
	
		Network1Label.setHorizontalAlignment(SwingConstants.CENTER);
		Network2Label.setHorizontalAlignment(SwingConstants.CENTER);
		
		Font font = new Font("Verdana", Font.BOLD, 16);
		Network1TextArea.setFont(font);
		Network2TextArea.setFont(font);
		
		ManualInput1Button.setActionCommand("Manual Input1");
		GUIInput1Button.setActionCommand("GUI Input1");
		
		ManualInput2Button.setActionCommand("Manual Input2");
		GUIInput2Button.setActionCommand("GUI Input2");
		
        HelpButton.addActionListener(DISetAlgebraMainListener.instance);
		
    	Preset1LoadButton.setActionCommand("Load1");
    	Preset1SaveButton.setActionCommand("Save1");
    	Preset1LoadButton.addActionListener(DISetAlgebraMainListener.instance);
    	Preset1SaveButton.addActionListener(DISetAlgebraMainListener.instance);
		
		Preset2LoadButton.addActionListener(DISetAlgebraMainListener.instance);
		Preset2SaveButton.addActionListener(DISetAlgebraMainListener.instance);
		
		ManualInput1Button.addActionListener(DISetAlgebraMainListener.instance);
		GUIInput1Button.addActionListener(DISetAlgebraMainListener.instance);
		ManualInput2Button.addActionListener(DISetAlgebraMainListener.instance);
		GUIInput2Button.addActionListener(DISetAlgebraMainListener.instance);
		LTSSimulationButton.addActionListener(DISetAlgebraMainListener.instance);
		
		InteractiveExecution1Button.setActionCommand("Interactive1");
		InteractiveExecution2Button.setActionCommand("Interactive2");
		InteractiveExecution1Button.addActionListener(DISetAlgebraMainListener.instance);
		InteractiveExecution2Button.addActionListener(DISetAlgebraMainListener.instance);

		Network1TextArea.setEditable(false);
		Network2TextArea.setEditable(false);
		InteractiveExecution1Button.setEnabled(false);
		InteractiveExecution2Button.setEnabled(false);
		LTSSimulationButton.setEnabled(false);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(Network1Label,GroupLayout.Alignment.CENTER)
					.addGroup(layout.createSequentialGroup()
							.addComponent(Preset1Label)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(Preset1Box, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(Preset1LoadButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(Preset1SaveButton)
					)
					.addComponent(Network1TextAreaPane,0,500,Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(ManualInput1Button)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(GUIInput1Button)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(InteractiveExecution1Button)
					)
					.addComponent(HelpButton)
				)
				.addGap(12)
				.addGroup(layout.createParallelGroup()
					.addComponent(Network2Label,GroupLayout.Alignment.CENTER)
					.addGroup(layout.createSequentialGroup()
							.addComponent(Preset2Label)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(Preset2Box, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(Preset2LoadButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(Preset2SaveButton)
					)
					.addComponent(Network2TextAreaPane,0,500,Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(ManualInput2Button)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(GUIInput2Button)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(InteractiveExecution2Button)
					)	
					.addComponent(LTSSimulationButton, GroupLayout.Alignment.TRAILING)
				)
				.addContainerGap()		
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGap(5)
			.addGroup(layout.createParallelGroup()
				.addComponent(Network1Label)
				.addComponent(Network2Label)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
					.addComponent(Preset1Label,GroupLayout.Alignment.CENTER)
					.addComponent(Preset1Box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(Preset1LoadButton)
					.addComponent(Preset1SaveButton)					
					.addComponent(Preset2Label,GroupLayout.Alignment.CENTER)
					.addComponent(Preset2Box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(Preset2LoadButton)
					.addComponent(Preset2SaveButton)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(Network1TextAreaPane)
				.addComponent(Network2TextAreaPane)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(ManualInput1Button)
				.addComponent(GUIInput1Button)
				.addComponent(InteractiveExecution1Button)
				.addComponent(ManualInput2Button)
				.addComponent(GUIInput2Button)
				.addComponent(InteractiveExecution2Button)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(HelpButton)
				.addComponent(LTSSimulationButton)
			)
			.addContainerGap()
		);
	}
}