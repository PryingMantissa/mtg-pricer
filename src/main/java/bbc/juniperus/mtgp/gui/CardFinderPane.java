package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;

public class CardFinderPane extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public CardFinderPane(SearchExecutor pricer, CardFinderPane searcher){
		setLayout(new MigLayout());
		JLabel lbl = new JLabel(searcher.getName());
		add(lbl, BorderLayout.NORTH);
		add(new SearchThreadView(pricer,searcher), BorderLayout.CENTER);
	}
}
