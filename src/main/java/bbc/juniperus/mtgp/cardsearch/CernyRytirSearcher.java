package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

class CernyRytirSearcher extends Searcher{
	
	public static final int RESULT_PER_PAGE = 30;
	public static final String URL = "http://www.cernyrytir.cz/";
	public static final String NAME = "Cerny Rytir";
	public static Currency currency;
	
	/** Default constructor*/
	CernyRytirSearcher(){
		
		currency = Currency.getInstance("CZK");
	}

	/**
	 * Retrieves the list of cards that match the card name.
	 * @param cardName Name of the card to be found.
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<CardResult> getCardResults(String cardName) throws IOException{
		
		/** 1. load the cards from the first page (might be last as well)*/
		
		//Get the html result page from the query as String.
		String html =  getHTMLString(createURL(cardName,1));
		
		List<CardResult> foundCards = new ArrayList<CardResult>();
		
		//Add all results we found on the first page.
		foundCards.addAll(extractCardsFromHtml(html));
		
		
		/**2. check for additional pages and load results from them as well*/
		
		//Determine if there are also additional pages.
		Document doc = Jsoup.parse(html);
		Elements span = doc.select("span.kusovkytext");
		
		//If the special element exists, it has more pages -> calculate how many.
		if (span.size() > 0){
			int resultsCount  = (int) getDoubleFromString(span.text(),1);
			int pagesTotal = (int) Math.ceil((float) resultsCount / RESULT_PER_PAGE);
			
			//Load cards from other pages as well.
			for (int i =2; i <= pagesTotal; i++){
				html = getHTMLString(createURL(cardName,i));
				foundCards.addAll(extractCardsFromHtml(html));
			}
		}
		return foundCards;
	}
	
	
	/**
	 * Parses single html page and creates List of Card objects.
	 * @param html
	 * @return
	 */
	public List<CardResult> extractCardsFromHtml(String html){
		
		Document doc = Jsoup.parse(html);
		
		List<CardResult> foundCards = new ArrayList<CardResult>();
		
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
				foundCards.add(new CardResult(name,type, edition, 
						getDoubleFromString(price,1),new Source(NAME),new Date(), currency));
			}
		}
		
		return foundCards;
	}
	
	
	private String createURL(String cardName, int page){
		
		final String addressCR = "http://www.cernyrytir.cz/index.php3";
		final String urlParam = "akce=3&"
				
				+ "limit="+ (page-1) * RESULT_PER_PAGE
				+ "&jmenokarty="+ cardName.replace(" ", "+")
				
				+ "&edice_magic=libovolna&poczob=30&foil=A&"
				+ "triditpodle=ceny&hledej_pouze_magic=1&submit=Vyhledej";
		
		return addressCR + "?" + urlParam;
	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}
	
	

	
}
