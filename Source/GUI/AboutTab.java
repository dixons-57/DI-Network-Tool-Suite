package GUI;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/* Class for generating the About information tab, which simply displays authorship info about the software */
@SuppressWarnings("serial")
public class AboutTab extends JPanel {

	static AboutTab instance = new AboutTab();
	
	JTextPane AboutMessagePane = new JTextPane();
	
	public AboutTab(){
		AboutMessagePane.setEditable(false);
		AboutMessagePane.setFont((new java.awt.Font("Dialog", 1, 18)));
		AboutMessagePane.setBackground(this.getBackground());
		
		StyledDocument documentSyle =  AboutMessagePane.getStyledDocument();
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
		documentSyle.setParagraphAttributes(0, documentSyle.getLength(), attributes, false);
		
		this.setLayout(new BorderLayout());
		this.add(AboutMessagePane,BorderLayout.CENTER);
		
		AboutMessagePane.setText("Delay-Insensitive Network Tool Suite by Daniel Morrison\n\n"
				+ "This software is free to be redistributed for academic use. It has been produced "
				+ "in support of a degree in Doctor of Philosophy at the University of Leicester, 2016.\n\n"
				+ "For further information please see the README and relevant chapter of the doctoral thesis.");	

	}
}
