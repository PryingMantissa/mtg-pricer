package bbc.juniperus.mtgp.domain;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;

public class CardResult implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private String edition;
	private double price;
	private String notFoundMsg;
	private Source source;
	@SuppressWarnings("unused")
	private Date date;
	private Currency currency;
	private boolean found;
	
	private CardResult(){}
	
	public CardResult(String name, String type, String edition,
						double price, Source source, Date date, Currency currency){
		if (name != null)
			this.name = name.replaceAll("[`´]", "'");
		this.type = type;
		this.edition = edition;
		this.price = price;
		this.source = source;
		this.date = date;
		this.currency = currency;
		this.found = true;
	}
	
	public static CardResult createNotFoundCardResult(){
		CardResult cr =  new CardResult();
		cr.found = false;
		
		String na = "N/A";
		
		cr.name = na;
		cr.price = -1;
		cr.edition = na;
		cr.type = na;
		return cr;
	}

	
	public boolean found(){
		return found;
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
	
	public Source getSource(){
		return source;
	}
	
	

	@Override
	public String toString(){
		
		if (notFoundMsg != null)
			return "N/A: " + notFoundMsg;
		
		return this.getClass().getSimpleName() + "[@" + source + ": " + name + ", " + type +", " + 
				", " + edition  +", " + price + "]";
	}

}
