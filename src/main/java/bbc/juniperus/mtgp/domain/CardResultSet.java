package bbc.juniperus.mtgp.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CardResultSet {
	private Card card;
	private Map<String,CardResult> cardResultsMap = new HashMap<String,CardResult>();
	
	public CardResultSet(Card card){
		this.card = card;
	}
	
	public void addCardResult(String sourceName, CardResult cardResult){
		cardResultsMap.put(sourceName, cardResult);
	}
	
	public CardResult getCardResultFor(String sourceName){
		return cardResultsMap.get(sourceName);
	}
	
	public Set<String> sourceNames(){
		return Collections.unmodifiableSet(cardResultsMap.keySet());
	}
	
	public Card card(){
		return card;
	}
}
