package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.PricingWorker;
import bbc.juniperus.mtgp.cardsearch.finder.CardFinder;

public class SearcherPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public SearcherPanel(PricingWorker pricer, CardFinder searcher){
		setLayout(new MigLayout());
		JLabel lbl = new JLabel(searcher.getName());
		add(lbl, BorderLayout.NORTH);
		add(new StatusRow(pricer,searcher), BorderLayout.CENTER);
	}
}
