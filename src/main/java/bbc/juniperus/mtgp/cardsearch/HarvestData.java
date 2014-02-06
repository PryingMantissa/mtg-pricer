package bbc.juniperus.mtgp.cardsearch;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import bbc.juniperus.mtgp.domain.Card;

/**
 * Collection of search related data which are used
 * after the search has ended.
 *
 */
public class HarvestData {
	long harvestTime;
	final List<Card> notFound = new ArrayList<Card>();
	private Currency currency;
	double totalPrice;
	
	/**
	 * Search currency
	 * @return currency of the search
	 */
	public Currency getCurrency(){
		return currency;
	}
	
	/**
	 * Sets the currency of the search
	 * @param currency currency to be set
	 */
	public void setCurrency(Currency currency){
		this.currency = currency;
	}
	
	/**
	 * Total price in form of formatted string
	 * @return string with formatted price
	 */
	public String getTotalPriceString(){
		String price =  String.format("%1$,.2f", totalPrice);
		price += " " + currency.getCurrencyCode();
		return price;
	}
	/**
	 * Gets number of cards which were not found in the search.
	 * @return count of cards not found
	 */
	public int getNotFoundCount(){
		return notFound.size();
	}
	
	/**
	 * Adds card to the list of cards which were not found.
	 * @param card card which was not found
	 */
	public void addNotFound(Card card) {
		notFound.add(card);
	}

	/**
	 * Returns time representing how much search took.
	 * @param time
	 */
	public void setHarvestTime(long time) {
		harvestTime = time;
	}
}
