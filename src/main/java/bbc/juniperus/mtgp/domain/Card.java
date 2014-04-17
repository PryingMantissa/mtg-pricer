package bbc.juniperus.mtgp.domain;

public class Card{
	
	private String name;
	
	public Card(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Card))
			return false;
		Card c = (Card) o;
		return c.name.equals(name);
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "[ name: " + name +  "]";
	}
}
