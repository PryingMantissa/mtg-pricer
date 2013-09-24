package bbc.juniperus.mtgp.cardsearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbc.juniperus.mtgp.datastruc.DataModel;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Deck;
import bbc.juniperus.mtgp.domain.DeckCard;
import bbc.juniperus.mtgp.gui.Main;


public class Pricer implements ProgressListener{

	private Map<ProgressListener,Searcher> listeners = new HashMap<ProgressListener,Searcher>();
	private List<Searcher> searchers = new ArrayList<Searcher>();
	private DataModel data = new DataModel();
	//Any sequence of letter,',-,/ or white space (includes leading and trailing white spaces).
	public final static String REG_EXP_NAME = "[a-zA-Z\\s'-/]+";
	public final static String REG_EXP_NUMBER = "\\d+";

	public Pricer() {}
	
	public Pricer(DataModel data){
		this.data = data;
	}
	
	
	
	public void addCard(Card card, int quantity){
		data.addCard(card, quantity);	
	}
	
	public void addSearcher(Searcher searcher){
		searchers.add(searcher);
	}
	
	
	public void runLookUp() throws IOException{
		
		for (Searcher s : searchers){
			new Thread(new Executor(s)).start();
		}
	}
	
	
	private class Executor implements Runnable{
		
		Searcher searcher;
		
		Executor(Searcher s){
			searcher = s;
		}

		@Override
		public void run() {
			Map<Card, CardResult> results = null;
			try {
				results = harvestResults(searcher);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (this){
				data.addResults(results, searcher.getName());
			}
		}
		
	}
	
	
	private Map<Card,CardResult> harvestResults(Searcher searcher) throws IOException {
		
		System.out.println("harvesting " + searcher);
		Map<Card, CardResult> results = new HashMap<Card,CardResult>();
		//Search for all cards using the Searcher.
		for (Card card : data.getCards()){
			CardResult result = null;
			
			fireCardSearchStarted(card, searcher);
			result = searcher.findCheapestCard(card.getName());

			results.put(card, result);
			System.out.println("For " + card + " found " + result);
			fireCardSearchEnded(result, searcher);
		}
		
		return results;
	}
	
	public void loadCardsFromFile(File file) throws IOException, ParseException{
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
	
		int lineCounter=0;
		String line;
		while ((line =reader.readLine()) != null){
			lineCounter++;
			//Ignore empty lines
			if (line.trim().equals(""))
				continue;
			
			int q = parseQuantity(line);
			//If no number specified. Set it to 1.
			if (q == 0)
				q = 1;
			String name = parseCardName(line);
			
			if (name == null){
				//No name matching regexp was found int line.
				reader.close();
				throw new ParseException("Failed to parse " + file.getAbsolutePath() + 
									" on the line " + lineCounter +
									"'. The line has to contain expressions matching '"+
									REG_EXP_NAME + "' (name of the card).",0);
			}
			
			Card c = new Card(name);
			data.addCard(c, q);
		}
		reader.close();
	}
	
	private int parseQuantity(String line){
		Pattern pat = Pattern.compile(REG_EXP_NUMBER);
		Matcher mat = pat.matcher(line);
		
		int q = 0;
		if (mat.find())
			//If regex is correct we won't ge IllegalArgumentException
			q = Integer.parseInt(mat.group());
		
		return q;
	}
	
	private String parseCardName(String line){
		
		Pattern pat = Pattern.compile(REG_EXP_NAME);
		
		Matcher mat = pat.matcher(line);
		String name = null;
		
		if (mat.find()){
			//Trim leading and trailing \w.
			name = mat.group().trim();
			//In case of split cards written in bad format.
			name = name.replace("/", " // ");
		}

		return name;
	}
	
	public void saveDataToBin(String path) throws IOException{
		
		OutputStream fileOs = new FileOutputStream(path);
		OutputStream bufferOs = new BufferedOutputStream(fileOs);
		ObjectOutput objectOs = new ObjectOutputStream(bufferOs);
		objectOs.writeObject(data);
		if (objectOs != null)
				objectOs.close(); 
	}
	
	public void  loadDataFromBin(String path) throws IOException, ClassNotFoundException{
		DataModel result = null;
		InputStream fileIs = new FileInputStream(path);
		InputStream bufferIs = new BufferedInputStream(fileIs);
		ObjectInput objectIs = new ObjectInputStream(bufferIs);
		result = (DataModel) objectIs.readObject();
		
		if (objectIs != null)
			objectIs.close();
		data = result;
	}
	
	public int getCardListSize(){
		return data.getRowCount();
	}
	
	
	
	public DataModel data(){
		return data;
	}
		
	//Testing main
	public static void main(String[] args) {
		
	}
	
	/**================ Listener related methods*/
	
	public void addProgressListener(ProgressListener listener, Searcher searcher){
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
	
	
	@Override
	public void cardSearchEnded(CardResult result, Searcher searcher) {
		System.out.println(searcher.getName() + " , result: " + result);
		
		
	}

	@Override
	public void cardSearchStarted(Card card, Searcher searcher) {
		System.out.print("Searchin for " + card.getName() + " ");
		
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
