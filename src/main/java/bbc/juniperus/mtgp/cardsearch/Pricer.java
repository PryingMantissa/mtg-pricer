package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbc.juniperus.mtgp.data.SearchData;
import bbc.juniperus.mtgp.data.viewmodel.ReportCreator;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;


public class Pricer{

	private Map<ProgressListener,Searcher> listeners = new HashMap<ProgressListener,Searcher>();
	private List<Searcher> searchers = new ArrayList<Searcher>();
	private SearchData data = new SearchData();
	//private List<Card> cards = new ArrayList<Card>();
	
	public SearchData data(){
		return data;
	}
	
	public int getCardListSize(){
		return data.cards().size();
	}
	
	public void addCard(Card card, int quantity){
		int q = data.getCardQuantity(card);
		
		//If already present. Increase the quantity.
		//Remove the card from data-store and add it again with new quantity.
		//Add it not to the card list of {@link Pricer} as its already there.
		if (q > 0){
			quantity += q;
			data.setCardQuantity(card, quantity);
			return;
		}
		data.addCard(card, quantity);
	}
	
	public boolean containsCard(Card card){
		return data.cards().contains(card);
	}
	
	public void removeCards(Collection<Card> cards){
		data.removeCards(cards);
	}
	
	public void setSearchers(Collection<Searcher> searchers){
		List<Source> sources = new ArrayList<Source>();
		for (Searcher s : searchers){
			this.searchers.add(s);
			sources.add(new Source(s.getName()));
		}
		data.addSources(sources);
	}
	
	public Collection<Searcher> getSearchers(){
		return searchers;
	}
	

	public void runLookUp(){
		for (Searcher s : searchers){
			new Thread(new Executor(s)).start();
		}
	}
	
	
	
	private void harvestResults(Searcher searcher) throws IOException {
		
		Source source = new Source(searcher.getName());
		HarvestData hData = new HarvestData();
		hData.currency = searcher.getCurrency();
		//harvestData.put(searcher, hData);
		//System.out.println("Starting harvesting with " + searcher);
		//Search for all cards using the Searcher.
		long timeStart = System.currentTimeMillis();
		for (Card card : data.cards()){
			CardResult result = null;
			
			fireCardSearchStarted(card, searcher);
			result = searcher.findCheapestCard(card.getName());
			if (result == null){
				result = CardResult.createNotFoundCardResult();
				hData.addNotFound(card);
			}
			else
				hData.totalPrice += result.getPrice();
			data.addResult(card, result, source);
			//System.out.println("For " + card + " found " + result);
			fireCardSearchEnded(result, searcher);
		}
		
		hData.setHarvestTime(System.currentTimeMillis() - timeStart);
		fireHarvestingEnded(searcher, hData);
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
					pl.startedSearchingFor(card,searcher);
	}
	
	public void fireCardSearchEnded(CardResult result, Searcher searcher){
		for (ProgressListener pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(searcher))
				pl.finishedSearchingFor(result,searcher);
	}
	
	public void fireHarvestingEnded(Searcher searcher, HarvestData data){
		for (ProgressListener pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(searcher))
				pl.finishedSearch(searcher, data);
	}
	
	
	//======================================================================
	
	private void errorOccured(Searcher searcher, Throwable t){
		System.out.println("Error during search with " + searcher + ": " + t.getMessage());
	}
	
	
	public static class HarvestData{
		long harvestTime;
		final List<Card> notFound = new ArrayList<Card>();
		private Currency currency;
		double totalPrice;
		
		
		public String getTotalPriceString(){
			String price =  String.format("%1$,.2f", totalPrice);
			price += " " + currency.getCurrencyCode();
			return price;
		}
		public int getNotFoundCount(){
			return notFound.size();
		}
		
		private void addNotFound(Card card) {
			notFound.add(card);
		}

		private void setHarvestTime(long time) {
			harvestTime = time;
		}
		
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
