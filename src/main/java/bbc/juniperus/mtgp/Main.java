package bbc.juniperus.mtgp;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import bbc.juniperus.mtgp.gui.Controller;
import bbc.juniperus.mtgp.gui.MainView;

public class Main {

	public Main() {
		setLookAndFeel();
		Controller controller = new Controller();
		controller.newPricing();
	}
	
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				new Main();
			}
		});
	}
	
	private void setLookAndFeel(){
		try {
			
			String lookAndFeel= javax.swing.UIManager.getSystemLookAndFeelClassName(); 
			
			System.out.println(lookAndFeel);
			if (lookAndFeel.endsWith("MetalLookAndFeel")) //This might be Linux so let's try GTK look and feel.
				lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
			
			javax.swing.UIManager.setLookAndFeel(lookAndFeel); 
			
			javax.swing.UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
