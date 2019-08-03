package GUI;

import java.awt.Font;
import javax.swing.*;
import GUIListeners.EnvironmentTabListener;

/* Class for handling GUI generation of the Environment Generation tab. 
 * GUI events are handled by a static instance of EnvironmentTabListener. 
 * Please see that class for info. */
@SuppressWarnings("serial")
public class EnvironmentTab extends JPanel {

	public static EnvironmentTab instance = new EnvironmentTab();
    
	public JLabel PresetLabel = new JLabel("Set Notation presets");
	public JComboBox<String> PresetBox = new JComboBox<>();
	public JButton PresetLoadButton = new JButton("Load");
	public JButton PresetSaveButton = new JButton("Save");

	public JLabel ModuleDefinitionLabel = new JLabel("Set Notation module definition");
	public JTextArea ModuleDefinitionTextArea = new JTextArea();
	public JScrollPane ModuleDefinitionTextAreaPane = new JScrollPane(ModuleDefinitionTextArea);
	public JButton EditButton = new JButton("Edit Definition");
    public JButton SetHelpButton = new JButton("?");
	public JLabel StatusLabel = new JLabel("Status: N/A");  
	public JButton GenerateButton = new JButton("Generate Environment");
    public JLabel StateRemovalLabel =new JLabel("Remove duplicate states");
    public JCheckBox StateRemoval = new JCheckBox();
	
	public JLabel EnvironmentLabel = new JLabel("Generated maximal environment definition");
	public JTextArea EnvironmentTextArea = new JTextArea();
	public JScrollPane EnvironmentTextAreaPane = new JScrollPane(EnvironmentTextArea);
    
    public EnvironmentTab() {
    	
    	EnvironmentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ModuleDefinitionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	Font font = new Font("Verdana", Font.BOLD, 16);
    	ModuleDefinitionTextArea.setFont(font);
    	EnvironmentTextArea.setFont(font);
    	
        PresetLoadButton.addActionListener(EnvironmentTabListener.instance);
        PresetSaveButton.addActionListener(EnvironmentTabListener.instance);
        
        EditButton.setActionCommand("EditSet");
        EditButton.addActionListener(EnvironmentTabListener.instance);
        SetHelpButton.addActionListener(EnvironmentTabListener.instance);
        
        GenerateButton.addActionListener(EnvironmentTabListener.instance);

    	ModuleDefinitionTextArea.setEditable(false);
    	EnvironmentTextArea.setEditable(false);
    	GenerateButton.setEnabled(false);
    	StateRemoval.setSelected(true);
    	
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PresetLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PresetBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PresetLoadButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PresetSaveButton)
                .addContainerGap()
            )
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(ModuleDefinitionLabel,GroupLayout.Alignment.CENTER)
                    .addComponent(ModuleDefinitionTextAreaPane,0,500,Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EditButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StatusLabel)
                    )
                    .addComponent(SetHelpButton,GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(GenerateButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StateRemovalLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StateRemoval)
                    )
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(EnvironmentLabel,GroupLayout.Alignment.CENTER)
                    .addComponent(EnvironmentTextAreaPane,0,500,Short.MAX_VALUE)
                )
                .addContainerGap()
            )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(PresetLabel,GroupLayout.Alignment.CENTER)
                .addComponent(PresetBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(PresetLoadButton)
                .addComponent(PresetSaveButton)
            )
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(ModuleDefinitionLabel)
                .addComponent(EnvironmentLabel)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(ModuleDefinitionTextAreaPane)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(EditButton)
                        .addComponent(SetHelpButton)
                        .addComponent(StatusLabel,GroupLayout.Alignment.CENTER)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(GenerateButton)
                        .addComponent(StateRemovalLabel,GroupLayout.Alignment.CENTER)
                        .addComponent(StateRemoval,GroupLayout.Alignment.CENTER)
                    )
                )
                .addComponent(EnvironmentTextAreaPane)
            )
            .addContainerGap()
        );
    }                              
}
