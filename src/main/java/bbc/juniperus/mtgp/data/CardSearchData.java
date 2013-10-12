package bbc.juniperus.mtgp.data;

import java.util.HashMap;
import java.util.Map;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

public class CardSearchData {
	
	private int quantity;
	private Map<Source,CardResult> results = new HashMap<Source,CardResult>();
	
	public void addResult(Source source, CardResult result){
		results.put(source, result);
	}
	
	public CardResult getResult(Source src){
		return results.get(src);
	}
	
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
}
