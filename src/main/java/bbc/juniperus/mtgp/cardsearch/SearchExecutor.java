package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

/**
 * Executor for  card search. Searches for all set {@link Card} with each set {@link CardFinder}.
 * When started, each {@CardFinder} is run in separate thread. All notifications to observers are therefore
 * on various threads.     
 */
public class SearchExecutor{

	private Set<SearchObserver> observers = new HashSet<>();
	
	private volatile boolean interruped;
	private volatile int findersLeft;
	private volatile boolean searchInProgress;
	private volatile Map<CardFinder, SearchResults> results;
	
	private final Collection<CardFinder> finders;
	private final Collection<Card> cards;
	
	
	
	public SearchExecutor(Collection<Card> cards, Collection<CardFinder> finders){
		this.cards = cards;
		this.finders = finders;
	}
	
	/**
	 * Stars the search. For each set {@link CardFiner} the search process is started
	 * in separate thread.
	 */
	public void startSearch(){
		results = new HashMap<>();
		
		searchInProgress = true;
		findersLeft = finders.size();
		for (CardFinder f : finders){
			results.put(f, new SearchResults(f));
			new Thread(new SearchRunnable(f)).start();
		}
	}
	
	/**
	 * Sets the interrupt flag to <code>true</code> which stops
	 * the running search threads.
	 */
	public void stopSearch(){
		interruped = true;
	}
	
	/**
	 * Determines if search is currently running.
	 * @return <code>true</code> if search is in progress, <code>false</code> if not
	 */
	public boolean isSearchInProgress(){
		return searchInProgress;
	}
	
	public SearchResults getResults(CardFinder cardFinder){
		
		if (!finders.contains(cardFinder))
			throw new IllegalArgumentException("No such finder registered with this search executor or null");
		
		return results.get(cardFinder);
	}
	
	
	/**
	 * Contains code invoked by search thread (with {@link SearchRunnable}.
	 * @param finder card finder for the search
	 * @throws IOException
	 */
	private void startSearching(CardFinder finder) throws IOException {
		
		long timeStart = System.currentTimeMillis();
		SearchResults theResults = this.results.get(finder);
		
		for (Card card : cards){
			
			//Test if stopped.
			if (interruped){
				fireSearchThreadFinished(finder); //Finishing just this finder's worker thread.
				
				//If this is the last running thread consider the search to be finished.
				if (--findersLeft < 1){
					searchInProgress = false;
					fireSearchFinished(true);
				}
				return;
			}
			
			//Starting...
			fireCardSearchStarted(card, finder);
			CardResult result = finder.findCheapestCard(card.getName());
			
			if (result == null)
				theResults.addNotFound(card);
			else
				theResults.addCardResult(card, result);
			//Ending...
			fireCardSearchEnded(card, result, finder);
		}
		
		theResults.setSearchTime(System.currentTimeMillis() - timeStart);
		
		fireSearchThreadFinished(finder);
		
		//If this was the last search thread then mark the search as finished.
		if (--findersLeft < 1){
			searchInProgress = false;
			fireSearchFinished(false);
		}
	}

	//================ Listeners related methods ========================
	/**
	 * Registers {@link SearchObserver} to receive the notifications from the ongoing search.
	 * For each {@link CardFinder} the notifying is executed on <b> separate worker thread which is not event dispatch thread.</b>
	 * @param observer observer object
	 */
	public void addSearchObserver(SearchObserver observer){
		boolean isNew = observers.add(observer);
		assert isNew;
	}
	
	/**
	 * See {@link SearchObserver#startedSearchingForCard(Card, CardFinder)}
	 * @param card the card for which the searching has begun
	 * @param finder the involved finder
	 */
	private void fireCardSearchStarted(Card card, CardFinder finder){
		for (SearchObserver o : observers)
			o.startedSearchingForCard(card, finder);
	
	}
	
	/**
	 * See {@link SearchObserver#finishedSearchingFor(CardResult, CardFinder)}
	 * @param result the result from the finished searching for the card
	 * @param finder the finder which was involved
	 */
	private void fireCardSearchEnded(Card card, CardResult result, CardFinder finder){
		for (SearchObserver o : observers)
			o.finishedSearchingForCard(card, result, finder);
	}
	
	/**
	 * Informs all {@link SearchObserver} that the search with a given card finder
	 * has successfully ended.
	 * @param finder finder involved
	 * @param data data related to the search
	 */
	private void fireSearchThreadFinished(CardFinder finder){
		for (SearchObserver o : observers)
				o.searchingFinished(finder);
	}
	
	/**
	 * Informs all registered {@link SearchObserver}s that the search with a given card finder
	 *  has encountered an (IO) error and ended. Is invoked on AWT dispatch thread.
	 * @param listener finder involved
	 * @param t cause
	 */
	private void fireSearchThreadFailed(CardFinder finder, Throwable t){
		for (SearchObserver o : observers)
				o.searchingFailed(finder, t);
	}
	
	/**
	 * Notifies all {@link SearchObserver} that the pricing processes has completely ended 
	 * (no ongoing search) for any finder. 
	 * @param interrupted <code>true</code> if it was interrupted by the user
	 */
	private void fireSearchFinished(boolean interrupted){
		for (SearchObserver o : observers)
				o.searchingFinished(interrupted);
	}
	
	
	/**
	 * Runnable executed on a search thread. 
	 */
	private class SearchRunnable implements Runnable {

		CardFinder finder;

		SearchRunnable(CardFinder s) {
			finder = s;
		}

		@Override
		public void run() {
			try {
				startSearching(finder);
			} catch (final IOException e) {
				System.out.println("IO exception during search for " + finder
						+ ": " + e.getMessage());
				e.printStackTrace();
				fireSearchThreadFailed(finder, e);
			}
		}
	}
		
	
}
