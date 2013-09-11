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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.SearcherFactory;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Deck;
import bbc.juniperus.mtgp.domain.DeckCard;

public class Pricer implements SearchProgressListener{

	/** Number of times in the row when the DeckPricer will accept exception
	 * when looking for card.
	 */
	public static final int MAX_TRIES = 5;
	private List<SearchProgressListener> listeners = new ArrayList<SearchProgressListener>();
	private List<Searcher> searchers = new ArrayList<Searcher>();
	private List<Card> cards = new ArrayList<Card>();
	private Map<Card,Integer> quantityMap = new HashMap<Card,Integer>();
	private Map<Card,Map<String,CardResult>> results = new HashMap<Card,Map<String,CardResult>>();
	private boolean withQuantity;
	private List<List<String>> reportMatrix = new ArrayList<List<String>>();
	private int colsCount;
	private int[] colsWidth;
	
	
	public void fill(List<Card> cardsList, List<Integer> quantity){	
		cards.clear();
		
		if (quantity ==null){
			withQuantity = false;
			cards.addAll(cardsList);
			return;
		}
		
		withQuantity = true;
		
		if (cardsList.size() != quantity.size())
			throw new IllegalArgumentException("Card List size does not match quantity List size");
		
		for (int i=0; i  < cardsList.size(); i++ ){
			Card card = cardsList.get(i);
			cards.add(card);
			quantityMap.put(card, quantity.get(i));
		}
		
		
	}
	

	public void clearCards(){
		cards.clear();
	}
	
	public List<Card> getCards(){
		return Collections.unmodifiableList(cards);
	}

	
	
	public void addSearcher(Searcher searcher){
		searchers.add(searcher);
	}
	
	public void addProgressListener(SearchProgressListener listener){
		listeners.add(listener);
	}
	
	public void fireCardSearched(Card card,CardResult result, Searcher searcher){
		for (SearchProgressListener pl : listeners)
			pl.cardSearched(card,result,searcher);
	}
	
	
	public void find() throws DeckEvalException{
		for (Searcher s : searchers)
			find(s);
	}
	
	
	public void find(Searcher searcher) throws DeckEvalException{
		
		//Search for all cards using the Searcher.
		for (Card card : cards){
			CardResult result = null;
			try {
				result = searcher.findCheapestCard(card.getName());
			} catch (IOException e) {
					throw new DeckEvalException("IO error when attempting to get price of the cards." +
													e.getMessage());
			}
	
			Map<String,CardResult>  resultSet = results.get(card);
			if (resultSet == null)
				resultSet = new HashMap<String,CardResult>();
			resultSet.put(searcher.getName(),result);
			results.put(card, resultSet);
			//Fire it for listeners.
			fireCardSearched(card,result, searcher);
		}
	}
	
	/**
	 * Create String matrix and also determine the maximum length of String in each column.
	 */
	private void makeReportMatrix(){
		
		if (withQuantity)
			colsCount = 2 + searchers.size()*2;
		else
			colsCount = 1 + searchers.size()*2;
		
		colsWidth = new int[colsCount];
		Arrays.fill(colsWidth, 0);
		
		
		//Header
		String[] headers = new String[colsCount];
		int index =0;
		headers[index] ="Card name";
		index++;
		
		if (withQuantity){
			headers[index] = "Quantity";
			index++;
		}
		
		for (Searcher s : searchers){
			headers[index] = s.getName();
			index++;
			headers[index] = "";
			index++;
		}
		
		List<String> headerRow = new ArrayList<String>();
		for (int i=0; i < headers.length;i++){
			headerRow.add(headers[i]);
			if (headers[i].length() > colsWidth[i])
				colsWidth[i] = headers[i].length();
		}
				
		reportMatrix.add(headerRow);
		//Result
		for (Card card :cards){
			List<String> row = new ArrayList<String>();
			index =0;
			int q = quantityMap.get(card);
			
			String cardName = card.getName();
			row.add(cardName);
			colsWidth[index] = cardName.length() > colsWidth[index]?
									cardName.length() : colsWidth[index];
			index++;
				
			if (withQuantity){
				String qString = q +"";
				row.add(qString);
				colsWidth[index] = qString.length() > colsWidth[index]?
						qString.length() : colsWidth[index];
				index++;
			}
			
			
			for (Searcher s : searchers){
				CardResult cr = results.get(card).get(s.getName());
				
				System.out.println(cr + "," + s.getName());
				
				
				String resultName = null; 
				String currencyString = null;
				String priceString = null;
				//Could be set flexibily to empty String.
				currencyString = " " + s.getCurrency();
				if (cr != null){
					resultName = cr.getName();
					priceString =cr.getPrice() + currencyString;
					
				}else{
					resultName = "Not found";
					priceString ="N/A" + currencyString;;
				}
				//Name of card result.
				row.add(resultName);
				row.add(priceString);
				
				colsWidth[index] = resultName.length() > colsWidth[index]?
						resultName.length() : colsWidth[index];
				index++;
				colsWidth[index] = priceString.length() > colsWidth[index]?
									priceString.length() : colsWidth[index];
				index++;
							
			}
			reportMatrix.add(row);
		}
	}
	
	
	
	public String produceReport(){
		makeReportMatrix();
		
		System.out.println(Arrays.toString(colsWidth));
		
		String interColString =" | ";

		Map<Integer,Searcher> priceColumns = new HashMap<Integer,Searcher>();
		
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i < reportMatrix.size() ;i++){
			List<String> row = reportMatrix.get(i);
			
			//Divider between header and results.
			if (i==1){
				sb.append("| ");
				for (int w : colsWidth){
					for (int j=0; j < w;j++)
						sb.append("*");
					sb.append(interColString);
				}
				sb.append("\n");
			}
			
			
			int index=0;
			sb.append("| " + allignLeft(row.get(index),colsWidth[index],interColString));
			index++;
			if (withQuantity){
				sb.append(allignRight(row.get(index),colsWidth[index],interColString));
				index++;
			}
			
			for (Searcher s : searchers){
				sb.append(allignLeft(row.get(index),colsWidth[index],interColString));
				index++;
				//Price column.
				sb.append(allignRight(row.get(index),colsWidth[index],interColString));
				priceColumns.put(index, s);
				index++;
			}
			sb.append("\n");
		}
		
		//Closing divider of result from final line.
		sb.append("| ");
		for (int w : colsWidth){
			for (int j=0; j < w;j++)
				sb.append("*");
			sb.append(interColString);
		}
		sb.append("\n");
		
		//Put totals
		
		sb.append("| ");
		for (int i = 0; i < colsWidth.length;i++){
			//First column.
			if (i==0)
				//TODO this one is not evaluated when determinig max String length in column.
				sb.append(allignLeft("Total price:",colsWidth[i],interColString));
			else if(priceColumns.containsKey(i)){
				//Column containing  prirce.
				Searcher s = priceColumns.get(i);
				String totalPrice = formatDouble(countTotalPrice(s))
										+ " " + s.getCurrency();
				sb.append(allignRight(totalPrice,colsWidth[i],interColString));
			}
			else{
				//Normal column.
				for (int j=0; j < colsWidth[i];j++)
					sb.append(" ");
				sb.append(interColString);
			}
			
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	
	private double countTotalPrice(Searcher s){
		
		double result = 0;
		
		for (Card c : cards){
			int q = quantityMap.get(c);
			CardResult cr = results.get(c).get(s.getName());
			if (cr != null)
				result += q*cr.getPrice();
		}
		return result;
	}
	

	public static String allignLeft(String s, int width, String interColString){
		return String.format("%-" + width + "s", s) +interColString;
	}
	
	public static String allignRight(String s, int width, String interColString){
		return String.format("%" + width + "s", s) +interColString;
	}
	
	
	private static String formatDouble(double d){
		return String.format("%1$,.2f", d);
	}
	
	public void serializeResults(String path){
		
		/*
		results.put(new Card("kokot"), null);
		results.put(new Card("pica"), null);
		*/
		
		try{
			OutputStream fileOs = new FileOutputStream(path);
			OutputStream bufferOs = new BufferedOutputStream(fileOs);
			ObjectOutput objectOs = new ObjectOutputStream(bufferOs);
		      
		      try{
		    	  objectOs.writeObject(results);
		      }finally{
		    	  objectOs.close();
		      }

		}catch(IOException e){
		      System.out.println("Saving Pricer failed: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void  deserializeResults(String path){
		
		Map<Card,Map<String,CardResult>> result = null;
		try{
		      InputStream fileIs = new FileInputStream(path);
		      InputStream bufferIs = new BufferedInputStream(fileIs);
		      ObjectInput objectIs = new ObjectInputStream(bufferIs);
		      try{
		    	  result = (Map<Card,Map<String,CardResult>>) objectIs.readObject();
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
		
		results = result;
	}
	
	
	
	//Testing main
	public static void main(String[] args) throws IOException, ParseException, DeckEvalException {
		
	
		
		String path = "d:\\deck.txt";
		@SuppressWarnings("unused")
		String savePath = "d:\\savePricer.prc";
		Pricer pc = new Pricer();
		
		File f = new File(path);
		
		Deck deck = new Deck();
		
		deck.readFromFile(f);
		
		System.out.println(deck);
		
		List<Searcher> searchers = new ArrayList<Searcher>();
		
		searchers.add(SearcherFactory.getCernyRytirPricer());
		searchers.add(SearcherFactory.getModraVeverickaPricer());
		searchers.add(SearcherFactory.getDragonPricer());
		
		
		pc.fill(deck.getCards(), deck.getQuantity());
		
		for(Searcher s : searchers)
			pc.addSearcher(s);
		
		pc.addProgressListener(pc);
		
		//pc.find();
		
		System.out.println(pc.results);
		//pc.serializeResults(savePath);
		
		pc.deserializeResults(savePath);
		System.out.println(pc.results);
		System.out.println(pc.produceReport());
		
		/*
		Card c = pc.results.keySet().iterator().next();
		
		System.out.println(c);
		System.out.println(pc.results.get(c).get("Modra Vevericka"));
		*/
		
		
		/*
		deck = deserializeDeck(savePath);
		
		
		
		System.out.println(dp.produceReport(deck,cPricer));
		*/
	}


	@Override
	public void cardSearched(Card card, CardResult result, Searcher searcher) {
		System.out.println("End of search: " + card.getName() +" with " + 
							searcher.getName() + " , result: " + result);
		
		
	}
	
	
	/*
	public Deck loadEvalueteDeck(String path, CardPricer cardPricer){

		addProgressListener(new SearchProgressListener() {
			
			@Override
			public void cardSearched(CardResult card) {
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
		

	}
	*/
	

}
