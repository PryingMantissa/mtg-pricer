package bbc.juniperus.mtgp.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.domain.Card;

public class PricingSettings {
	
	private final Map<Card,Integer>  cardQuantityMap = new HashMap<>();
	private final List<Card> cardList = new ArrayList<>(); //For keeping track of insertion orde
	private final List<Card> roCardList = Collections.unmodifiableList(cardList);
	private final List<CardFinder> finders = new ArrayList<>();
	
	
	public void addCard(Card card, int quantity){
		if (cardQuantityMap.get(card) != null)
			throw new IllegalArgumentException("The card " + card + "is already in the collection");
		if (card == null)
			throw new NullPointerException();
		if (quantity < 1)
			throw new IllegalArgumentException("The quantity needs to be at least 1");
			
		cardQuantityMap.put(card, quantity);
		cardList.add(card);
	}
	
	/**
	 * Returns unmodifiable <code>List</code> with cards which are part of these settings.
	 * @return <code>List</code> with set cards
	 */
	public List<Card> getCards(){
		return roCardList;
	}
	
	public int getQuantity(Card card){
		return cardQuantityMap.get(card);
	}
	
	public void setNewQuantity(Card card, int newQuantity){
		if (!cardQuantityMap.keySet().contains(card))
			throw new IllegalArgumentException("No such card in collection");
		if (newQuantity < 1)
			throw new IllegalArgumentException("The quantity needs to be at least 1");
		cardQuantityMap.put(card, newQuantity);
	}
	
	public void addFinder(CardFinder finder){
		if (finders.contains(finder))
			throw new IllegalArgumentException("The finder has been already added");
		finders.add(finder);
	}
	
	public Collection<CardFinder> getFinders(){
		return Collections.unmodifiableCollection(finders);
	}
}
