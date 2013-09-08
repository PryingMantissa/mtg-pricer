package bbc.juniperus.mtgp.cardpricing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bbc.juniperus.mtgp.domain.Card;

public class ModraVeverickaCardPricer extends CardPricer{

	//public static final int RESULT_PER_PAGE = 50;
	public static final String URL = "http://www.modravevericka.sk/";
	public static final String NAME = "Modra Vevericka";
	public static Currency currency;
	
	public ModraVeverickaCardPricer() {
		currency = Currency.getInstance("EUR");
	}
	

	@Override
	List<Card> getCardResults(String normalizedCardName) throws IOException {
		String html = getHTMLString(getQueryUrl(normalizedCardName));
		return extractCardsFromHtml(html);
	}

	
	private List<Card> extractCardsFromHtml(String html){
		
		Document doc = Jsoup.parse(html);
		List<Card> foundCards = new ArrayList<Card>();

		Elements resultRows = doc.select("#card_list").select("div.card");
		
		String name = null; 
		String edition = "N/A";
		String type = "N/A";
		String price = null;
		
		for (int i = 0; i < resultRows.size(); i++){
			Element card = resultRows.get(i);
			name = card.select("div.name a").text();
			price = card.select("div.price").text();
			foundCards.add(new Card(name,type, edition, getDoubleFromString(price,1)));
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
	public String getCurrency() {
		return currency.getSymbol();
	}
	
	//Testing main
	public static void main(String[] args) throws IOException{
		
		ModraVeverickaCardPricer pc = new ModraVeverickaCardPricer();
		System.out.println(pc.getCardResults("Goblin"));
	}
}
