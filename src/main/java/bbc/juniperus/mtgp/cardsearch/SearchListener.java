package bbc.juniperus.mtgp.cardsearch;

import bbc.juniperus.mtgp.cardsearch.finder.CardFinder;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;


/**
 *	Listener for events related to card search.
 */
public interface SearchListener {
	
	/**
	 * Informs that the the search with a given card infder  currently started trying to find the result for a given card 
	 * @param card the card for which the finder has started currently searching
	 * @param finder card finder involved
	 */
	void startedSearchingFor(Card card, CardFinder finder);
	
	/**
	 * Informs that the the search for a card has finished and is about to start searching for another one
	 * (if it was not the final card).
	 * @param result result from the given card search
	 * @param finder card finder involved
	 */
	void finishedSearchingFor(CardResult result, CardFinder finder);
	
	/**
	 * Informs that the search has successfully ended.
	 * @param finder card finder involved
	 * @param data search related data
	 */
	void finishedSearch(CardFinder finder, HarvestData data);
	
	/**
	 * Informs that the search encountered an error and has been forced to stop.
	 * @param finder card finder involved
	 * @param t cause
	 */
	void failedSearch(CardFinder finder, Throwable t);

	/**
	 * Invoked when the pricing process has completed.
	 * @param interrupted <code>true</code> if interrupted by the user
	 */
	void pricingEnded(boolean interrupted);
	
}
