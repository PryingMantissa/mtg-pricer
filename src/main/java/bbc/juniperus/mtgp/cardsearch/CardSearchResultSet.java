package bbc.juniperus.mtgp.cardsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

/**
 * A container for search results for specific card finder with read-only public interface.
 * Once created, it stays same for a duration of a search (and afterwards). So the intermediate
 * results can be read from it while new results are being added. <p>
 * 
 * Methods are thread safe - getters and setters are synchronized.
 * 
 */
public class CardSearchResultSet {
	
	private long searchTime;
	private final List<Card> notFound = new ArrayList<>();
	private final List<Card> notFoundRO = Collections.unmodifiableList(notFound);
	private final CardFinder finder;
	private final Map<Card, CardResult> results;
	private final Map<Card, CardResult> resultsView;
	
	public CardSearchResultSet(CardFinder finder){
		this.finder = finder;  
		results = new HashMap<>();
		resultsView = Collections.unmodifiableMap(results);
	}
	
	public synchronized CardResult getCardResult(Card card){
		return results.get(card);
	}
	
	/**
	 * Returns unmodifiable  card - card result map.
	 * @return card - result map
	 */
	public synchronized Map<Card,CardResult> getCardResults(){
		return resultsView;
	}
	
	public CardFinder getFinder(){
		return finder;
	}

	
	/**
	 * Gets the cards which were not found in the search.
	 * @return list of cards not found
	 */
	public synchronized List<Card> getNotFoundCards(){
		return notFoundRO;
	}
	
	/**
	 * Returns the length of the search in milliseconds.
	 * @return
	 */
	public synchronized long getSearchTime(){
		return searchTime;
	}
	

	synchronized void addCardResult(Card card, CardResult result){
		results.put(card, result);
	}
	
	/**
	 * Adds card to the list of cards which were not found.
	 * @param card card which was not found
	 */
	synchronized void addNotFound(Card card) {
		notFound.add(card);
		results.put(card, CardResult.NULL_CARD_RESULT);
	}
	
	/**
	 * Sets the length of the search  in milliseconds.
	 * @param time
	 */
	synchronized void setSearchTime(long time) {
		searchTime = time;
	}
	
	
	
}
