package bbc.juniperus.mtgp.data;

import java.util.Collection;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.Source;

/**
 * Listener for the changes in the {@link DataStore instance}.
 *
 */
public interface DataChangeListener {
	
	/**
	 * Informs that a new result has been added.
	 */
	void resultAdded();
	
	/**
	 * Informs that a new source has been added.
	 * @param sources added source
	 */
	void sourcesAdded(Collection<Source> sources);
	
	/**
	 * Informs that a card has been added.
	 * @param card
	 */
	void cardAdded(Card card);
	
	/**
	 * Informs that a row with the given card has changed.
	 * @param card
	 */
	void rowChanged(Card card);
	
	/**
	 * Informs that card have been removed.
	 * @param cards removed cards
	 */
	void cardsRemoved(Collection<Card> cards);
}
