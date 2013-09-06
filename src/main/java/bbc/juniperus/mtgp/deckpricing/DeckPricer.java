package bbc.juniperus.mtgp.deckpricing;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import bbc.juniperus.mtgp.cardpricing.CardPricer;
import bbc.juniperus.mtgp.cardpricing.CardPricerFactory;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.Deck;
import bbc.juniperus.mtgp.domain.DeckCard;

public class DeckPricer {

	/** Number of times in the row when the DeckPricer will accept exception
	 * when looking for card.
	 */
	public static final int MAX_TRIES = 5;
	private List<CardProgressListener> listeners = new ArrayList<CardProgressListener>();
	
	public void addProgressListener(CardProgressListener listener){
		listeners.add(listener);
	}
	
	public void fireCardEvaluated(Card card){
		for (CardProgressListener pl : listeners)
			pl.cardEvaluated(card);
	}
	
	
	public Deck loadDeck(String path) throws DeckPricerException{
		
		File f = new File(path);
		
		if (!f.exists())
			throw new DeckPricerException("The file does not exits.");

		Deck deck = new Deck();
		
		try {
			deck.readFromFile(f);
		} catch (IOException e) {
			throw new DeckPricerException("Error when reading from the file.\n" + e.getMessage());
		} catch (ParseException e) {
			throw new DeckPricerException("Error when parsing the file.\n" + e.getMessage());
		}
		
		return deck;
	}
	
	
	public void evaluateDeck(Deck deck, CardPricer pricer) throws DeckPricerException{
		
		int notFoundCounter = 0;
		String errorMsg = null;
		for (DeckCard card : deck.getCards()){
			Card foundCard = null;
			try {
				foundCard = pricer.findCheapestCard(card.getName());
			} catch (IOException e) {
				notFoundCounter ++;
				if (notFoundCounter > MAX_TRIES)
					throw new DeckPricerException("Error when attempting to get price of the cards." +
													"Too many failed attemps in a row(" + MAX_TRIES +").");
				errorMsg = "Error while getting the price: " +e.getMessage();
			}
			
			//No card found. Set the message why.
			if (foundCard == null){
				foundCard = new Card(null,null,null, -1);
				foundCard.setNotFound(errorMsg == null? "Not found" : errorMsg);
			}
				
			card.addFoundCard(pricer.getName(), foundCard);
			//Fire it for listeners.
			fireCardEvaluated(foundCard);
		}
	}
	
	
	
	public static void main(String[] args) {
		
		String path = "d:\\deck.txt";
		
		DeckPricer dp = new DeckPricer();
		
		dp.addProgressListener(new CardProgressListener() {
			
			@Override
			public void cardEvaluated(Card card) {
				System.out.println("Ready: " + card);
				
			}
		});
		
		Deck deck = null;
		try {
			deck = dp.loadDeck(path);
		} catch (DeckPricerException e) {
			System.out.println("Error when loading the deck: " + e.getMessage());
			System.exit(1);
		}
		
		System.out.println("Deck parsed: " + deck);
		
		System.out.println("Starting price getting\n");
		
		try {
			dp.evaluateDeck(deck,CardPricerFactory.getCernyRytirPricer());
		} catch (DeckPricerException e) {
			System.out.println("Erro when evaluting deck: " + e.getMessage());
			System.exit(1);
		}
		
		
		for (DeckCard dc : deck.getCards()){
			System.out.println(dc);
			System.out.println(dc.stringifyFoundCards());
		}
	}
	
	
	
	

}
