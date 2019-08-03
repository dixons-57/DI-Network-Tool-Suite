package GUI;

import java.awt.BorderLayout;
import javax.swing.*;
import GUIListeners.DISetAlgebraInteractiveExecutionListener;
import GUIListeners.DISetAlgebraLTSListener;
import GUIListeners.DISetAlgebraGUIInputListener;

/* Class for controlling behaviour of the DI-Set Algebra tab. 
 * This class basically hides and loads the correct screens within this tab 
 * (GUI Input, Interactive Execution, LTS, Main). These operations are called
 * by the various listener classes (GUIListeners package) relating to the above
 * screens */
@SuppressWarnings("serial")
public class DISetAlgebraTab extends JPanel {

	public static DISetAlgebraTab instance = new DISetAlgebraTab();
	
	private int currentScreen=1;
	
	public DISetAlgebraTab(){
		this.setLayout(new BorderLayout());
		this.add(DISetAlgebraMainScreen.instance,BorderLayout.CENTER);
	}
	
	public void showMainScreen(){
		removeShownScreen();
		this.add(DISetAlgebraMainScreen.instance,BorderLayout.CENTER);
		currentScreen=1;
		repaint();
	}
	
	public void showInputScreen(int network){
		removeShownScreen();
		DISetAlgebraGUIInputListener.instance.resetInputScreen(network);
		this.add(DISetAlgebraGUIInputScreen.instance,BorderLayout.CENTER);
		currentScreen=2;
		repaint();
	}
	
	public void showLTSScreen(){
		removeShownScreen();
		DISetAlgebraLTSListener.instance.resetLTSScreen();
		this.add(DISetAlgebraLTSScreen.instance,BorderLayout.CENTER);
		currentScreen=3;
		repaint();
	}
	
	public void showExecutionScreen(int network){
		removeShownScreen();
		DISetAlgebraInteractiveExecutionListener.instance.resetExecutionScreen(network);
		this.add(DISetAlgebraInteractiveExecutionScreen.instance,BorderLayout.CENTER);
		currentScreen=4;
		repaint();
	}

	private void removeShownScreen() {
		if(currentScreen==1){
			this.remove(DISetAlgebraMainScreen.instance);
		}
		else if(currentScreen==2){
			this.remove(DISetAlgebraGUIInputScreen.instance);
		}
		else if(currentScreen==3){
			this.remove(DISetAlgebraLTSScreen.instance);
		}
		else if(currentScreen==4){
			this.remove(DISetAlgebraInteractiveExecutionScreen.instance);
		}
	}
	
}                       
