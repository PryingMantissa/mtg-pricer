package bbc.juniperus.mtgp.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;

public class ResultsPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pricer pricer;
	private List<StatusRow> rows = new ArrayList<StatusRow>();
	private int maxFirstColW;
	
	public ResultsPanel(Pricer pricer){
		this.pricer = pricer;
		setLayout(new GridLayout(0,1));
		setBackground(Color.WHITE);
	}
	
	
	public void addFeedBackRow(Searcher searcher){
		StatusRow row  = new StatusRow(pricer,searcher);
		
		if (row.getNameLabelWidth() > maxFirstColW){
			maxFirstColW = row.getNameLabelWidth();
			for (StatusRow s : rows)
				s.setNameLabelWidth(maxFirstColW);
		}
		else
			row.setNameLabelWidth(maxFirstColW);
		
		
		rows.add(row);
		add(row);
	}
	

}
