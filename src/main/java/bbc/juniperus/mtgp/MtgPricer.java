package bbc.juniperus.mtgp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MtgPricer {
	
	public final int resultsPerPageCR = 30;
	
	public static void main (String[] args) throws IOException{
		
		
		
		MtgPricer mp = new MtgPricer();
		
		
		Deck d = new Deck();
		d.readFromFile("d:\\deck.txt");
		mp.evalDeck(d);
		
		
		//System.out.println(mp.getFromCR("Forest"));
	}
	
	
	
	
	public void evalDeck(Deck deck){
		
		int priceTotal = 0;
		
		List<String> cardNames = deck.getCardNames();
		
		for (String name :cardNames){
			Card c = getFromCR(name);
			int q = deck.getQuantityOf(name);
			System.out.println(c + "\t" + q + " x " + c.getPrice() + " = " + q*c.getPrice());
			priceTotal +=q * c.getPrice();
		}
		
		System.out.println("\n*********\nTotal price " + priceTotal);
		
	}
	
	
	public Card getFromCR(String cardName){
		
		String html = null;
		try {
			html = getHTMLString(createCRURL(cardName,1));
		} catch (IOException e) {
			//TODO change to other exception
			throw new RuntimeException("Cannot get the html page from Cerny Rytir: " + e.getMessage());
		}
				
		
		
		int pagesTotal = 1;
		int page = 1;
		
		Document doc = Jsoup.parse(html);
		Elements span = doc.select("span.kusovkytext");
		//If the special element exists it has more pages -> calculate how many.
		if (span.size() > 0){
			int resultsCount  = getIntFromString(span.text());
			pagesTotal = (int) Math.ceil((float) resultsCount / resultsPerPageCR);
		}
		
		
		List<Card> cards = new ArrayList<Card>();
		
		
		do{
			//If not the first page is to be loaded.
			if (page >1)
				try {
					html = getHTMLString(createCRURL(cardName,page));
				} catch (IOException e) {
					//TODO change to other exception
					throw new RuntimeException("Cannot get the html page from Cerny Rytir: " + e.getMessage());
				}
			//Add the results.
			cards.addAll(getCardsFromHtmlFromCR(html));
			page++;
		}while ( page <= pagesTotal);
			
		
		if (cards.size() < 1)
			return null;
		
		//System.out.println("results for " + cardName + " found " + cards.size());
		
		//Find cheapest card.
		Card cheapest = cards.get(0);
		
		for (Card card : cards){
			if (card.getPrice() < cheapest.getPrice())
				cheapest = card;
		}
		
		return cheapest;
		
		
	}
	
	private String createCRURL(String cardName, int page){
		
		String[] strings = cardName.trim().split("\\s+");
		String modifiedName = strings[0];
		
		for (int i = 1; i < strings.length; i++)
			modifiedName += "+" + strings[i];
		
		
		final String addressCR = "http://www.cernyrytir.cz/index.php3";
		final String urlParam = "akce=3&"
				
				+ "limit="+ (page-1) * resultsPerPageCR
				+ "&jmenokarty="+ modifiedName
				
				+ "&edice_magic=libovolna&poczob=30&foil=A&"
				+ "triditpodle=ceny&hledej_pouze_magic=1&submit=Vyhledej";
		
		
		//System.out.println(addressCR + "?" + urlParam);
		
		return addressCR + "?" + urlParam;
	}
	
	
	public  List<Card> getCardsFromHtmlFromCR(String html){
		
		Document doc = Jsoup.parse(html);
		
		List<Card> foundCards = new ArrayList<Card>();
		
		
		
		//Find second table with kusovkytext class.
		Element table = doc.select("table.kusovkytext").get(1);
		Elements resultRows = table.select("tbody > tr");
		
		String name = null; 
		String edition = null;
		String type = null;
		String price = null;
		
		
		for (int i = 0; i < resultRows.size(); i++){
			
			int modRes = i % 3;
			
			if (modRes == 0)
				name = resultRows.get(i).select("td div font").text();
			else if (modRes == 1)
				edition = resultRows.get(i).select("td:eq(0)").text();
			//modRes == 2 -> last row
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
	
	
	
	
	
}
