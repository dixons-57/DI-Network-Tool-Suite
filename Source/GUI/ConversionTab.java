package GUI;

import java.awt.Font;
import javax.swing.*;
import GUIListeners.ConversionTabListener;

/* Class for handling GUI generation of the Conversion tab. GUI events are 
 * handled by a static instance of ConversionTabListener. Please see that 
 * class for info. */
@SuppressWarnings("serial")
public class ConversionTab extends JPanel {

	public static ConversionTab instance = new ConversionTab();
	
	public JLabel PresetSeqLabel = new JLabel("Sequential machine presets");
	public JComboBox<String> SeqModulePresetBox = new JComboBox<String>();
	public JButton SeqModulePresetLoadButton = new JButton("Load");
	public JButton SeqModulePresetSaveButton = new JButton("Save");
	
	public JLabel PresetSetLabel = new JLabel("Set Notation presets");
    public JComboBox<String> SetNotationPresetBox = new JComboBox<String>();
    public JButton SetNotationPresetLoadButton = new JButton("Load");
    public JButton SetNotationPresetSaveButton = new JButton("Save");

    public JLabel SeqLabel = new JLabel("Sequential machine module definition");
    public JTextArea SeqTextArea = new JTextArea();
    public JScrollPane SeqTextAreaPane = new JScrollPane(SeqTextArea);
    public JLabel AFuncLabel = new JLabel("A function");
    public JTextArea AFuncTextArea = new JTextArea();
    public JScrollPane AFuncTextAreaPane = new JScrollPane(AFuncTextArea);
    public JLabel StateRemovalLabel =new JLabel("Remove states unreachable from top-most");
    public JCheckBox StateRemoval = new JCheckBox();
    
    public JButton EditSeqButton = new JButton("Edit Definition");
    public JButton SeqHelpButton = new JButton("?");
    public JButton ConvertToSetButton = new JButton("Convert to Set Notation");

    public JLabel SetLabel = new JLabel("Set Notation module definition");
    public JTextArea SetTextArea = new JTextArea();
    public JScrollPane SetTextAreaPane = new JScrollPane(SetTextArea);
    
    public JButton EditSetButton = new JButton("Edit Definition");
    public JButton SetHelpButton = new JButton("?");
    public JLabel SetStatusLabel = new JLabel("Status: N/A");
    public JButton ConvertToSeqButton = new JButton("Convert to (ND) Sequential Machine");
    
    public ConversionTab() {

    	SetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        SeqLabel.setHorizontalAlignment(SwingConstants.CENTER);
        AFuncLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Font font = new Font("Verdana", Font.BOLD, 16);
    	SeqTextArea.setFont(font);
    	AFuncTextArea.setFont(font);
    	SetTextArea.setFont(font);
        
    	SeqModulePresetLoadButton.setActionCommand("Load1");
    	SeqModulePresetSaveButton.setActionCommand("Save1");
    	SeqModulePresetLoadButton.addActionListener(ConversionTabListener.instance);
    	SeqModulePresetSaveButton.addActionListener(ConversionTabListener.instance);
    	
    	SetNotationPresetLoadButton.addActionListener(ConversionTabListener.instance);
    	SetNotationPresetSaveButton.addActionListener(ConversionTabListener.instance);
        
        EditSeqButton.setActionCommand("EditSeq");
        EditSeqButton.addActionListener(ConversionTabListener.instance);
        SeqHelpButton.setActionCommand("SeqHelp");
        SeqHelpButton.addActionListener(ConversionTabListener.instance);
        
        ConvertToSetButton.addActionListener(ConversionTabListener.instance);
        
        EditSetButton.setActionCommand("EditSet");
        EditSetButton.addActionListener(ConversionTabListener.instance);
        SetHelpButton.setActionCommand("SetHelp");
        SetHelpButton.addActionListener(ConversionTabListener.instance);
        
        ConvertToSeqButton.addActionListener(ConversionTabListener.instance);
        
        ConvertToSeqButton.setEnabled(false);
        ConvertToSetButton.setEnabled(false);
        SeqTextArea.setEditable(false);
        AFuncTextArea.setEditable(false);
        SetTextArea.setEditable(false);
        StateRemoval.setSelected(true);
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PresetSeqLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SeqModulePresetBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SeqModulePresetLoadButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SeqModulePresetSaveButton)
                    )
                    .addComponent(SeqLabel,GroupLayout.Alignment.CENTER)
                    .addComponent(SeqTextAreaPane,0,500,Short.MAX_VALUE)
                    .addComponent(AFuncLabel,GroupLayout.Alignment.CENTER)
                    .addComponent(AFuncTextAreaPane)
                    .addComponent(EditSeqButton)
        			.addComponent(SeqHelpButton,GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ConvertToSetButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StateRemovalLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StateRemoval)
                    )
                )
				.addGap(12)
                .addGroup(layout.createParallelGroup()
                     .addGroup(layout.createSequentialGroup()
                         .addComponent(PresetSetLabel)
                         .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                         .addComponent(SetNotationPresetBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                         .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                         .addComponent(SetNotationPresetLoadButton)
                         .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                         .addComponent(SetNotationPresetSaveButton)
                    )
                    .addComponent(SetLabel,GroupLayout.Alignment.CENTER)
                    .addComponent(SetTextAreaPane,0,500,Short.MAX_VALUE)
                    .addComponent(ConvertToSeqButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EditSetButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SetStatusLabel)
                    )
        			.addComponent(SetHelpButton,GroupLayout.Alignment.TRAILING)
                )
                .addContainerGap()
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(PresetSeqLabel,GroupLayout.Alignment.CENTER)
                .addComponent(SeqModulePresetBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(SeqModulePresetLoadButton)
                .addComponent(SeqModulePresetSaveButton)
                .addComponent(PresetSetLabel,GroupLayout.Alignment.CENTER)
                .addComponent(SetNotationPresetBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(SetNotationPresetLoadButton)
                .addComponent(SetNotationPresetSaveButton)
            )
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(SetLabel)
                .addComponent(SeqLabel)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(SeqTextAreaPane,100,100,Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(AFuncLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(AFuncTextAreaPane,100,100,Short.MAX_VALUE)
                )
                .addComponent(SetTextAreaPane)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(EditSeqButton)
                .addComponent(SeqHelpButton)
                .addComponent(SetHelpButton)
                .addComponent(EditSetButton)
                .addComponent(SetStatusLabel,GroupLayout.Alignment.CENTER)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(ConvertToSetButton)
                .addComponent(StateRemovalLabel,GroupLayout.Alignment.CENTER)
                .addComponent(StateRemoval,GroupLayout.Alignment.CENTER)
                .addComponent(ConvertToSeqButton)
            )
            .addContainerGap()
        );
    }                                 
}
