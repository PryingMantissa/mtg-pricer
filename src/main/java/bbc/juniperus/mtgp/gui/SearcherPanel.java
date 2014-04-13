package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;

public class SearcherPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public SearcherPanel(SearchExecutor pricer, CardFinder searcher){
		setLayout(new MigLayout());
		JLabel lbl = new JLabel(searcher.getName());
		add(lbl, BorderLayout.NORTH);
		add(new StatusRow(pricer,searcher), BorderLayout.CENTER);
	}
}
