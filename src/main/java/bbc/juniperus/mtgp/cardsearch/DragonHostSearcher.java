package bbc.juniperus.mtgp.cardsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bbc.juniperus.mtgp.domain.CardResult;

public class DragonHostSearcher extends Searcher {
	public static final int RESULT_PER_PAGE = 120;
	public static final String URL = "http://shop.dragonhost.eu/";
	public static final String NAME = "Draco";
	public static Currency currency;
	
	public DragonHostSearcher() {
		currency = Currency.getInstance("EUR");
	}
	

	@Override
	List<CardResult> getCardResults(String normalizedCardName) throws IOException {
		
		System.out.println("Looking for " + normalizedCardName);
		
		String html = getHTMLString(getQueryUrl(normalizedCardName,1));
		
		//Parse the first page.
		List<CardResult> results = extractCardsFromHtml(html);
		
		//Find out how many pages of results there are.
		int resultsCount = getResultsCountFromHtml(html);
		
		if (resultsCount == 0)
			return null;
		
		int pagesTotal = (int) Math.ceil((float) resultsCount/ RESULT_PER_PAGE);
		
		//Load other pages.
		if (pagesTotal > 1)
			for (int i = 2; i <= pagesTotal;i++){
				html = getHTMLString(getQueryUrl(normalizedCardName,i));
				results.addAll(extractCardsFromHtml(html));
			}
		
		return results;
	}

	
	
	private int getResultsCountFromHtml(String html){
		Document doc = Jsoup.parse(html);
		
		Elements els = doc.select("div.category-products").select("p.amount");

		
		if (els.size() == 0)
			return 0;
		
		
		System.out.println(els.size());
		System.out.println(els.html());
		
		String content = null;
		
		//Get the first one - in case there is top and bottom toolbar
		//otherwise the combination of text in these elements would be returned.
		content = els.get(0).text();
		
		System.out.println("content is " + content);
		//Position of the number in the inner text of element which 
		//describes number of results.
		int position = 1;
		
		//If there are <= 120 results the format is : Results: 19
		//If > 120 the format is Results: 1 to 120 fomr 129. 
		if (countRegexMatches(content, "[\\d]+") > 1)
			position =3;
				
		//First get number at first position (if there are less than 120 results
		//there is only one number)
		int result =(int) getDoubleFromString(content, position);
		return result;
	}
	
	private List<CardResult> extractCardsFromHtml(String html){
		
		Document doc = Jsoup.parse(html);
		List<CardResult> foundCards = new ArrayList<CardResult>();

		Elements resultRows = doc.select("div.col-main").select("li.item");
		
		String name = null; 
		String edition = "N/A";
		String type = "N/A";
		String price = null;
		
		for (int i = 0; i < resultRows.size(); i++){
			Element card = resultRows.get(i);
			
			name = card.select("h2.product-name a").text();
			price = card.select("span.price").text();
			foundCards.add(new CardResult(name,type, edition, 
					getDoubleFromString(price,1),NAME,new Date(), currency));
			
		}
		
		return foundCards;
	}
	
	
	
	private String getQueryUrl(String cardName, int page){
		//Max limit allowed seems to be 120.No matter if higher humber is entered.
		final String  queryString="catalogsearch/result/index/?limit=120&"
				+ "p=" + page + "&" 
				+ "q=" +cardName.replace(" ", "+");;
		
		System.out.println(URL + queryString);
		return URL + queryString;
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
	public String getCurrency() {
		return currency.getSymbol();
	}
	
	//Testing main
	public static void main(String[] args) throws IOException{
		
		
		DragonHostSearcher pc = new DragonHostSearcher();
		
		List<CardResult> l = pc.getCardResults("Enlarge");
		System.out.println(l.size());
		System.out.println(l);
		
		/*
		String text ="78987 ewew 7897  eew 1212";
		String regExp = "[\\d.,]+";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(text);
		
		for ( int i =0; i <;i++)
			m.find();
		
		String no = m.group().replace(",",".");
		
		System.out.println(no);
		*/
		
	}
}
