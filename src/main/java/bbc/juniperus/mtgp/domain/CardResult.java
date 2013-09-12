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
	private Date date;
	private Currency currency;
	
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
