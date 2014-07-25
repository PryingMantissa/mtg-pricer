package sk.lkce.mtgp.cardsearch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sk.lkce.mtgp.domain.CardResult;

/**
 * Implementation of {@link CardFinder} html scrapper for web page <b>http://www.modravevericka.sk/</b>.
 *
 */
class ModraVeverickaCardFinder extends CardFinder{

	//public static final int RESULT_PER_PAGE = 50;
	public static final String URL = "http://www.modravevericka.sk/";
	public static final String NAME = "Modra Vevericka";
	public static Currency currency;
	
	public ModraVeverickaCardFinder() {
		currency = Currency.getInstance("EUR");
	}
	

	@Override
	List<CardResult> getCardResults(String normalizedCardName) throws IOException {
		String html = getHTMLString(getQueryUrl(normalizedCardName));
		return extractCardsFromHtml(html);
	}

	
	private List<CardResult> extractCardsFromHtml(String html){
		
		Document doc = Jsoup.parse(html);
		List<CardResult> foundCards = new ArrayList<CardResult>();

		Elements resultRows = doc.select("#card_list").select("div.card");
		
		String name = null; 
		String edition = "N/Atyrrtyr";
		String type = "N/A";
		String price = null;
		
		

		
		for (int i = 0; i < resultRows.size(); i++){
			
			Element card = resultRows.get(i);
			name = card.select("div.name a").text();
			price = card.select("div.price").text();
			foundCards.add(new CardResult(name,type, edition, 
										getDoubleFromString(price,1), currency));
		}
		
		
		return foundCards;
	}
	
	
	
	private String getQueryUrl(String cardName){
		//Number of page size specified in URL - 10000.
		final String  queryString="x-cards,x-page-1-size-10000-order-name-asc.html?onclick=run_shopping_assistant&"
				+ "filter_name=" + cardName.replace(" ", "+");
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
	public Currency getCurrency() {
		return currency;
	}
	
	@Override
	public java.net.URL getURLForCard(String cardName) {
		String normalizedName = normalizeCardName(cardName);
		java.net.URL url;
		try {
			url = new java.net.URL(getQueryUrl(normalizedName));
		} catch (MalformedURLException e) {
			//This should not happen. Re-throw it anyway.
			throw new RuntimeException(e);
		}
		return url;
	}
	

}
