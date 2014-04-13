package bbc.juniperus.mtgp.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the group of {@link CardResult} from for a given {@link Card}. 
 * In other words: it is {@link Source} - <code>{@link CardResult}</code> map.
 *
 */
public class CardResultGroup {
	
	private Map<String,CardResult> cardResultsMap = new HashMap<String,CardResult>();
	
	public CardResultGroup(){
		
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
	
}
