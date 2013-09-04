package bbc.juniperus.mtgp;

public class Card {
	
	private String name;
	private String type;
	private String edition;
	private int price;
	
	
	public Card(String name, String type, String edition, int price){
		this.name = name;
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


	public int getPrice() {
		return price;
	}
	
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + "[" + name + ", " + type +", " + 
				", " + edition  +", " + price + "]";
	}
	
	
}
