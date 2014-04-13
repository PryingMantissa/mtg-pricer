package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import bbc.juniperus.mtgp.data.DataStorage;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

/**
 * Worker for executing card search (search for all cards in deck/link) for each set {@link CardFinder} and updates listeners
 * with the search progress. Search for each {@link CardPricer} is executed in separate thread.    
 */
public class Pricer2{

	private Map<SearchObserver,CardFinder> listeners = new HashMap<SearchObserver,CardFinder>();
	private List<CardFinder> searchers = new ArrayList<CardFinder>();
	private DataStorage data = new DataStorage();
	private boolean interruped;
	private boolean searchInProgress;
	private int harvestsLeft;
	
	/**
	 * Reference to underlying {@link DataStorage} where all results from the search
	 * are saved.
	 * @return
	 */
	public DataStorage data(){
		return data;
	}
	
	/**
	 * Gets the size of the card list (number of card entry) saved. 
	 * @return
	 */
	public int getCardListSize(){
		return data.cards().size();
	}
	
	/**
	 * Adds a new card to the search.
	 * @param card new card
	 * @param quantity quantity of the card
	 */
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
	
	/**
	 * Tests if a given {@link Card} is present among data.
	 * @param card card to test
	 * @return <code>true</code> if there is match, <code>false</code> if otherwise
	 */
	public boolean containsCard(Card card){
		return data.cards().contains(card);
	}
	
	/**
	 * Removes all provided cards.
	 * @param cards cards to be removed
	 */
	public void removeCards(Collection<Card> cards){
		data.removeCards(cards);
	}
	
	/**
	 * Sets {@link CardFinders} for which the search will be executed.
	 * @param finders the finders to be used in search
	 */
	public void setCardFinders(Collection<CardFinder> finders){
		List<Source> sources = new ArrayList<Source>();
		for (CardFinder s : finders){
			this.searchers.add(s);
			sources.add(new Source(s.getName()));
		}
		data.addSources(sources);
	}
	
	/**
	 * Gets lits of the {@link CardFinder} for which the search is set to run.
	 * @return list of {@link CardFinder} to be used in search
	 */
	public Collection<CardFinder> getCardFinders(){
		return searchers;
	}
	

	/**
	 * Stars the search. For each set {@link CardFiner} the search process is started
	 * in separate thread.
	 */
	public void startSearch(){
		searchInProgress = true;
		harvestsLeft = searchers.size();
		for (CardFinder s : searchers){
			new Thread(new SearchRunnable(s)).start();
		}
		//When the search has started no changes to the table are allowed.
		data.setReadWrite(false);
	}
	
	/**
	 * Sets the interrupt flag to <code>true</code> which stops
	 * the running search threads.
	 */
	public void interrupt(){
		interruped = true;
	}
	
	/**
	 * Determines if search is currently running.
	 * @return <code>true</code> if search is in progress, <code>false</code> if not
	 */
	public boolean isSearchInProgress(){
		return searchInProgress;
	}
	
	/**
	 * Determines if there are no cards loaded.
	 * @return <code>true</code> if there are no cards loaded, <code>false</code> otherwise
	 */
	public boolean hasNoCards(){
		return data.getRowsCount() == 0;
	}
	
	/**
	 * Contains code invoked by search thread (with {@link SearchRunnable}.
	 * @param finder card finder for the search
	 * @throws IOException
	 */
	private void harvestResults(CardFinder finder) throws IOException {
		
		Source source = new Source(finder.getName());
		SearchResults hData = new SearchResults();
		hData.setCurrency(finder.getCurrency());
		//Search for all cards using the Searcher.
		long timeStart = System.currentTimeMillis();
		for (Card card : data.cards()){
			if (interruped){
				fireHarvestingEnded(finder, hData);
				//If this is the last running thread.
				if (--harvestsLeft < 1){
					searchInProgress = false;
					firePricingFinished(true);
				}
				return;
			}
			CardResult result = null;
			
			fireCardSearchStarted(card, finder);
			result = finder.findCheapestCard(card.getName());
			if (result == null){
				result = CardResult.createNullCardResult();
				hData.addNotFound(card);
			}
			else
				hData.totalPrice += result.getPrice();
			data.addResult(card, result, source);
			System.out.println("For " + card + " found " + result);
			fireCardSearchEnded(result, finder);
		}
		
		hData.setSearchTime(System.currentTimeMillis() - timeStart);
		
		fireHarvestingEnded(finder, hData);
		if (--harvestsLeft < 1){
			searchInProgress = false;
			firePricingFinished(false);
		}
	}

	//================ Listeners related methods ========================
	/**
	 * Registers {@link SearchObserver} for a given card finder.
	 * @param listener listener for the search
	 * @param searcher type of card finder which the listener is interested in, <code>null</code> if it should be notified
	 * for events realted to all finders
	 */
	public void addProgressListener(SearchObserver listener, CardFinder finder){
		
		//Add for all if not specified.
		if (finder == null)
			for (CardFinder s: searchers)
				listeners.put(listener, s);
		listeners.put(listener, finder);
	}
	
	/**
	 * See {@link SearchObserver#startedSearchingForCard(Card, CardFinder)}
	 * @param card the card for which the searching has begun
	 * @param finder the finder which is involved
	 */
	private void fireCardSearchStarted(Card card, CardFinder finder){
		for (SearchObserver pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(finder))
					pl.startedSearchingForCard(card,finder);
	}
	
	/**
	 * See {@link SearchObserver#finishedSearchingFor(CardResult, CardFinder)}
	 * @param result the result from the finished searching for the card
	 * @param finder the finder which was involved
	 */
	private void fireCardSearchEnded(CardResult result, CardFinder finder){
		for (SearchObserver pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(finder))
				pl.finishedSearchingFor(result,finder);
	}
	
	/**
	 * Informs all {@link SearchObserver} that the search with a given card finder
	 * has successfully ended.
	 * @param finder finder involved
	 * @param data data related to the search
	 */
	private void fireHarvestingEnded(CardFinder finder, SearchResults data){
		for (SearchObserver pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(finder))
				pl.searchingFinished(finder, data);
	}
	
	/**
	 * Informs all registered {@link SearchObserver}s that the search with a given card finder
	 *  has encountered an (IO) error and ended. Is invoked on AWT dispatch thread.
	 * @param listener finder involved
	 * @param t cause
	 */
	private void fireHarvestingFailed(CardFinder finder, Throwable t){
		for (SearchObserver pl : listeners.keySet())
			if (listeners.get(pl) == null ||  listeners.get(pl).equals(finder))
				pl.searchingFailed(finder, t);
	}
	
	/**
	 * Notifies all {@link SearchObserver} that the pricing processes has completely ended 
	 * (no ongoing search) for any finder. 
	 * @param interrupted <code>true</code> if it was interrupted by the user
	 */
	private void firePricingFinished(boolean interrupted){
		for (SearchObserver pl : listeners.keySet())
				pl.searchingFinished(interrupted);
	}
	
	
	/**
	 * Runnable executed on a search thread. 
	 */
	private class SearchRunnable implements Runnable{
		
		CardFinder finder;
		SearchRunnable(CardFinder s){
			finder = s;
		}

		@Override
		public void run() {
			try {
				harvestResults(finder);
			} catch (final IOException e) {
				
				System.out.println("IO exception during search for " +
						finder + ": " + e.getMessage());
				e.printStackTrace();
				
				//Invoke on AWT dispatch thread.
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						fireHarvestingFailed(finder,e);
					}
				});
			}
		}
		
	}
}
