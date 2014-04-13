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
	
	private final Map<Card,Integer>  cards = new HashMap<>();
	private final List<CardFinder> finders = new ArrayList<>();
	
	
	public void addCard(Card card, int quantity){
		if (cards.get(card) != null)
			throw new IllegalArgumentException("The card " + card + "is already in the collection");
		if (card == null)
			throw new NullPointerException();
		if (quantity < 1)
			throw new IllegalArgumentException("The quantity needs to be at least 1");
			
		cards.put(card, quantity);
		
	}
	
	public int getQuantity(Card card){
		return cards.get(card);
	}
	
	public void setNewQuantity(Card card, int newQuantity){
		if (!cards.keySet().contains(card))
			throw new IllegalArgumentException("No such card in collection");
		if (newQuantity < 1)
			throw new IllegalArgumentException("The quantity needs to be at least 1");
		cards.put(card, newQuantity);
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
