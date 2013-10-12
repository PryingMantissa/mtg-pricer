package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbc.juniperus.mtgp.data.SearchData;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;


public class Pricer{

	private Map<ProgressListener,Searcher> listeners = new HashMap<ProgressListener,Searcher>();
	private List<Searcher> searchers = new ArrayList<Searcher>();
	private SearchData data = new SearchData();
	private List<Card> cards = new ArrayList<Card>();

	
	public SearchData data(){
		return data;
	}
	
	public int getCardListSize() {
		return cards.size();
	}

	public void addCards(Map<Card,Integer> map){
		for (Card c : map.keySet()){
			cards.add(c); //Save to own List as well.
			data.addCard(c, map.get(c));
		}
	}
	
	public void addSearcher(Searcher searcher){
		searchers.add(searcher);
	}
	
	
	public void runLookUp() throws IOException{
		for (Searcher s : searchers){
			new Thread(new Executor(s)).start();
		}
	}
	
	private void harvestResults(Searcher searcher) throws IOException {
		
		Source source = new Source(searcher.getName());
		//System.out.println("Starting harvesting with " + searcher);
		//Search for all cards using the Searcher.
		for (Card card : cards){
			CardResult result = null;
			
			fireCardSearchStarted(card, searcher);
			result = searcher.findCheapestCard(card.getName());
			data.addResult(card, result, source);
			
			//System.out.println("For " + card + " found " + result);
			fireCardSearchEnded(result, searcher);
		}
	}
	
	
	//================ Listeners related methods ========================
	
	public void addProgressListener(ProgressListener listener, Searcher searcher){
		
		//Add for all if not specified.
		if (searcher == null)
			for (Searcher s: searchers)
				listeners.put(listener, s);
		listeners.put(listener, searcher);
	}
	
	public void fireCardSearchStarted(Card card, Searcher searcher){
		for (ProgressListener pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(searcher))
					pl.cardSearchStarted(card,searcher);
	}
	
	public void fireCardSearchEnded(CardResult result, Searcher searcher){
		for (ProgressListener pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(searcher))
				pl.cardSearchEnded(result,searcher);
	}
	
	//======================================================================
	
	private void errorOccured(Searcher searcher, Throwable t){
		System.out.println("Error during search with " + searcher + ": " + t.getMessage());
	}
	
	private class Executor implements Runnable{
		
		Searcher searcher;
		Executor(Searcher s){
			searcher = s;
		}

		@Override
		public void run() {
			try {
				harvestResults(searcher);
			} catch (IOException e) {
				e.printStackTrace();
				Pricer.this.errorOccured(searcher,e);
			}
		}
		
	}
}
