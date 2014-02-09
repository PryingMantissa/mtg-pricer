package bbc.juniperus.mtgp.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

/**
 * The central storage of all card related app data - card to be search and quantity and
 * the results returned from the search. The data is stored in 'rows' while each 'row' is mapped to a certain
 * card and contains all card information.
 *
 */
public class DataStorage {

	/**
	 * Result type.
	 */
	public static enum Result {CARD_NAME, PRICE,TYPE, EDITION, CURRENCY};
	private Map<Card,CardData> cardData = new LinkedHashMap<Card,CardData>();
	private Set<Source> sources = new LinkedHashSet<Source>();
	private Set<DataChangeListener> listeners = new HashSet<DataChangeListener>();
	private boolean mutable = true;
	
	
	/**
	 * Returns collection of all cards.
	 * @return all cards 
	 */
	public Collection<Card> cards(){
		return Collections.unmodifiableSet(cardData.keySet());
	}
	
	/**
	 * Adds the card to the storage.
	 * @param card card to be added
	 * @param quantity card quantity
	 */
	public void addCard(Card card, int   quantity){
		if (quantity <1)
			throw new IllegalArgumentException("Quantity must be at least 1");
		
		//Create for each card data object.
		CardData csd = new CardData();
		csd.setQuantity(quantity);
		cardData.put(card,csd);
		fireCardAdded(card);
	}
	
	/**
	 * Removes all cards passed as parameter from the storage.
	 * @param cards cards to be removed.
	 */
	public void removeCards(Collection<Card> cards){
		for (Card c : cards)
			cardData.remove(c);
		fireCardsRemoved(cards);
	}
	
	
	/**
	 * Gets all registered {@link Source} objects in this storage.
	 * @return
	 */
	public Set<Source> getSources(){
		return Collections.unmodifiableSet(sources);
	}

	
	/**
	 * Adds the result data for a a given card & source to the storage. 
	 * @param card card object
	 * @param result result data
	 * @param source source object
	 */
	public synchronized void  addResult(Card card, CardResult result, Source source){
		//System.out.println("adding result " + card.getName() + "  " +result);
		cardData.get(card).addResult(source, result);
		fireResultAdded();
		
		if (!sources.contains(source)){
			throw new IllegalArgumentException("No such source is registred by this instance: " + source);
		}
	}
	
	/**
	 * Determines if the write operations are permitted on this storage.
	 * @return <code>true</code> if read-write operations permitted, <code>false</code> if read-only
	 */
	public boolean isReadWrite(){
		return mutable;
	}
	
	/**
	 * Sets the storage to be read-write or read-only.
	 * @param b <code>true</code> if modifying of data is permitted, <code>false</code> if not
	 */
	public void setReadWrite(boolean b){
		mutable = b;
	}
	
	/**
	 * Registers new {@link DataChangeListener} for this storage.
	 * @param listener listener to be registered
	 */
	public void addDataChangeListener(DataChangeListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Notifies all listeners that results has been added.
	 */
	private void fireResultAdded(){
		for (DataChangeListener l :listeners)
			l.resultAdded();
	}
	
	/**
	 * Notifies all listeners that {@link Source} objects has been added.
	 * @param sources all listeners which have been added.
	 */
	private void fireSourcesAdded(Collection<Source> sources){
		for (DataChangeListener l :listeners)
			l.sourcesAdded(sources);
	}
	/**
	 * Notifies all listneners that card has been added to storage.
	 * @param c card which was added.
	 */
	private void fireCardAdded(Card c){
		for (DataChangeListener l :listeners)
			l.cardAdded(c);
	}
	
	/**
	 * Notifies all listeners that data in a row for a card have been changed.
	 * @param c card which row has been changed
	 */
	private void fireRowChanged(Card c){
		for (DataChangeListener l :listeners)
			l.rowChanged(c);
	}
	
	/**
	 * Notifies all listeners that card(s) have been removed from the storage.
	 * @param cards
	 */
	private void fireCardsRemoved(Collection<Card> cards){
		for (DataChangeListener l :listeners)
			l.cardsRemoved(cards);
	}
	
	/**
	 * Returns the row count in storage.
	 * @return row count 
	 */
	public int getRowsCount(){
		return cardData.size();
	}
	
	/**
	 * Returns quantity for a card in a storage.
	 * @param card card
	 * @return card quantity or -1 if no card is in the storage
	 */
	public int getCardQuantity(Card card){
		
		CardData row = cardData.get(card);
		//No such card present.
		if (row == null)
			return  -1;
		return row.getQuantity();
	}
	
	/**
	 * Sets the quantity for the card.
	 * @param card
	 * @param quantity
	 */
	public void setCardQuantity(Card card, int quantity){
		CardData row = cardData.get(card);
		
		if (row == null)
			throw new IllegalArgumentException("Card is not present in storage.");
		
		row.setQuantity(quantity);
		fireRowChanged(card);
	}
	
	
	/**
	 * Replaces a card in the storage with a new card
	 * @param oldCard card to be replaced
	 * @param newCard new card to be put in place instead of old card
	 */
	public void replaceCard(Card oldCard, Card newCard){
		CardData o = cardData.get(oldCard);
		cardData.remove(oldCard);
		cardData.put(newCard, o);
	}
	
	
	/**
	 * Gets representation of a card result of a given type for a given source
	 * as a string.
	 * @param card card
	 * @param source source
	 * @param type result type
	 * @return string representation of the result
	 */
	public String getStrResult(Card card, Source source, Result type){
		CardData row = cardData.get(card);
		CardResult result = row.getResult(source);
		
		//TODO change?
		if (result == null)
			return "";
		
		if (type == Result.CARD_NAME)
			return result.getName();
		else if (type == Result.PRICE){
			double d  = result.getPrice();
			if (d < 0)
				return "N/A";
			return Double.toString(d);
		}
		else if (type == Result.EDITION)
			return result.getEdition();
		else if (type == Result.TYPE)
			return result.getType();
		else if (type == Result.EDITION)
			return result.getEdition();
		else if (type == Result.CURRENCY)
			return result.getCurrency().getCurrencyCode();
		
		throw new RuntimeException("We were not supposed to get here");
	}

	/**
	 * Add sources to this storage.
	 * @param sources sources to be added.
	 */
	public void addSources(Collection<Source> sources) {
		List<Source> list = new ArrayList<Source>();
		for (Source s : sources)
			if (this.sources.add(s))
				list.add(s);
		System.out.println("Sources added " + sources);
		fireSourcesAdded(list);
	}
}

