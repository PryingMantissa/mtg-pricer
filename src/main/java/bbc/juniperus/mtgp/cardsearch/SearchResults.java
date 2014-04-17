package bbc.juniperus.mtgp.cardsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

/**
 * Search related statistics.
 */
public class SearchResults {
	
	private long searchTime;
	private final List<Card> notFound;
	private final CardFinder finder;
	private final Map<Card, CardResult> results;
	
	public SearchResults(CardFinder finder){
		this.finder = finder;  
		notFound = new ArrayList<Card>();
		results = new HashMap<>();
	}
	
	/** Copy constructor*/
	private SearchResults(SearchResults oldInstance){
		finder = oldInstance.finder;  
		searchTime = oldInstance.searchTime;
		notFound = new ArrayList<>(oldInstance.notFound);
		results = new HashMap<>(oldInstance.results);
	}
	
	
	public CardFinder getFinder(){
		return finder;
	}
	
	/**
	 * Gets the cards which were not found in the search.
	 * @return list of cards not found
	 */
	public List<Card> getNotFoundCards(){
		return notFound;
	}
	
	/**
	 * Adds card to the list of cards which were not found.
	 * @param card card which was not found
	 */
	public void addNotFound(Card card) {
		notFound.add(card);
	}

	
	public void addCardResult(Card card, CardResult result){
		results.put(card, result);
	}
	
	
	public CardResult getCardResult(Card card){
		return results.get(card);
	}
	
	
	/**
	 * Sets the length of the search  in milliseconds.
	 * @param time
	 */
	void setSearchTime(long time) {
		searchTime = time;
	}
	
	/**
	 * Returns the length of the search in milliseconds.
	 * @return
	 */
	public long getSearchTime(){
		return searchTime;
	}
	
	/**
	 * Alternative method to {@link #clone()} with the same functionality.
	 * @return
	 */
	public SearchResults makeClone(){
		return new SearchResults(this);
	}
	
	
	
}
