package bbc.juniperus.mtgp.gui;


import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardSearchResults;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

/**
 * A view component which shows the current progress of the search
 * for a given search thread/card finder based on the events
 * received from a search executor.
 */
@SuppressWarnings("serial")
public class SearchThreadProgressView extends JPanel implements SearchObserver{

	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel resultsLabel = new JLabel();
	private final CardFinder finder;
	private final static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##");

	/**
	 * Construct a search thread progress view for a given card finder.
	 * @param finder the card finder for which this view is constructed
	 */
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
	}

	/**
	 * Sets the results label text on the EDT.
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
	
	/**
	 * Formats a long value into a formatted string.
	 * @param time the value in the millis to be converted to string
	 * @return a formatted date
	 */
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
