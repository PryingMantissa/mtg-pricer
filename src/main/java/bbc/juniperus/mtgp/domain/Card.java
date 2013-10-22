package bbc.juniperus.mtgp.domain;

import java.io.Serializable;

public class Card implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public Card(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
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
	
	
}
