package bbc.juniperus.mtgp.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class DeckCard implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	private int quantity;
	private String name;
	/** Store the found cards for this DeckCard. The key is the name of the
	 * pricing web.
	 */
	private Map<String,CardResult> foundCards = new HashMap<String,CardResult>();
	
	public DeckCard(String name, int quantity){
		this.name = name;
		this.quantity = quantity;
	}

	
	public int getQuantity() {
		return quantity;
	}


	public String getName() {
		return name;
	}

	
	public void addFoundCard(String sourceName, CardResult card){
		foundCards.put(sourceName, card);
	}
	
	public CardResult getCardResult(String sourceName){
		return foundCards.get(sourceName);
	}
	

	@Override
	public String toString() {
		return "DeckCard [quantity=" + quantity + ", name=" + name + "]";
	}
	
	public String stringifyFoundCards(){
		
		StringBuilder sb = new StringBuilder();
		for (String name : foundCards.keySet())
			sb.append("@ ").append(name).append(":")
			.append(foundCards.get(name)).append("\n");
		
		return sb.toString();
	}
	
	
}
