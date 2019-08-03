package GUI;

import javax.swing.*;
import GUIListeners.DISetAlgebraGUIInputListener;

/* Class for handling GUI generation of the DI-Set Algebra tab's GUI Input screen. 
 * GUI events are handled by a static instance of DISetAlgebraGUIInputListener. 
 * Please see that class for info. */
@SuppressWarnings("serial")
public class DISetAlgebraGUIInputScreen extends JPanel {

	public static DISetAlgebraGUIInputScreen instance = new DISetAlgebraGUIInputScreen();

    public JLabel ModulesTitleLabel = new JLabel("Modules");
    public JLabel SelectModuleLabel = new JLabel("Select Module");
    public DefaultListModel<String> SelectModulesListModel = new DefaultListModel<String>();
    public JList<String> SelectModulesList = new JList<String>(SelectModulesListModel);
    public JScrollPane SelectModulesPane = new JScrollPane(SelectModulesList);
    public JButton AddModuleButton = new JButton("Add Module");
    public JButton RemoveModuleButton = new JButton("Remove Module");
    public JLabel ChosenModulesLabel = new JLabel("Chosen Modules");
    public DefaultListModel<String> ChosenModulesListModel = new DefaultListModel<String>();
    public JList<String> ChosenModulesList = new JList<String>(ChosenModulesListModel);
    public JScrollPane ChosenModulesPane = new JScrollPane(ChosenModulesList);
    
    public JSeparator DividingLine1 = new JSeparator();
    
    public JLabel WireConnectionsTitleLabel = new JLabel("Wire connections");
    public JLabel SourceModuleLabel = new JLabel("Source Module");
    public DefaultListModel<String> SourceModuleListModel = new DefaultListModel<String>();
    public JList<String> SourceModuleList = new JList<String>(SourceModuleListModel);
    public JScrollPane SourceModulePane = new JScrollPane(SourceModuleList);
    public JLabel SourceOutputLabel = new JLabel("Source Output");	
    public DefaultListModel<String> SourceOutputListModel = new DefaultListModel<String>();
    public JList<String> SourceOutputList = new JList<String>(SourceOutputListModel);
    public JScrollPane SourceOutputPane = new JScrollPane(SourceOutputList);
    public JButton HideOutputButton = new JButton("Hide");
    
    public JButton ConnectButton = new JButton("Connect");

    public JLabel TargetModuleLabel = new JLabel("Target Module");
    public DefaultListModel<String> TargetModuleListModel = new DefaultListModel<String>();
	public JList<String> TargetModuleList = new JList<String>(TargetModuleListModel);
	public JScrollPane TargetModulePane = new JScrollPane(TargetModuleList);
	public JLabel TargetInputLabel = new JLabel("Target Input");
    public DefaultListModel<String> TargetInputListModel = new DefaultListModel<String>();
    public JList<String> TargetInputList = new JList<String>(TargetInputListModel);
    public JScrollPane TargetInputPane = new JScrollPane(TargetInputList);
    public JButton HideInputButton = new JButton("Hide");
    
    public JSeparator DividingLine2 = new JSeparator();
    
    public JLabel BuildTitleLabel = new JLabel("Build");
    public JLabel WireFunctionLabel = new JLabel("Wire function");
    public DefaultListModel<String> WireFunctionListModel = new DefaultListModel<String>();
	public JList<String> WireFunctionList = new JList<String>(WireFunctionListModel);
	public JScrollPane WireFunctionPane = new JScrollPane(WireFunctionList);
	public JButton RemoveConnectionButton = new JButton("Remove Connection");
    public JButton PlaceSignalButton = new JButton("Place Signal");
    public JLabel BusContentsLabel = new JLabel("Bus contents");
    public DefaultListModel<String> BusContentsListModel = new DefaultListModel<String>();
	public JList<String> BusContentsList = new JList<String>(BusContentsListModel);
	public JScrollPane BusContentsPane = new JScrollPane(BusContentsList);
	public JButton RemoveSignalButton = new JButton("Remove Signal");
	
    public JLabel HiddenLabel = new JLabel("Hidden ports");
    public DefaultListModel<String> HiddenListModel = new DefaultListModel<String>();
	public JList<String> HiddenList = new JList<String>(HiddenListModel);
	public JScrollPane HiddenPane = new JScrollPane(HiddenList);
	public JButton RemoveHidingButton = new JButton("Remove Hiding");
    
	public JLabel FinaliseLabel = new JLabel("Finalise");
	public JButton BuildNetworkButton = new JButton("Build Network");
	public JButton CancelButton = new JButton("Cancel");
    
    public String[][][] builtInModuleDefinitions = {
    		{{"sATS"},{"R","T"},{"T0","T1"},
    			{"({T},{T1}).sATS","({R,T},{T0}).sATS"}},
    		
    		{{"sATSm1"},{"T0","T1"},{"R","T"},
    			{"({T1},{T}).sATSm1","({T0},{R,T}).sATSm1"}},
    		
    		{{"mATS1"},{"R","T"},{"T0","T1"},
        			{"({T},{T1}).mATS1","({R,T},{T0}).mATS1"}},
        		
        	{{"mATS0"},{"R","T"},{"T0","T1"},
        			{"({T},{T0}).mATS1"}},
    		
    		{{"fATS1"},{"R","T"},{"T0","T1"},
    			{"({T},{T1}).fATS1","({R,T},{T0}).fATS1","({R,T},{T1}).fATS0"}},
    		
    		{{"fATS0"},{"R","T"},{"T0","T1"},
    			{"({T},{T0}).fATS1"}},
    		
    		{{"F"},{"a"},{"b","c"},
    			{"({a},{b,c}).F"}},
    		
    		{{"J"},{"a","b"},{"c"},
    			{"({a,b},{c}).J"}},
    		
    		{{"M"},{"a","b"},{"c"},
    			{"({a},{c}).M","({b},{c}).M"}},
    		
    		{{"Select0"},{"R","S","T"},{"R'","S'","T1","T0"},
            			{"({T},{T0}).Select0","({S},{S'}).Select1","({R},{R'}).Select0"}},
    		
    		{{"Select1"},{"R","S","T"},{"R'","S'","T1","T0"},
        			{"({T},{T1}).Select1","({S},{S'}).Select1","({R},{R'}).Select0"}},
    		
    		{{"C"},{"c"},{"a","b"},
    			{"({c},{a}).C","({c},{b}).C"}},
    		
    		{{"Mem0"},{"c","t"},{"c'","t0","t1"},
        			{"({c},{c'}).Mem1","({t},{t0}).Mem0"}},
    		
    		{{"Mem1"},{"c","t"},{"c'","t0","t1"},
            			{"({c},{c'}).Mem0","({t},{t1}).Mem1"}},
        		
    		{{"RT0"},{"R","W"},{"W0","W1"},
    			{"({W},{W0}).RT1"}},
    		
    		{{"RT1"},{"R","W"},{"W0","W1"},
    			{"({R},{W1}).RT1","({W},{W1}).RT0"}},
    		
    		{{"IRT0"},{"W0","W1"},{"R","W"},
    			{"({W1},{W}).IRT1"}},
    		
    		{{"IRT1"},{"W0","W1"},{"R","W"},
    			{"({W1},{R}).IRT1","({W0},{W}).IRT0"}},
    			
    		{{"REv"},{"n","s","w","e"},{"n'","s'","w'","e'"},
    			{"({n},{s'}).REv","({s},{n'}).REv","({w},{s'}).REh","({e},{n'}).REh"}},
    		
    		{{"REh"},{"n","s","w","e"},{"n'","s'","w'","e'"},
			{"({n},{w'}).REv","({s},{e'}).REv","({w},{e'}).REh","({e},{w'}).REh"}},
    		
    		{{"RDM0"},{"q","p","r"},{"0","a","s"},
    			{"({q},{q0}).RDM0","({p},{s}).RDMa","({r},{s}).RDM1"}},
    		
    		{{"RDM1"},{"q","p","r"},{"0","a","s"},
    			{"({q},{s}).RDMb","({p},{q0}).RDM1","({r},{s}).RDM0"}},
    		
    		{{"RDMa"},{"q","p","r"},{"0","a","s"},
    			{"({r},{a}).RDM1"}},
    		
    		{{"RDMb"},{"q","p","r"},{"0","a","s"},
    			{"({r},{a}).RDM0"}},
    		
    		{{"DM0"},{"q","p","r","c"},{"0","1","a","s"},
    			{"({q},{q0}).DM0","({p},{q1}).DM0","({r},{s}).DM1","({c},{s}).DMa"}},
    		
    		{{"DM1"},{"q","p","r","c"},{"0","1","a","s"},
    			{"({q},{q1}).DM0","({p},{q0}).DM0","({r},{s}).DM0","({c},{s}).DMb"}},
    		
    		{{"DMa"},{"q","p","r","c"},{"0","1","a","s"},
    			{"({r},{a}).DM1"}},
    		
    		{{"DMb"},{"q","p","r","c"},{"0","1","a","s"},
    			{"({r},{a}).DM0"}},
    };
    
    public DISetAlgebraGUIInputScreen() {
        WireConnectionsTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 16));
        ModulesTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 16));        
        BuildTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 16));               
        for(int i=0;i<builtInModuleDefinitions.length;i++){
        	SelectModulesListModel.addElement(builtInModuleDefinitions[i][0][0]);
        }        
        HideOutputButton.setActionCommand("Hide1");
        HideInputButton.setActionCommand("Hide2");
       
        WireConnectionsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        SelectModuleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ChosenModulesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ModulesTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        SourceModuleLabel.setHorizontalAlignment(SwingConstants.CENTER);        
        BuildTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        TargetModuleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        WireFunctionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        BusContentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        HiddenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        FinaliseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        SourceOutputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        TargetInputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        DividingLine1.setBackground(new java.awt.Color(0, 0, 0));
        DividingLine1.setOrientation(SwingConstants.VERTICAL);
        DividingLine2.setBackground(new java.awt.Color(0, 0, 0));
        DividingLine2.setOrientation(SwingConstants.VERTICAL);

        AddModuleButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        RemoveModuleButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        ConnectButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        RemoveConnectionButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        PlaceSignalButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        RemoveSignalButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        BuildNetworkButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        CancelButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        HideOutputButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        HideInputButton.addActionListener(DISetAlgebraGUIInputListener.instance);
        RemoveHidingButton.addActionListener(DISetAlgebraGUIInputListener.instance);

        SelectModulesList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        ChosenModulesList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        SourceModuleList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        SourceOutputList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        TargetModuleList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        TargetInputList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        WireFunctionList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        BusContentsList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        HiddenList.addListSelectionListener(DISetAlgebraGUIInputListener.instance);
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup()
                .addComponent(ModulesTitleLabel,GroupLayout.Alignment.CENTER)
                .addComponent(SelectModuleLabel,GroupLayout.Alignment.CENTER)
                .addComponent(SelectModulesPane,0,800,Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(AddModuleButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(RemoveModuleButton)
                )
                .addComponent(ChosenModulesLabel,GroupLayout.Alignment.CENTER)
                .addComponent(ChosenModulesPane,0,800,Short.MAX_VALUE)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(DividingLine1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(WireConnectionsTitleLabel,GroupLayout.Alignment.CENTER)
                .addComponent(ConnectButton,GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(SourceModuleLabel,GroupLayout.Alignment.CENTER)
                        .addComponent(SourceModulePane,0,800,Short.MAX_VALUE)
                        .addComponent(SourceOutputLabel,GroupLayout.Alignment.CENTER)
                        .addComponent(SourceOutputPane,0,800,Short.MAX_VALUE)
                        .addComponent(HideOutputButton,GroupLayout.Alignment.TRAILING)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(TargetModuleLabel,GroupLayout.Alignment.CENTER)
                        .addComponent(TargetModulePane,0,800,Short.MAX_VALUE)
                        .addComponent(TargetInputLabel,GroupLayout.Alignment.CENTER)
                        .addComponent(TargetInputPane,0,800,Short.MAX_VALUE)
                        .addComponent(HideInputButton)
                    )
                )
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(DividingLine2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(BuildTitleLabel, GroupLayout.Alignment.CENTER)
                .addComponent(WireFunctionLabel, GroupLayout.Alignment.CENTER)
                .addComponent(WireFunctionPane,0,800,Short.MAX_VALUE)                
                .addGroup(layout.createSequentialGroup()
                    .addComponent(RemoveConnectionButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(PlaceSignalButton)
                )
                .addComponent(BusContentsLabel,GroupLayout.Alignment.CENTER)
                .addComponent(BusContentsPane,0,800,Short.MAX_VALUE)
                .addComponent(RemoveSignalButton)
                .addComponent(HiddenLabel,GroupLayout.Alignment.CENTER)
                .addComponent(HiddenPane,0,800,Short.MAX_VALUE)
                .addComponent(RemoveHidingButton)
                .addComponent(FinaliseLabel,GroupLayout.Alignment.CENTER)                
                .addGroup(GroupLayout.Alignment.CENTER,layout.createSequentialGroup()
					    .addComponent(BuildNetworkButton)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(CancelButton)
                )
            )
            .addContainerGap()
        );
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(DividingLine1)
            .addComponent(DividingLine2)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ModulesTitleLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SelectModuleLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SelectModulesPane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(AddModuleButton)
                    .addComponent(RemoveModuleButton)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChosenModulesLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChosenModulesPane)
                .addContainerGap()
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(WireConnectionsTitleLabel)        
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)        
                .addGroup(layout.createParallelGroup()
                        .addComponent(SourceModuleLabel)
                        .addComponent(TargetModuleLabel)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(SourceModulePane)
                    .addComponent(TargetModulePane)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ConnectButton)
                .addGroup(layout.createParallelGroup()
                    .addComponent(SourceOutputLabel)
                    .addComponent(TargetInputLabel)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(SourceOutputPane)
                    .addComponent(TargetInputPane)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(HideOutputButton)
                    .addComponent(HideInputButton)
                )
                .addContainerGap()
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(BuildTitleLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WireFunctionLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WireFunctionPane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(RemoveConnectionButton)
                    .addComponent(PlaceSignalButton)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BusContentsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BusContentsPane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RemoveSignalButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HiddenLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HiddenPane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RemoveHidingButton)   
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FinaliseLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup()
                    .addComponent(BuildNetworkButton)
                    .addComponent(CancelButton)
                )
                .addContainerGap()
            )
        );
    }
}
