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
		checkIfCardIsInCollection(card, false);
		if (quantity < 1)
			throw new IllegalArgumentException("The quantity needs to be at least 1");
			
		cardQuantityMap.put(card, quantity);
		cardList.add(card);
	}
	
	public void removeCard(Card card){
		checkIfCardIsInCollection(card, true);
		
		cardList.remove(card);
		cardQuantityMap.remove(card);
	}
	
	public void replaceCard(Card oldCard, Card newCard){
		checkIfCardIsInCollection(oldCard, true);
		checkIfCardIsInCollection(newCard, false);
		int index = cardList.indexOf(oldCard);
		int quantity = cardQuantityMap.get(oldCard);
		
		removeCard(oldCard);
		cardQuantityMap.put(newCard, quantity);
		cardList.add(index, newCard);
		
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
		checkIfCardIsInCollection(card, true);
		if (newQuantity < 1)
			throw new IllegalArgumentException("The quantity needs to be at least 1");
		cardQuantityMap.put(card, newQuantity);
	}
	
	public void addFinder(CardFinder finder){
		if (finders.contains(finder))
			throw new IllegalArgumentException("The finder has been already added");
		finders.add(finder);
	}
	
	public void removeFinder(CardFinder finder){
		if (!finders.contains(finder))
			throw new IllegalArgumentException("No such finder in the settings");
		finders.remove(finder);
	}
	
	
	public Collection<CardFinder> getFinders(){
		return Collections.unmodifiableCollection(finders);
	}
	
	private void checkIfCardIsInCollection(Card card, boolean shouldBePresent){
		if (card == null)
			throw new NullPointerException();
		
		boolean isPresent = cardQuantityMap.get(card) != null;
		
		if (shouldBePresent && !isPresent)
			throw new IllegalArgumentException("No such card ( " + card + ") in collection");
		else if (!shouldBePresent && isPresent)
			throw new IllegalArgumentException("The card " + card + ") is already in collection");
	}
	
}
