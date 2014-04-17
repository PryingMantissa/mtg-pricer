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
	
	public CardFindersPane(Controller controller){
		setLayout(new MigLayout());
		this.controller = controller;
		//JLabel lbl = new JLabel(searcher.getName());
		//add(lbl, BorderLayout.NORTH);
		//add(new SearchThreadView(pricer,searcher), BorderLayout.CENTER);
	}
	
	
	public void showFinderSettings(){
		removeAll();
		add(new JLabel("<html><b>Card pricing sources:</b></html>"), "wrap");
		
		CheckBoxListener listener = new CheckBoxListener();
		checkBoxMap.clear();
		
		for (CardFinder finder : controller.getCardFinders()){
			JCheckBox checkBox = new JCheckBox(finder.getName());
			checkBox.addActionListener(listener);
			checkBoxMap.put(checkBox, finder);
			add(checkBox, "wrap");
		}
		
	}
	
	public void showSearchProgress(SearchExecutor searchExecutor){
		removeAll();
		repaint();
		add(new JLabel("<html><b>Search progress:</b></html>"), "wrap");
		
		for (CardFinder finder : searchExecutor.getCardFinders()){
			ThreadSearchProgressView view = new ThreadSearchProgressView(finder);
			searchExecutor.addSearchObserver(view);
			add(view, "wrap");
		}
		revalidate();
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
