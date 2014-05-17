package bbc.juniperus.mtgp.gui;


import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.cardsearch.CardSearchResults;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

@SuppressWarnings("serial")
public class SearchThreadProgressView extends JPanel implements SearchObserver{

	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel resultsLabel = new JLabel();
	private final CardFinder finder;
	private final static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##");
	
	public SearchThreadProgressView(CardFinder finder){
		super (new BorderLayout());
		this.finder = finder;
		JLabel label = new JLabel();
		label.setText(finder.getName());
		add(label,BorderLayout.NORTH);
		add(progressBar);
		add(resultsLabel, BorderLayout.SOUTH);
		
		Font f  = label.getFont().deriveFont(Font.BOLD);
		label.setFont(f);
		//cardNameLabel.setText("No serch...");	
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
				resultsLabel.setText(text);
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
		
		//setLabelText(card.getName());
		progressBar.setStringPainted(true);
		progressBar.setString(card.getName());
		
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
	public void searchThreadFinished(CardFinder finder, CardSearchResults results) {
		if (this.finder == finder){
			
			int all = results.getCardResults().size();
			int found = all - results.getNotFoundCards().size();

			long time = results.getSearchTime();
			
			double totalPrice = 0;
			
			for (CardResult cardResult : results.getCardResults().values())
				if (cardResult != CardResult.NULL_CARD_RESULT)
					totalPrice += cardResult.getPrice();
			
			
			
			String  text = "Found " + found + "/" + all  + " cards <br/>" + 
			"Pricing time: "+ formatTime(time) + "<br/>" +
			"Total price: " + DOUBLE_FORMAT.format(totalPrice) + " " + finder.getCurrency().getCurrencyCode(); 
			
			text = "<html><body style='padding-left:10px;'>"+  text + "</body></html>";
			progressBar.setVisible(false);
			setLabelText(text);
		}
	}
	
	private static String formatTime(long time){
		int timeSeconds = (int) (time /1000); //Convert to seconds
		int seconds = timeSeconds % 60;
		timeSeconds =- seconds;
		int minutes = timeSeconds/60;

		String result;
		
		if (minutes == 0)
			result = seconds  + " s";
		else
			result = minutes + " min " + seconds + " s";
		
		return result;
		
	}

	@Override
	public void searchThreadFailed(CardFinder finder, Throwable t) {
		setLabelText("Failed");
	}

	@Override
	public void searchingFinished(boolean interrupted) {
		// No implementation
	}


}
