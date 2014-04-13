package bbc.juniperus.mtgp.gui;


import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchResults;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public class SearchThreadView extends JPanel{

	
	private JProgressBar progressBar;
	private SearchExecutor pricer;
	private JLabel lblFoundNumber;
	private JLabel lblPrice;
	private JLabel lblName;
	
	Border brdRight = BorderFactory.createEmptyBorder(0, 0, 0, 30);
	Border brdLeft = BorderFactory.createEmptyBorder(0, 30, 0, 0);
	
	private int t =5;
	Border border = BorderFactory.createEmptyBorder(t, t, t, t);
	
	private static final long serialVersionUID = 1L;

	public SearchThreadView(SearchExecutor pricer, CardFinder searcher, int maxProgress){
		
		//setBackground(Color.green);
		setBorder(border);
		this.pricer = pricer;
		MigLayout m =new MigLayout("ins 0");
		setLayout(m);
	
		progressBar = new JProgressBar();

		lblPrice = new JLabel("Search will begin shortly");
		lblPrice.setFont(lblPrice.getFont().deriveFont((float)11.0));

		lblName = new JLabel(searcher.getName());
		lblName.setFont(lblName.getFont().deriveFont(Font.BOLD));
		lblFoundNumber = new JLabel();
		lblFoundNumber.setVisible(false);
		
		add(lblName,"wrap");
		add(lblFoundNumber,"wrap");
		add(lblPrice,"width 170:170:170, wrap");
		add(progressBar,"width 150:150:150");
		progressBar.setMaximum(maxProgress);
	}

	public void setText(String text){
		lblPrice.setText(text);
	}
	
	public void incrementProgress(){
		progressBar.setValue(progressBar.getValue() + 1);
	}
	
	public void setResults(SearchResults results){
		remove(progressBar);
		/*
		int notFound = data.getNotFoundCount();
		
		if (notFound == 0){
			lblFoundNumber.setForeground(new Color(0x009933));
			lblFoundNumber.setText("Found all cards");
		}else{
			lblFoundNumber.setForeground(Color.RED);
			lblFoundNumber.setText("Not found " +notFound + " card(s)");
		}
		
		lblFoundNumber.setVisible(true);
		lblPrice.setText("Total price: " + data.getTotalPriceString());
		*/
	}
}
