package GUI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

//import InputOutputOperations.ConversionPresets;
import InputOutputOperations.DISetAlgebraPresets;
import InputOutputOperations.SequentialMachinePresets;
import InputOutputOperations.SetNotationModulePresets;
import InputOutputOperations.SimulationPresets;

/* JFrame which represents the main window. It renders the main GUI and also
 * listens to resize and movement events (moving the console window appropriately). It adds the
 * four main tabs (Conversion, Construction, Environment Generation, DI-Set Algebra)
 * to this window (these tabs render themselves using their own classes), as well as the
 * Settings and About tabs. */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ComponentListener, FocusListener{

	static MainWindow instance;
        
    int minimumWidth = 957;
    int minimumHeight = 420;
	
	public MainWindow(){
		super("Delay-Insensitive Network Tool Suite");
		instance=this;
		
		JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Conversion", ConversionTab.instance);
		tabPane.addTab("Construction",ConstructionTab.instance);
		tabPane.addTab("Environment Generation",EnvironmentTab.instance);
		tabPane.addTab("DI-Set Algebra", DISetAlgebraTab.instance);
		tabPane.addTab("About",AboutTab.instance);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setPreferredSize(new Dimension((int)screenSize.getWidth()/2,(int)screenSize.getHeight()/2));
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		this.setContentPane(tabPane);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.addComponentListener(this);
		this.addFocusListener(this);
		this.pack();
		
		ConsoleWindow.output("Program initialised successfully - All GUI elements loaded");
		
		SequentialMachinePresets.loadFileList();
		SetNotationModulePresets.loadFileList();
		DISetAlgebraPresets.loadFileList();
		SimulationPresets.loadFileList();
		
		ConsoleWindow.output("Loaded list of presets");
		
		repositionConsole();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		repositionConsole();
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		repositionConsole();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		repositionConsole();
	}
	
	public void repositionConsole(){
		ConsoleWindow.instance.requestFocus();
		ConsoleWindow.instance.setLocation(getLocationOnScreen().x, getLocationOnScreen().y+getHeight()-8);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		repositionConsole();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}
}