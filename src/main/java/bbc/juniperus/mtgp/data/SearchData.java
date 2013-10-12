package bbc.juniperus.mtgp.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

public class SearchData {

	public static enum Result {CARD_NAME, PRICE,TYPE, EDITION, CURRENCY};
	private Map<Card,CardSearchData> cardData = new LinkedHashMap<Card,CardSearchData>();
	private Set<Source> sources = new LinkedHashSet<Source>();
	private Set<DataChangeListener> listeners = new HashSet<DataChangeListener>();
	
	public Collection<Card> cards(){
		return Collections.unmodifiableSet(cardData.keySet());
	}
	
	public void addCard(Card card, int quantity){
		if (quantity <1)
			throw new IllegalArgumentException("Quantity must be at least 1");
		
		//Create for each card data object.
		CardSearchData csd = new CardSearchData();
		csd.setQuantity(quantity);
		cardData.put(card,csd);
		fireCardAdded(card);
	}
	
	public Set<Source> getSources(){
		return Collections.unmodifiableSet(sources);
	}

	
	public synchronized void  addResult(Card card, CardResult result, Source source){
		//System.out.println("adding result " + card.getName() + "  " +result);
		cardData.get(card).addResult(source, result);
		fireResultAdded();
		
		if (!sources.contains(source)){
			//System.out.println("adding source");
			sources.add(source);
			fireSourceAdded(source);
		}
	}
	
	
	public void addDataChangeListener(DataChangeListener listener){
		listeners.add(listener);
	}
	
	private void fireResultAdded(){
		for (DataChangeListener l :listeners)
			l.resultAdded();
	}
	
	private void fireSourceAdded(Source s){
		for (DataChangeListener l :listeners)
			l.sourceAdded(s);
	}
	
	private void fireCardAdded(Card c){
		for (DataChangeListener l :listeners)
			l.cardAdded(c);
	}
	
	public int getRowsCount(){
		return cardData.size();
	}
	
	public int getCardQuantity(Card card){
		CardSearchData row = cardData.get(card);
		return row.getQuantity();
	}
	
	public String getStrResult(Card card, Source source, Result type){
		CardSearchData row = cardData.get(card);
		CardResult result = row.getResult(source);
		
		//TODO change?
		if (result == null)
			return "";
		
		if (type == Result.CARD_NAME)
			return result.getName();
		else if (type == Result.PRICE){
			return Double.toString(result.getPrice());
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
}

