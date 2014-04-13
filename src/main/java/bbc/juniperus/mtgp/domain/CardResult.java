package bbc.juniperus.mtgp.domain;

import java.util.Currency;

import javax.xml.transform.Source;

/**
 * Represents a result of search for a particular {@link Card} from one {@link Source}. Contains
 * all information about the card which were available from the search results.
 */

public class CardResult{
	
	private String name;
	private String type;
	private String edition;
	private double price;
	private String notFoundMsg;
	private Currency currency;

	/** Null object for no result*/
	public static final CardResult NULL_CARD_RESULT = createNullCardResult();
	
	private CardResult(){}
	
	public CardResult(String name, String type, String edition,
						double price,Currency currency){
		if (name != null)
			this.name = name.replaceAll("[`�]", "'");
		this.type = type;
		this.edition = edition;
		this.price = price;
		this.currency = currency;
	}
	
	private static CardResult createNullCardResult(){
		CardResult cr =  new CardResult();
		
		String na = "N/A";
		
		cr.name = na;
		cr.price = -1;
		cr.edition = na;
		cr.type = na;
		return cr;
	}


	public String getName() {
		return name;
	}


	public String getType() {
		return type;
	}


	public String getEdition() {
		return edition;
	}

	public double getPrice() {
		return price;
	}
	
	public Currency getCurrency(){
		return currency;
	}
	
	
	@Override
	public String toString(){
		
		if (notFoundMsg != null)
			return "N/A: " + notFoundMsg;
		
		return this.getClass().getSimpleName() + "[ " + name + ", " + type +", " + 
				", " + edition  +", " + price + "]";
	}

}
