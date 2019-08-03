package GUI;

import java.awt.Font;
import javax.swing.*;
import GUIListeners.DISetAlgebraLTSListener;

/* Class for handling GUI generation of the DI-Set Algebra tab's LTS screen. 
 * GUI events are handled by a static instance of DISetAlgebraLTSListener. 
 * Please see that class for info. */
@SuppressWarnings("serial")
public class DISetAlgebraLTSScreen extends JPanel {

	public static DISetAlgebraLTSScreen instance = new DISetAlgebraLTSScreen();

	public JLabel TitleLabel = new JLabel("LTS Analysis");
	
	public JLabel Network1Label = new JLabel("Network 1");
	public JTextArea Network1TextArea = new JTextArea();
	public JScrollPane Network1TextAreaPane = new JScrollPane(Network1TextArea);
	public JLabel InfiniteDetectionLabel1 = new JLabel("Infinite detection");
	public JCheckBox InfiniteDetectionCheck1 = new JCheckBox();
	public JButton GenerateLTS1Button = new JButton("Generate");
	public JButton AnalyseLTS1Button = new JButton("Analyse");
    
	public JLabel Network2Label = new JLabel("Network 2");
	public JTextArea Network2TextArea = new JTextArea();
	public JScrollPane Network2TextAreaPane = new JScrollPane(Network2TextArea);
	public JLabel InfiniteDetectionLabel2 = new JLabel("Infinite detection");
	public JCheckBox InfiniteDetectionCheck2 = new JCheckBox();
	public JButton GenerateLTS2Button = new JButton("Generate");
	public JButton AnalyseLTS2Button= new JButton("Analyse");
    
	public JLabel SimulationLabel = new JLabel("Simulation (s,s') where s is in Network 1, s' is in Network 2");
	public JTextArea SimulationTextArea = new JTextArea();
	public JScrollPane SimulationTextAreaPane = new JScrollPane(SimulationTextArea);
    
	public JLabel PresetLabel = new JLabel("Simulation presets");
	public JComboBox<String> PresetBox = new JComboBox<>();
	public JButton PresetLoadButton = new JButton("Load");
	public JButton PresetSaveButton = new JButton("Save");
	
	public JLabel SimulationTypeLabel = new JLabel("Simulation Type");
	public ButtonGroup SimulationTypeGroup = new ButtonGroup();
	public JRadioButton SimulationTypeOption1 = new JRadioButton("Bisimulation");
	public JRadioButton SimulationTypeOption2 = new JRadioButton("1 Simulates 2");
	public JRadioButton SimulationTypeOption3 = new JRadioButton("2 Simulates 1");
	public JButton VerifyButton = new JButton("Verify");
	
	public JButton ManualInputButton = new JButton("Edit Definition");
    public JButton SimHelpButton = new JButton("?");
	public JButton CancelButton = new JButton("Cancel");
	
    public DISetAlgebraLTSScreen() {
        
        TitleLabel.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        TitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
    	Font font = new Font("Verdana", Font.BOLD, 12);
    	Network1TextArea.setFont(font);
    	Network2TextArea.setFont(font);
    	SimulationTextArea.setFont(font);
        Network1Label.setHorizontalAlignment(SwingConstants.CENTER);
        Network2Label.setHorizontalAlignment(SwingConstants.CENTER);
        SimulationLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	SimulationTypeGroup.add(SimulationTypeOption1);
    	SimulationTypeGroup.add(SimulationTypeOption2);
    	SimulationTypeGroup.add(SimulationTypeOption3);
    	
        GenerateLTS1Button.setActionCommand("GenerateLTS1Button");
        AnalyseLTS1Button.setActionCommand("AnalyseLTS1Button");
        
        GenerateLTS2Button.setActionCommand("GenerateLTS2Button");
        AnalyseLTS2Button.setActionCommand("AnalyseLTS2Button");

        ManualInputButton.setActionCommand("Manual");
        
        GenerateLTS1Button.addActionListener(DISetAlgebraLTSListener.instance);
        AnalyseLTS1Button.addActionListener(DISetAlgebraLTSListener.instance);
        
        GenerateLTS2Button.addActionListener(DISetAlgebraLTSListener.instance);
        AnalyseLTS2Button.addActionListener(DISetAlgebraLTSListener.instance);
        
        ManualInputButton.addActionListener(DISetAlgebraLTSListener.instance);
        SimHelpButton.addActionListener(DISetAlgebraLTSListener.instance);
        VerifyButton.addActionListener(DISetAlgebraLTSListener.instance);
        
    	PresetLoadButton.addActionListener(DISetAlgebraLTSListener.instance);
    	PresetSaveButton.addActionListener(DISetAlgebraLTSListener.instance);
        
        CancelButton.addActionListener(DISetAlgebraLTSListener.instance);

        Network1TextArea.setEditable(false);
        Network2TextArea.setEditable(false);
        SimulationTextArea.setEditable(false);
    	SimulationTypeOption1.setSelected(true);
    	InfiniteDetectionCheck1.setSelected(true);
    	InfiniteDetectionCheck2.setSelected(true);
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(TitleLabel,GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
            	.addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(Network1Label,GroupLayout.Alignment.CENTER)
                    .addComponent(Network1TextAreaPane,0,500,Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(InfiniteDetectionLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(InfiniteDetectionCheck1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GenerateLTS1Button)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AnalyseLTS1Button)
                    )
                    .addComponent(SimulationLabel,GroupLayout.Alignment.CENTER)
                    .addComponent(SimulationTextAreaPane,0,500,Short.MAX_VALUE)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(Network2Label,GroupLayout.Alignment.CENTER)
                    .addComponent(Network2TextAreaPane,0,500,Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(InfiniteDetectionLabel2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(InfiniteDetectionCheck2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GenerateLTS2Button)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AnalyseLTS2Button)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PresetLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PresetBox)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PresetSaveButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PresetLoadButton)
                    )
                    .addComponent(SimulationTypeLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SimulationTypeOption1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SimulationTypeOption2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SimulationTypeOption3)
                    )
                    .addGroup(layout.createSequentialGroup()
                    	.addComponent(ManualInputButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    	.addComponent(SimHelpButton)
                    )
                    .addComponent(VerifyButton)
                    .addComponent(CancelButton,GroupLayout.Alignment.TRAILING)
                )
                .addContainerGap()
            )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(TitleLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(Network1Label)
                .addComponent(Network2Label)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(Network1TextAreaPane,50,200,Short.MAX_VALUE)
                .addComponent(Network2TextAreaPane,50,200,Short.MAX_VALUE)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(InfiniteDetectionLabel1,GroupLayout.Alignment.CENTER)
                .addComponent(InfiniteDetectionCheck1,GroupLayout.Alignment.CENTER)
                .addComponent(GenerateLTS1Button)
                .addComponent(AnalyseLTS1Button)
                .addComponent(InfiniteDetectionLabel2,GroupLayout.Alignment.CENTER)
                .addComponent(InfiniteDetectionCheck2,GroupLayout.Alignment.CENTER)
                .addComponent(GenerateLTS2Button)
                .addComponent(AnalyseLTS2Button)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(SimulationLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                	.addComponent(SimulationTextAreaPane,50,200,Short.MAX_VALUE)
                    .addContainerGap()
                )                
                .addGroup(layout.createSequentialGroup()
                        .addGap(0, 82, Short.MAX_VALUE)
                        .addComponent(CancelButton)
                        .addContainerGap()
                )
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(PresetLabel,GroupLayout.Alignment.CENTER)
                    	.addComponent(PresetBox,25,25,25)
                    	.addComponent(PresetSaveButton)
                    	.addComponent(PresetLoadButton)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(SimulationTypeLabel)
                    .addGroup(layout.createParallelGroup()
                		.addComponent(SimulationTypeOption1)
                		.addComponent(SimulationTypeOption2)
                		.addComponent(SimulationTypeOption3)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup()
                    		.addComponent(ManualInputButton)
                    		.addComponent(SimHelpButton)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)     
                    .addComponent(VerifyButton)  
                    .addContainerGap()
                )
            )
        );
    }

}
