package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;

public class SearcherPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private Searcher searcher;
	
	public SearcherPanel(Pricer pricer, Searcher searcher){
		this.searcher = searcher;
		setLayout(new MigLayout());
		JLabel lbl = new JLabel(searcher.getName());
		add(lbl, BorderLayout.NORTH);
		add(new StatusRow(pricer,searcher), BorderLayout.CENTER);
	}
}
