package bbc.juniperus.mtgp.domain;

public class Card {
	
	private String name;
	private String type;
	private String edition;
	private double price;
	private String notFoundMsg;
	
	public Card(String name, String type, String edition, int price){
	
		this.name = name.replaceAll("[`´]", "'");
		this.type = type;
		this.edition = edition;
		this.price = price;
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
	
	
	public void setNotFound(String msg){
		notFoundMsg = msg;
	}
	
	@Override
	public String toString(){
		
		if (notFoundMsg != null)
			return "N/A: " + notFoundMsg;
		
		return this.getClass().getSimpleName() + "[" + name + ", " + type +", " + 
				", " + edition  +", " + price + "]";
	}
	
	
}
