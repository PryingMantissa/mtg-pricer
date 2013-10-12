package bbc.juniperus.mtgp.datastruc;

import java.util.HashMap;
import java.util.Map;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

public class CardRow {
	
	private Card card;
	private int quantity;
	private Map<Source,CardResult> results = new HashMap<Source,CardResult>();
	
	public CardRow(Card card, int quantity){
		this.card = card;
		this.quantity = quantity;
	}
	
	public void addResult(Source source, CardResult result){
		results.put(source, result);
	}
	
	public CardResult getResult(Source src){
		return results.get(src);
	}
	
	public Card getCard(){
		return card;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
}
