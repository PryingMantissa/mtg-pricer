package bbc.juniperus.mtgp.cardpricing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbc.juniperus.mtgp.domain.Card;

public abstract class CardPricer {
	
	
	/**
	 * Retrieves the list of cards that match the card name.
	 * @param cardName Name of the card to be found.
	 * @return List of found cards.
	 * @throws IOException
	 */
	abstract List<Card> getCardResults(String normalizedCardName) throws IOException;
	
	public abstract String getURL();
	public abstract String getName();
	public abstract String getCurrency();
	
	
	/**
	 * Finds the cheapest match for the card.
	 * @param cardName
	 * @return
	 * @throws IOException
	 */
	public Card findCheapestCard(String cardName) throws IOException{
		
		String normalizedCardName = normalizeCardName(cardName);
		List<Card> foundCards = getCardResults(normalizedCardName);
		
		/* Remove cards which does not exactly match the name
		 * e.g. Mountain search return Goblin Mountaineer as well.
		 * Also foil version of cards (Mountain - foil) will be removed (they are more expensive anyway).
		 */
		
		normalizedCardName = normalizedCardName.replaceAll("[`´]", "'"); //Be sure to have "'" instead of "`"  and "´"
		//so it can be compared.
		
		
		if (foundCards == null || foundCards.size() < 1)
			return null;
		
		
		for (Card card: new ArrayList<Card>(foundCards))
			if (!card.getName().equalsIgnoreCase(normalizedCardName))
				foundCards.remove(card);
		
		//If all the results have been filtered out.
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
	 * Retrieves html document in form of String for given URL.
	 * @param address URL in from of String
	 * @return String of retrieved html document
	 * @throws IOException
	 */
	static String getHTMLString(String address) throws IOException{
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
	
	
	
	
	
	
	/**
	 * Return the first occurrence of the number in a String. If not found, an exception is thrown.
	 * @param text String containing the number
	 * @return found number
	 */
	static double getDoubleFromString(String text, int position){
		
		String regex = "[\\d.,]+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		
		if (position != 1){
			System.out.println(position);
			System.out.println(text);
		}
		
		for (int i =0; i < position;i++)
			m.find();
		String no = m.group().replace(",",".");
		
		return Double.parseDouble(no);
		
	}
	
	
	static int countRegexMatches(String text, String regex){
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		int counter =0;
		
		while(m.find())
			counter++;
		
		return counter;
	}
	
	
	/**
	 * Transform  a name of the card containing more spaces than standard
	 * to normalized format. E.g. " Flames     of   Firebrand" to "Flames of Firebrand".
	 * @param cardName
	 * @return
	 */
	static String normalizeCardName(String cardName){
		//Trim and split it.
		String[] nameParts = cardName.trim().split("\\s+");
		String newCardName = nameParts[0];
		
		//No need for string builder - card names do not consist of many words.
		for (int i = 1; i < nameParts.length; i++)
			newCardName += " " + nameParts[i];
		
		return newCardName;
	}
}
