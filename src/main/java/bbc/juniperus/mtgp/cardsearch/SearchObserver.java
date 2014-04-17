package bbc.juniperus.mtgp.cardsearch;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

/**
 * An observer for events related to card search. <p>
 * 
 * <b>NOTE:</b> If used with {@link SearchExecutor}, the methods are <b>WILL NOT</b> invoked on the event dispatch thread!.
 *
 *@see {@link SearchExecutor}
 *@see {@link CardFinder}
 */
public interface SearchObserver {
	
	
	/**
	 * Invoked when the search has started. 
	 * @param numberOfCards number of cards in the search
	 */
	void searchStarted(int numberOfCards);
	
	/**
	 * Invoked when the card finder thread started searching for a card.
	 * @param card the card for which the finder has started searching
	 * @param finder the card finder involved
	 */
	void startedSearchingForCard(Card card, CardFinder finder);
	
	/**
	 * Invoked when card finder thread has finished searched for a card.
	 * @param card car for which the finder finished searching
	 * @param result result of the card search
	 * @param finder card finder involved
	 */
	void finishedSearchingForCard(Card card, CardResult result, CardFinder finder);
	
	/**
	 * Invoked when a card finder search thread successfully ended.
	 * @param finder card finder involved
	 */
	void searchThreadFinished(CardFinder finder);
	
	/**
	 * Invoked when a card finder search thread encountered an error and has been forced to stop.
	 * @param finder card finder involved
	 * @param t cause
	 */
	void searchThreadFailed(CardFinder finder, Throwable t);

	/**
	 * Invoked when the whole search has completed.
	 * @param interrupted <code>true</code> if the search was interrupted by the user
	 */
	void searchingFinished(boolean interrupted);
	
}
