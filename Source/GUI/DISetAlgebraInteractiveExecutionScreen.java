package GUI;

import java.awt.Font;

import javax.swing.*;

import GUIListeners.DISetAlgebraInteractiveExecutionListener;

/* Class for handling GUI generation of the DI-Set Algebra tab's Interactive Execution screen. 
 * GUI events are handled by a static instance of DISetAlgebraInteractiveExecutionListener. 
 * Please see that class for info. */
@SuppressWarnings("serial")
public class DISetAlgebraInteractiveExecutionScreen extends JPanel {

	public static DISetAlgebraInteractiveExecutionScreen instance = new DISetAlgebraInteractiveExecutionScreen();
	
    public JLabel TitleLabel = new JLabel("Interactive Execution of Network 1");
    
    public JTextArea ExecutionOutputTextArea = new JTextArea();
    public JScrollPane ExecutionOutputTextAreaPane = new JScrollPane(ExecutionOutputTextArea);

    public JLabel SelectLabel = new JLabel("Select Transition");
    public JTextField InputField = new JTextField();
    public JButton ApplyTransitionButton = new JButton("Apply");
    
    public JButton CancelButton = new JButton("Cancel");
	
    public DISetAlgebraInteractiveExecutionScreen() {
    	
    	ExecutionOutputTextArea.setFont(new Font("Verdana", Font.BOLD, 16));
    	TitleLabel.setFont(new java.awt.Font("Tahoma", 1, 16));
    	TitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	
        CancelButton.addActionListener(DISetAlgebraInteractiveExecutionListener.instance);
    	ApplyTransitionButton.addActionListener(DISetAlgebraInteractiveExecutionListener.instance);
    	InputField.setActionCommand("Enter");
    	InputField.addActionListener(DISetAlgebraInteractiveExecutionListener.instance);
    	
    	ExecutionOutputTextArea.setEditable(false);
    	
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(TitleLabel,GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
            	.addContainerGap()
                .addComponent(ExecutionOutputTextAreaPane)
                .addContainerGap()
            )
            .addGroup(layout.createSequentialGroup() 
            	.addContainerGap()
                .addComponent(SelectLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InputField, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ApplyTransitionButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CancelButton)            
                .addContainerGap()
            )     
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(TitleLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(ExecutionOutputTextAreaPane)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(SelectLabel)
                .addComponent(ApplyTransitionButton)
                .addComponent(InputField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(CancelButton)
            )
            .addContainerGap()
        );
    }

}
