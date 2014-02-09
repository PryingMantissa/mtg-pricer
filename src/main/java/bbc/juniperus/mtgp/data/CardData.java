package bbc.juniperus.mtgp.data;

import java.util.HashMap;
import java.util.Map;

import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

/**
 * Data related to card which is part of the search. Containing 
 * the quantity of the card and found results.
 */
public class CardData {
	
	private int quantity;
	private Map<Source,CardResult> results = new HashMap<Source,CardResult>();
	
	/**
	 * Add search result.
	 * @param source source of the search result
	 * @param result the search result
	 */
	public void addResult(Source source, CardResult result){
		results.put(source, result);
	}
	
	/**
	 * Gets search result for a given source.
	 * @param src source for which the result should be retrieved
	 * @return search result
	 */
	public CardResult getResult(Source src){
		return results.get(src);
	}
	
	/**
	 * Sets the quantity of the card. Is irrelevant for the search
	 * but necessary when computing total prices of the card deck.
	 * @param quantity
	 */
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	
	
	/**
	 * Returns the quantity of the card in card deck/list.
	 * @return quantity of the card
	 */
	public int getQuantity(){
		return quantity;
	}
	
}
