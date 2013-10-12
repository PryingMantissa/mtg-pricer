package bbc.juniperus.mtgp.gui.test;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class LayoutTest {
	
	
	public static void main(String[] args){
		
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MigLayout m =new MigLayout();
		JPanel pane = new JPanel(m);
		
		
		//pane.add(lab("kokotewewew"), "west");
		pane.add(lab("Picura"));

		
		
		
		
		frame.add(pane);
		frame.setVisible(true);
		
	}
	
	
	static private JLabel lab(String text){
		JLabel lbl = new JLabel(text);
		lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		return lbl;
	}
}
