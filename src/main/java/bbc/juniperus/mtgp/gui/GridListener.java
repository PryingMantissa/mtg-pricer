package bbc.juniperus.mtgp.gui;

public interface GridListener {
	
	void gridFocusLost();
	
	void gridFocusGained();
	
	void gridSelectionChanged(int[] selectedRows);
	
}
