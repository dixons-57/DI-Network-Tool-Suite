package GUI;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/* Class for generating the Console Output window. A static instance is created and then all
 * useful debug or algorithm info is passed to this window during run-time. */
@SuppressWarnings("serial")
public class ConsoleWindow extends JFrame {

	public static ConsoleWindow instance = new ConsoleWindow();
	
	private JTextArea TextArea = new JTextArea();
	private JScrollPane TextAreaPane = new JScrollPane(TextArea);
	private long currentOutputLine =0;
	
    public ConsoleWindow(){
    	super("Console Output");

    	TextArea.setEditable(false);
    	DefaultCaret caret = (DefaultCaret)TextArea.getCaret();
    	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    	
        GroupLayout layout = new GroupLayout(getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(TextAreaPane, GroupLayout.DEFAULT_SIZE, MainWindow.instance.getWidth()-16, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(TextAreaPane, GroupLayout.DEFAULT_SIZE, MainWindow.instance.getHeight()/4, Short.MAX_VALUE)
        );
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setMinimumSize(new Dimension(MainWindow.instance.minimumWidth,MainWindow.instance.minimumHeight/4));
        this.setVisible(true);
    }

    /* Adds the specified string to the console output, and creates a new line */
	public static void output(String out){
    	instance.TextArea.append(instance.currentOutputLine+": "+out+"\n");
    	instance.currentOutputLine++;
    }

}
