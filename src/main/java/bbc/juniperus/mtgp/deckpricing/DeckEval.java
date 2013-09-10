package bbc.juniperus.mtgp.deckpricing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bbc.juniperus.mtgp.cardpricing.CardPricer;
import bbc.juniperus.mtgp.cardpricing.CardPricerFactory;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Deck;
import bbc.juniperus.mtgp.domain.DeckCard;

public class DeckEval {

	/** Number of times in the row when the DeckPricer will accept exception
	 * when looking for card.
	 */
	public static final int MAX_TRIES = 5;
	private List<CardPricingProgressListener> listeners = new ArrayList<CardPricingProgressListener>();
	
	public void addProgressListener(CardPricingProgressListener listener){
		listeners.add(listener);
	}
	
	public void fireCardEvaluated(CardResult card){
		for (CardPricingProgressListener pl : listeners)
			pl.cardEvaluated(card);
	}
	
	
	public Deck loadDeck(String path) throws DeckEvalException{
		
		File f = new File(path);
		
		if (!f.exists())
			throw new DeckEvalException("The file does not exits.");

		Deck deck = new Deck();
		
		try {
			deck.readFromFile(f);
		} catch (IOException e) {
			throw new DeckEvalException("Error when reading from the file.\n" + e.getMessage());
		} catch (ParseException e) {
			throw new DeckEvalException("Error when parsing the file.\n" + e.getMessage());
		}
		
		return deck;
	}
	
	
	public void evaluateDeck(Deck deck, CardPricer pricer) throws DeckEvalException{
		
		int notFoundCounter = 0;
		String errorMsg = null;
		for (DeckCard card : deck.getCards()){
			CardResult foundCard = null;
			try {
				foundCard = pricer.findCheapestCard(card.getName());
			} catch (IOException e) {
				notFoundCounter ++;
				if (notFoundCounter > MAX_TRIES)
					throw new DeckEvalException("Error when attempting to get price of the cards." +
													"Too many failed attemps in a row(" + MAX_TRIES +").");
				errorMsg = "Error while getting the price: " +e.getMessage();
			}
			
			//No card found. Set the message why.
			if (foundCard == null){
				foundCard = new CardResult("- Not Found -","N/A","N/A", -1);
				foundCard.setNotFound(errorMsg == null? "Not found" : errorMsg);
			}
			
				
			card.addFoundCard(pricer.getName(), foundCard);
			//Fire it for listeners.
			fireCardEvaluated(foundCard);
		}
	}
	
	
	public String produceReport(Deck deck, CardPricer[] cardPricers){
		
		//2 main columns + 2 per CardPricer (card name and price)
		int colWidths[] = new int[2 + cardPricers.length*2 ];
		double[] deckPrices = new double[cardPricers.length];
		
		//Set default values.
		Arrays.fill(colWidths, 0);
		
		//Iterate the list and find the max length for each column.
		for (DeckCard card : deck.getCards()){
			
			if (colWidths[0] < card.getName().length())
				colWidths[0] = card.getName().length();
			
			String q = Integer.toString(card.getQuantity());
			if (colWidths[1] < q.length())
				colWidths[1] = (q.length());
			
			int index =2;
			for (CardPricer cp : cardPricers){
				CardResult cardResult = card.getCardResult(cp.getName());
				
				if (colWidths[index] < cardResult.getName().length())
					colWidths[index] = cardResult.getName().length();
				index++;
				
				//Make String version of card price.
				String price = formatPrice(cardResult, cp);
				if (colWidths[index] < price.length())
					colWidths[index] = price.length();
				index++;
				
			}
		}
		
		
		
		String interColString =" | ";

		StringBuilder sb = new StringBuilder();
		for (DeckCard card : deck.getCards()){
			int index = 0;
			//Name and quantity of original deck cards.
			sb.append("| " + allignLeft(card.getName(),colWidths[index], interColString));
			sb.append(allignRight(card.getQuantity()+"",colWidths[index], interColString));

			for (CardPricer cp : cardPricers){
				CardResult cardResult = card.getCardResult(cp.getName());
				
				sb.append(allignLeft(cardResult.getName(),colWidths[index], interColString));
				index++;

				//Make String version of card price.
				String price = formatPrice(cardResult, cp);
				sb.append(allignRight(price,colWidths[index], interColString));
				index++;
				
			}
			sb.append("\n");
		}

		//sb.append("Total deck price is: " + String.format("%.2f",deckPrice) + " " +cardPricer.getCurrency());
		return sb.toString();
		
	}
	
	
	public static String formatPrice(CardResult cardResult, CardPricer cardPricer){
		String price = cardResult.getPrice() < 0 ? "N/A" : String.format("%.2f", cardResult.getPrice());
		price = price + " " + cardPricer.getCurrency();
		
		return price;
	}
	
	
	public static String allignLeft(String s, int width, String interColString){
		return String.format("%-" + width + "s", s) +interColString;
	}
	
	public static String allignRight(String s, int width, String interColString){
		return String.format("%" + width + "s", s) +interColString;
	}
	
	
	public static void serializeDeck(Deck deck,String path){
		
		try{
			OutputStream fileOs = new FileOutputStream(path);
			OutputStream bufferOs = new BufferedOutputStream(fileOs);
			ObjectOutput objectOs = new ObjectOutputStream(bufferOs);
		      
		      try{
		    	  objectOs.writeObject(deck);
		      }finally{
		    	  objectOs.close();
		      }

		}catch(IOException e){
		      System.out.println("Saving deck failed: " + e.getMessage());
		}

	}
	
	public static Deck deserializeDeck(String path){
		
		Deck result = null;
		try{
		      InputStream fileIs = new FileInputStream(path);
		      InputStream bufferIs = new BufferedInputStream(fileIs);
		      ObjectInput objectIs = new ObjectInputStream(bufferIs);
		      try{
		    	  result = (Deck) objectIs.readObject();
		      }
		      finally{
		    	  objectIs.close();
		      }
	    }catch(ClassNotFoundException e){
	    	System.out.println("Loading deck failed: " + e.getMessage());
	    }catch(IOException e){
	    	System.out.println("Loading deck failed: " + e.getMessage());
	    }catch(ClassCastException e){
	    	System.out.println("Loading deck failed: " + e.getMessage());
	    }
		
		return result;
	}
	
	
	
	//Testing main
	public static void main(String[] args) {
		
		
		String path = "d:\\deck.txt";
		@SuppressWarnings("unused")
		String savePath = "d:\\savedDeck.dck";
		DeckEval dp = new DeckEval();
		
		
		
		Deck deck = null;
		CardPricer cPricer = CardPricerFactory.getCernyRytirPricer();
		deck =dp.loadEvalueteDeck(path,cPricer);
		
		cPricer = CardPricerFactory.getModraVeverickaPricer();
		deck =dp.loadEvalueteDeck(path,cPricer);
		
		cPricer = CardPricerFactory.getDragonPricer();
		deck =dp.loadEvalueteDeck(path,cPricer);
		
		
		serializeDeck(deck,savePath );
		/*
		deck = deserializeDeck(savePath);
		
		
		
		System.out.println(dp.produceReport(deck,cPricer));
		*/
	}
	
	public Deck loadEvalueteDeck(String path, CardPricer cardPricer){

		addProgressListener(new CardPricingProgressListener() {
			
			@Override
			public void cardEvaluated(CardResult card) {
				System.out.println("Ready: " + card);
				
			}
		});
		
		Deck deck = null;
		try {
			deck = loadDeck(path);
		} catch (DeckEvalException e) {
			System.out.println("Error when loading the deck: " + e.getMessage());
			System.exit(1);
		}
		
		System.out.println("Deck parsed: " + deck);
		
		System.out.println("Starting price getting\n");
		

		try {
			evaluateDeck(deck,cardPricer);
		} catch (DeckEvalException e) {
			System.out.println("Erro when evaluting deck: " + e.getMessage());
			System.exit(1);
		}
		
		return deck;
		
		/**
		for (DeckCard dc : deck.getCards()){
			System.out.println(dc);
			System.out.println(dc.stringifyFoundCards());
		}
		*/
	}
	
	

}
