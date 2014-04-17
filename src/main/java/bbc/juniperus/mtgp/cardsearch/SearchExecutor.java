package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

	public enum Phase {SETTING, SEARCHING, PRICING_FINISHED}

	private Set<SearchObserver> observers = new HashSet<>();
	
	private volatile boolean interruped;
	private volatile int findersLeft;
	private volatile Map<CardFinder, SearchResults> results;
	
	private final Collection<CardFinder> finders;
	private final Collection<Card> cards;
	private Phase currentPhase;
	
	
	public SearchExecutor(Collection<Card> cards, Collection<CardFinder> finders){
		this.cards = cards;
		this.finders = finders;
		currentPhase = Phase.SETTING;
	}
	
	/**
	 * Stars the search. The current phase must be {@link Phase#SETTING } (first phase).
	 * For each set {@link CardFiner} the search process is started
	 * in separate thread.
	 * 
	 * @throws IllegalStateException if the current phase is not {@link Phase#SETTING}
	 */
	public void startSearch(){
		if (currentPhase != Phase.SETTING)
			throw new IllegalStateException("The search cannot be started"
					+ " because the current phase is not " + Phase.SETTING);

		results = new HashMap<>();
		currentPhase = Phase.SEARCHING;
		findersLeft = finders.size();
		fireSearchStarted(cards.size());
		for (CardFinder f : finders){
			results.put(f, new SearchResults(f));
			new Thread(new SearchRunnable(f)).start();
		}
	}
	
	/**
	 * Sets the interrupt flag to <code>true</code> which stops
	 * the running search threads. All pending requests issues by card finders on
	 * web servers need to return before the search as a whole is stopped.
	 */
	public void stopSearch(){
		if (currentPhase != Phase.SEARCHING)
			throw new IllegalStateException("The current phase is not " + Phase.SEARCHING);
		interruped = true;
	}
	
	/**
	 * Returns the current phase of the search
	 * @return current phase
	 */
	public Phase getCurrentPhase(){
		return currentPhase;
	}
	
	/**
	 * Returns a collection of card finders assigned to this search executor.
	 * @return assigned card finders  
	 */
	public Collection<CardFinder> getCardFinders(){
		return Collections.unmodifiableCollection(finders);
	}
	
	/**
	 * Returns copy of  search results for specific card finder.
	 * @param cardFinder
	 * @return car finder search results
	 */
	public SearchResults getResults(CardFinder cardFinder){
		if (!finders.contains(cardFinder))
			throw new IllegalArgumentException("No such finder registered with this search executor or null");
		if (currentPhase != Phase.PRICING_FINISHED)
			throw new IllegalStateException("The current phase is not " + Phase.PRICING_FINISHED);
		
		return results.get(cardFinder).makeClone();
	}

	/**
	 * Returns copy of search results for all card finders.
	 * @return search results for all card finders
	 */
	public Collection<SearchResults> getResults(){
		List<SearchResults> resList = new ArrayList<>();
		
		for (SearchResults sr : results.values())
			resList.add(sr.makeClone());
		
		return resList;
	}
	
	
	/**
	 * Contains code invoked by search thread (with {@link SearchRunnable}.
	 * This is not run on event dispatch thread.
	 * @param finder card finder for the search
	 * @throws IOException
	 */
	private void startSearching(CardFinder finder) throws IOException {
		long timeStart = System.currentTimeMillis();
		SearchResults theResults = this.results.get(finder);
		
		for (Card card : cards){
			//Test if stopped.
			if (interruped){
				System.out.println("interrupted " + finder);
				fireSearchThreadFinished(finder); //Finishing just this finder's worker thread.
				
				//If this is the last running thread consider the search to be finished.
				if (--findersLeft < 1){ //TODO put in one method with the other part
					assert currentPhase == Phase.SEARCHING;
					currentPhase = Phase.PRICING_FINISHED;
					System.out.println("interrupted " + finder);
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
			currentPhase = Phase.PRICING_FINISHED;
			fireSearchFinished(false);
		}
	}

	/**
	 *	Listener methods 
	 *================================================================================ 
	 */
	
	/**
	 * Registers an observer to receive the notifications from the ongoing search. <p>
	 * For each card finder, the observer's methods (with the exception of {@link SearchObserver#searchStarted(int)})
	 * are invoked on a  separate worker thread <b>which is not the event dispatch thread.</b> 
	 * @param observer the observer object
	 * @see {@link CardFinder}
	 */
	public void addSearchObserver(SearchObserver observer){
		boolean isNew = observers.add(observer);
		assert isNew;
	}
	

	private void fireSearchStarted(int numberOfCards){
		for (SearchObserver o : observers)
			o.searchStarted(numberOfCards);
	}
	

	private void fireCardSearchStarted(Card card, CardFinder finder){
		for (SearchObserver o : observers)
			o.startedSearchingForCard(card, finder);
	}
	
	private void fireCardSearchEnded(Card card, CardResult result, CardFinder finder){
		for (SearchObserver o : observers)
			o.finishedSearchingForCard(card, result, finder);
	}
	

	private void fireSearchThreadFinished(CardFinder finder){
		for (SearchObserver o : observers)
				o.searchThreadFinished(finder);
	}

	private void fireSearchThreadFailed(CardFinder finder, Throwable t){
		for (SearchObserver o : observers)
				o.searchThreadFailed(finder, t);
	}
	
	private void fireSearchFinished(boolean interrupted){
		for (SearchObserver o : observers)
				o.searchingFinished(interrupted);
	}
	
	/**
	 *================================================================================ 
	 */
	
	
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
