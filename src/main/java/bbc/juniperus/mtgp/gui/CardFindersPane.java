package bbc.juniperus.mtgp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.data.PricingSettings;

@SuppressWarnings("serial")
public class CardFindersPane extends JPanel{
	
	private Map<JCheckBox, CardFinder> checkBoxMap = new HashMap<>();
	
	public CardFindersPane(){
		setLayout(new MigLayout());
		//JLabel lbl = new JLabel(searcher.getName());
		//add(lbl, BorderLayout.NORTH);
		//add(new SearchThreadView(pricer,searcher), BorderLayout.CENTER);
	}
	
	
	public void setPreSearchOptions(Collection<CardFinder> finders, 
			PricingSettings settings){
		
		removeAll();
		add(new JLabel("<html><b>Card pricin sources:</b></html>"), "wrap");
		
		CheckBoxListener listener = new CheckBoxListener();
		checkBoxMap.clear();
		
		for (CardFinder finder : finders){
			JCheckBox checkBox = new JCheckBox(finder.getName());
			checkBox.addActionListener(listener);
			checkBoxMap.put(checkBox, finder);
			add(checkBox, "wrap");
		}
		
	}
	
	
	private class CheckBoxListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
	
	
}
