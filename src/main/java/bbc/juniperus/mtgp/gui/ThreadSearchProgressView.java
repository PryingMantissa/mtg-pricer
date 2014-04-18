package bbc.juniperus.mtgp.gui;


import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.cardsearch.SearchResultsData;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

@SuppressWarnings("serial")
public class ThreadSearchProgressView extends JPanel implements SearchObserver{

	
	private JProgressBar progressBar = new JProgressBar();
	private JLabel cardNameLabel = new JLabel();
	private CardFinder finder;
//	private JLabel lblFoundNumber;
//	private JLabel lblPrice;
//	private JLabel lblName;
//	
	Border brdRight = BorderFactory.createEmptyBorder(0, 0, 0, 30);
	Border brdLeft = BorderFactory.createEmptyBorder(0, 30, 0, 0);
	
	private int t = 5;
	Border border = BorderFactory.createEmptyBorder(t, t, t, t);
	
	public ThreadSearchProgressView(CardFinder finder){
		super (new BorderLayout());
		this.finder = finder;
		
		add(progressBar);
		add(cardNameLabel, BorderLayout.SOUTH);
		cardNameLabel.setText("No serch...");	
		/*
		//setBackground(Color.green);
		//setBorder(border);
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
		add(progressBar,"width 150:150:150");*/
		
	}

	/**
	 * Helper method to set the test of the label on the event dispatch thread
	 */
	private void setLabelText(final String text){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				cardNameLabel.setText(text);
			}
		});
	}
	
	
	@Override
	public void searchStarted(final int numberOfCards) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setMaximum(numberOfCards);
			}
		});
	}
	
	@Override
	public void cardSearchStarted(Card card, CardFinder finder) {
		if (finder != this.finder)
			return;
		
		setLabelText(card.getName());
		
	}

	@Override
	public void cardSearchFinished(Card card, CardResult result,
			CardFinder finder) {
		if (finder != this.finder)
			return;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setValue(progressBar.getValue() +1);
			}
		});
		
	}

	@Override
	public void searchThreadFinished(CardFinder finder) {
		setLabelText("Completed");
	}

	@Override
	public void searchThreadFailed(CardFinder finder, Throwable t) {
		setLabelText("Finished");
	}

	@Override
	public void searchingFinished(boolean interrupted) {
		// No implementation
	}


}
