package GUI;

import java.awt.Font;

import javax.swing.*;

import GUIListeners.ConstructionTabListener;

/* Class for handling GUI generation of the Construction tab. GUI events are 
 * handled by a static instance of ConstructionTabListener. Please see that 
 * class for info. */
@SuppressWarnings("serial")
public class ConstructionTab extends JPanel {

	public static ConstructionTab instance = new ConstructionTab();
    
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
	public JButton ConstructSetButton = new JButton("Construct");
	
	public JLabel NetworkListLabel = new JLabel("List of modules and wires in construction");
	public JTextArea NetworkListTextArea = new JTextArea();
	public JScrollPane NetworkListTextAreaPane = new JScrollPane(NetworkListTextArea);

	public JLabel DISetLabel = new JLabel("DI-Set algebra definition of construction");
	public JTextArea DISetTextArea = new JTextArea();
	public JScrollPane DISetTextAreaPane = new JScrollPane(DISetTextArea);
    
    public ConstructionTab() {
    	
        NetworkListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ModuleDefinitionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        DISetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Font font = new Font("Verdana", Font.BOLD, 16);
    	ModuleDefinitionTextArea.setFont(font);
    	NetworkListTextArea.setFont(font);
    	DISetTextArea.setFont(font);
    	
        PresetLoadButton.addActionListener(ConstructionTabListener.instance);
        PresetSaveButton.addActionListener(ConstructionTabListener.instance);
        
        EditButton.setActionCommand("EditSet");
        EditButton.addActionListener(ConstructionTabListener.instance);
        SetHelpButton.addActionListener(ConstructionTabListener.instance);
        ConstructSetButton.addActionListener(ConstructionTabListener.instance);

        ConstructSetButton.setEnabled(false);
    	ModuleDefinitionTextArea.setEditable(false);
    	NetworkListTextArea.setEditable(false);
    	DISetTextArea.setEditable(false);

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
                    .addComponent(ModuleDefinitionLabel, GroupLayout.Alignment.CENTER)
                    .addComponent(ModuleDefinitionTextAreaPane,0,500,Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EditButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StatusLabel)
                    )
                    .addComponent(SetHelpButton,GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ConstructSetButton)
                    )                                            
                    .addComponent(DISetLabel, GroupLayout.Alignment.CENTER)
                    .addComponent(DISetTextAreaPane,0,200,Short.MAX_VALUE)
                )
                .addGap(12,12,12)
                .addGroup(layout.createParallelGroup()
                    .addComponent(NetworkListLabel, GroupLayout.Alignment.CENTER)
                    .addComponent(NetworkListTextAreaPane,0,500,Short.MAX_VALUE)
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
                .addComponent(NetworkListLabel)
                .addComponent(ModuleDefinitionLabel)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(ModuleDefinitionTextAreaPane,100,100,Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(EditButton)
                        .addComponent(SetHelpButton)
                        .addComponent(StatusLabel,GroupLayout.Alignment.CENTER)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                     .addComponent(ConstructSetButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(DISetLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(DISetTextAreaPane,100,100,Short.MAX_VALUE)
                )
                .addComponent(NetworkListTextAreaPane)
            )
            .addContainerGap()        
        );
    }                              
}
