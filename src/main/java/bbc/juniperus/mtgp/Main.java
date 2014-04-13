package bbc.juniperus.mtgp;

import javax.swing.UnsupportedLookAndFeelException;

import bbc.juniperus.mtgp.gui.Controller;
import bbc.juniperus.mtgp.gui.MainView;

public class Main {

	public Main() {
		setLookAndFeel();
		Controller controller = new Controller();
		MainView view = new MainView(controller);
		
	}
	
	
	public static void main(String[] args){
		new Main();
	}
	
	private void setLookAndFeel(){
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
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
