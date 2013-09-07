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
	
	
	public String produceReport(Deck deck, CardPricer cardPricer){
		
		String sourceName = cardPricer.getName();
		StringBuilder sb = new StringBuilder();
		int colWidths[] = new int[] {30,2,30,15,20,15};
		int maxColWidths[] = new int[colWidths.length];
		
		//Set default values.
		Arrays.fill(maxColWidths, 0);
		
		//Iterate the list and find the max lengts for each column.
		for (DeckCard card : deck.getCards()){
			
			if (maxColWidths[0] < card.getName().length())
				maxColWidths[0] = card.getName().length();
			
			if (maxColWidths[1] < (card.getQuantity()+"").length())
				maxColWidths[1] = (card.getQuantity()+"").length();
			
			Card fCard = card.getFoundCard(sourceName);
			
			if (maxColWidths[2] < fCard.getName().length())
				maxColWidths[2] = fCard.getName().length();
			
			if (maxColWidths[3] < fCard.getType().length())
				maxColWidths[3] = fCard.getType().length();
			
			if (maxColWidths[4] < fCard.getEdition().length())
				maxColWidths[4] = fCard.getEdition().length();
			
			int priceLength = (fCard.getPrice() + " "
								+ cardPricer.getCurrency()).length();
			if (maxColWidths[5] < priceLength)
				maxColWidths[5] = priceLength;
			
		}
		
		String interColString =" | ";
		colWidths = maxColWidths;

		for (DeckCard card : deck.getCards()){
			//Name and quantity of original deck cards.
			sb.append("| " + allignLeft(card.getName(),colWidths[0], interColString));
			sb.append(allignRight(card.getQuantity()+"",colWidths[1], interColString));
			//sb.append(emptyCol);
			
			Card fCard = card.getFoundCard(sourceName);
			
			sb.append(allignLeft(fCard.getName(),colWidths[2], interColString));
			sb.append(allignLeft(fCard.getType(),colWidths[3], interColString));
			sb.append(allignLeft(fCard.getEdition(),colWidths[4], interColString));
			sb.append(allignRight(fCard.getPrice() + " " +cardPricer.getCurrency()
					,colWidths[5], interColString));
			sb.append("\n");
		}
		
		return sb.toString();
		
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
		
		@SuppressWarnings("unused")
		String path = "d:\\deck.txt";
		String savePath = "d:\\savedDeck.dck";
		DeckPricer dp = new DeckPricer();
		/*
		
		Deck deck =dp.loadEvalueteDeck(path);
		serializeDeck(deck,savePath );
		*/
		Deck deck2 = deserializeDeck(savePath);
		
		//System.out.println(deck2);
		
		System.out.println(dp.produceReport(deck2, CardPricerFactory.getCernyRytirPricer()));
	}
	
	public Deck loadEvalueteDeck(String path){

		addProgressListener(new CardProgressListener() {
			
			@Override
			public void cardEvaluated(Card card) {
				System.out.println("Ready: " + card);
				
			}
		});
		
		Deck deck = null;
		try {
			deck = loadDeck(path);
		} catch (DeckPricerException e) {
			System.out.println("Error when loading the deck: " + e.getMessage());
			System.exit(1);
		}
		
		System.out.println("Deck parsed: " + deck);
		
		System.out.println("Starting price getting\n");
		
		
		CardPricer cPricer = CardPricerFactory.getCernyRytirPricer();
		try {
			evaluateDeck(deck,cPricer);
		} catch (DeckPricerException e) {
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
