package bbc.juniperus.mtgp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MtgPricer {
	
	public final int resultsPerPageCR = 30;
	
	public static void main (String[] args){

		String path = args[0];
		
		Deck d = new Deck();
		
		try {
			d.readFromFile(path);
		} catch (FileNotFoundException e){
			System.out.println("The File could not be found.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("An error when reading from the file.");
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (IllegalStateException e){
			System.out.println("The deck file could not be parsed");
			System.exit(1);
		}
		
		System.out.println("Deck file parsed.\n\nRetrieving now info from http://www.cernyrytir.cz/\n");
		
		MtgPricer mp = new MtgPricer();
		try {
			mp.evalDeck(d);
		} catch (UnknownHostException e){
			System.out.println("The server could not be reached. Try later.");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("There is a connection problem. Try later.");
			System.out.println(e.getMessage());
		}
	}
	
	
	public void evalDeck(Deck deck) throws IOException{
		
		int priceTotal = 0;
		
		List<String> cardNames = deck.getCardNames();
		System.out.print("Card in deck");
		System.out.print("                  "); //18 spaces
		System.out.print("Card found on cernyrytir.cz");
		System.out.print("                              ");
		System.out.print("Piece price");
		System.out.print("          ");
		System.out.println("Total price");
		
		System.out.println("***************************************"
				+ "*****************************************************"
				+ "****************************");
		
		for (String name :cardNames){
			Card c = findCard(name);
			int q = deck.getQuantityOf(name);
			System.out.printf("%-30s",name);
			
			if (c == null){
				System.out.println("- not found -");
				continue;
			}
			System.out.printf("%-60s",c.getName() + " /" + c.getType() + "/ - " +
					c.getEdition());
			System.out.printf("%7s",c.getPrice() + " Kc");
			System.out.printf("%-9s", "   x " +q);
			System.out.print("=");
			System.out.printf("%8s",c.getPrice() *q + " Kc");
			System.out.println();
			
			priceTotal +=q * c.getPrice();
		}
		
		System.out.println("***************************************"
				+ "*****************************************************"
				+ "****************************");
		
		System.out.println("                                        "
				+ "                                            "
				+ "        Total deck price: " + priceTotal + " Kc");
		
	}
	
	
	/**
	 * Finds the best match for the card name - cheapest one.
	 * @param cardName
	 * @return
	 * @throws IOException
	 */
	public Card findCard(String cardName) throws IOException{
		
		String normalizedCardName = normalizeCardName(cardName);
		List<Card> foundCards = findCardMatches(normalizedCardName);
		
		/* Remove cards which does not exactly match the name
		 * e.g. Mountain search return Goblin Mountaineer as well.
		 * Also foil version of cards (Mountain - foil) will be removed (they are more expensive anyway).
		 */
		
		normalizedCardName = normalizedCardName.replaceAll("[`´]", "'"); //Be sure to have "'" instead of "`" so it can be compared.
		
		for (Card card: new ArrayList<Card>(foundCards)){

			if (!card.getName().equalsIgnoreCase(normalizedCardName))
				foundCards.remove(card);
			
		}
		
		if (foundCards.size() < 1)
			return null;
		
		//Select the cheapest card.
		Card cheapest = foundCards.get(0);
		
		for (Card c : foundCards)
			if (cheapest.getPrice() > c.getPrice())
				cheapest = c;
		
		
		return cheapest;
	}
	
	
	
	
	/**
	 * Retrieves the list of cards that match the card name.
	 * @param cardName Name of the card to be found.
	 * @return
	 * @throws IOException
	 */
	public List<Card> findCardMatches(String cardName) throws IOException{
		
		/** 1. load the cards from the first page (might be last as well)*/
		
		//Get the html result page from the query as String.
		String html =  getHTMLString(createURL(cardName,1));
		
		List<Card> foundCards = new ArrayList<Card>();
		
		//Add all results we found on the first page.
		foundCards.addAll(getCardsFromHtml(html));
		
		
		/**2. check for additional pages and load results from them as well*/
		
		//Determine if there are also additional pages.
		Document doc = Jsoup.parse(html);
		Elements span = doc.select("span.kusovkytext");
		int pagesTotal = 1;
		
		//If the special element exists, it has more pages -> calculate how many.
		if (span.size() > 0){
			int resultsCount  = getIntFromString(span.text());
			pagesTotal = (int) Math.ceil((float) resultsCount / resultsPerPageCR);
			
			//Load cards from other pages as well.
			for (int i =2; i <= pagesTotal; i++){
				html = getHTMLString(createURL(cardName,i));
				foundCards.addAll(getCardsFromHtml(html));
			}
		}
		return foundCards;
	}
	
	
	
	
	private String createURL(String cardName, int page){
		
		final String addressCR = "http://www.cernyrytir.cz/index.php3";
		final String urlParam = "akce=3&"
				
				+ "limit="+ (page-1) * resultsPerPageCR
				+ "&jmenokarty="+ cardName.replace(" ", "+")
				
				+ "&edice_magic=libovolna&poczob=30&foil=A&"
				+ "triditpodle=ceny&hledej_pouze_magic=1&submit=Vyhledej";
		
		
		//System.out.println(addressCR + "?" + urlParam);
		
		return addressCR + "?" + urlParam;
	}
	
	
	public  List<Card> getCardsFromHtml(String html){
		
		Document doc = Jsoup.parse(html);
		
		List<Card> foundCards = new ArrayList<Card>();
		
		//Find second table with kusovkytext class which contains the elements with info.
		//Extract table rows containing the required info.
		Elements resultRows = doc.select("table.kusovkytext").get(1).select("tbody > tr");
		
		String name = null; 
		String edition = null;
		String type = null;
		String price = null;
		
		for (int i = 0; i < resultRows.size(); i++){
			
			int modRes = i % 3;
			
			//1st row
			if (modRes == 0)
				name = resultRows.get(i).select("td div font").text();
			//2nd row
			else if (modRes == 1)
				edition = resultRows.get(i).select("td:eq(0)").text();
			//Last row -> modRes == 2
			else{
				type = resultRows.get(i).select("td:eq(0)").text();
				price = resultRows.get(i).select("td:eq(2)").text();
				//Add card
				foundCards.add(new Card(name,type, edition, getIntFromString(price)));
			}
		}
		
		return foundCards;
	}
	
	
	
	public String getHTMLString(String address) throws IOException{
		URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
        
        connection.setRequestMethod("GET");

        //Get Response
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();

       return response.toString();
	}
	
	
	public String postHTMLString(String address, String parameters) throws IOException{
		
		//TODO Double code - OPTIMALIZE!
		
		URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
        
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Language", "en-US");
        //set accepting language?
        connection.setRequestProperty("Content-Length","" + parameters.getBytes().length); 
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true); 
        
        //Send request
        DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
        wr.writeBytes (parameters);
        wr.flush ();
        wr.close ();

        //Get Response
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();

       return response.toString();
        
	}
	
	
	/**
	 * Return the first occurrence of the number in a String. If not found, an exception is thrown.
	 * @param text String containing the number
	 * @return found number
	 */
	public static int getIntFromString(String text){
		
		String regExp = "\\d+";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(text);
		
		m.find();
		String no = m.group();
		
		return Integer.parseInt(no);
		
	}
	
	/**
	 * Transform  a name of the card containing more spaces than standard
	 * to normalized format. E.g. " Flames     of   Firebrand" to "Flames of Firebrand".
	 * @param cardName
	 * @return
	 */
	public static String normalizeCardName(String cardName){
		//Trim and split it.
		String[] nameParts = cardName.trim().split("\\s+");
		String newCardName = nameParts[0];
		
		//No need for string builder - card names do not consist of many words.
		for (int i = 1; i < nameParts.length; i++)
			newCardName += " " + nameParts[i];
		
		return newCardName;
	}
	
	
	
}
