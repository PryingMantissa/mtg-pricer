package bbc.juniperus.mtgp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;

@SuppressWarnings("serial")
public class CardFindersPane extends JPanel{
	
	private Map<JCheckBox, CardFinder> checkBoxMap = new HashMap<>();
	private Controller controller;
	private JLabel statusLabel;
	
	public CardFindersPane(Controller controller){
		setLayout(new MigLayout());
		this.controller = controller;
		//JLabel lbl = new JLabel(searcher.getName());
		//add(lbl, BorderLayout.NORTH);
		//add(new SearchThreadView(pricer,searcher), BorderLayout.CENTER);
		statusLabel = new JLabel();
	}
	
	
	public void showFinderSettings(){
		removeAll();
		statusLabel.setText("<html><b>Card pricing sources</b></html>");
		add(statusLabel,"wrap");
		
		CheckBoxListener listener = new CheckBoxListener();
		checkBoxMap.clear();
		
		for (CardFinder finder : controller.getCardFinders()){
			JCheckBox checkBox = new JCheckBox(finder.getName());
			
			if (controller.getPricingSettings().getFinders().contains(finder)) //Set selected it its part of the settings
				checkBox.setSelected(true);
			
			checkBox.addActionListener(listener);
			checkBoxMap.put(checkBox, finder);
			add(checkBox, "wrap");
		}
		
	}
	
	public void showSearchProgress(SearchExecutor searchExecutor){
		removeAll();
		statusLabel.setText("<html><b>Search progress</b></html>");
		add(statusLabel,"wrap");
		
		for (CardFinder finder : searchExecutor.getCardFinders()){
			ThreadSearchProgressView view = new ThreadSearchProgressView(finder);
			searchExecutor.addSearchObserver(view);
			add(view, "wrap");
		}
		revalidate();
	}
	
	public void displayStoppingSearch(){
		statusLabel.setText("<html><b>Stopping the search...</b></html>");
	}
	
	public void displaySearchStopped(){
		statusLabel.setText("<html><b>Search stopped by user</b></html>");
	}
	
	public void displaySearchFinished(){
		statusLabel.setText("<html><b>Search succesfully finished</b></html>");
	}
	
	
	private class CheckBoxListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			
			System.out.println(checkBox.isSelected());
			controller.setFinderEnabled(checkBoxMap.get(checkBox), checkBox.isSelected());
		}
		
	}
	
	
}
