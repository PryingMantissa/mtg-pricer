package bbc.juniperus.mtgp.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Deck {
	
	//Any sequence of letter,',-,/ or white space (includes leading and trailing white spaces).
	public final static String REG_EXP_NAME = "[a-zA-Z\\s'-/]+";
	public final static String REG_EXP_NUMBER = "\\d+";
	
	private List<DeckCard> cards = new ArrayList<DeckCard>();
	
	/**
	 * Parses the file on the given path and stores parsed cards.
	 * @param path location of the file
	 * @throws IOException
	 * @throws ParseException
	 */
	public void readFromFile(File f) throws IOException, ParseException{
		
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(f));
	
		int lineCounter=0;
		String line;
		while ((line =reader.readLine()) != null){
			lineCounter++;
			//Ignore empty lines
			if (line.trim().equals(""))
				continue;
			
			DeckCard card = Deck.parseLine(line);
			//If the line could not be parsed.
			if (card == null){
				reader.close();
				throw new ParseException("Failed to parse the line " + lineCounter +
											" - '" + line +"'. The line has to contain expressions matching"+
											REG_EXP_NUMBER + "(number) and " + REG_EXP_NAME +
											"(name of the card)",0);
			}
			cards.add(card);
		}
		reader.close();
	}
	
	/**
	 * Utility method. Parses String and creates {@link DeckCard}.
	 * @param line <code>String<code> to be parsed
	 * @return new <code>DeckCard</code> object
	 */
	public static DeckCard parseLine(String line){
		
		Pattern patName = Pattern.compile(REG_EXP_NAME);
		Pattern patNumber = Pattern.compile(REG_EXP_NUMBER);
		
		Matcher mName = patName.matcher(line);
		Matcher mNumber = patNumber.matcher(line);
		
		String name = null;
		String quantity = null;
		
		if (mName.find())
			//Trim leading and trailing \w.
			name = mName.group().trim();
		
		if (mNumber.find())
			quantity = mNumber.group();

		if (name == null || quantity == null)
			return null;
		
		//In case of split cards are written in bad format.
		name = name.replace("/", " // ");
		
		//Quantity is assumed to be parsable to int -> guaranteed by reg exp.
		return new DeckCard(name, Integer.parseInt(quantity));
	}
	
	/**
	 * Returns  <code>List</code> containing <code>Deck</code>'s  {@link DeckCard} objects.
	 * @return unmodifiable <code>List</code>
	 */
	public List<DeckCard> getCards(){
		return Collections.unmodifiableList(cards);
	}
	
	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		for (DeckCard card : cards)
			sb.append(card + "\n");
			
		return sb.toString();
	}
	
	
	
}
