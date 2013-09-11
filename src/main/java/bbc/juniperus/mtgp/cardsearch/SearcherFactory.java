package bbc.juniperus.mtgp.cardsearch;


public class SearcherFactory {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Searcher getCernyRytirPricer(){
		return new CernyRytirSearcher();
	}
	
	public static Searcher getModraVeverickaPricer(){
		return new ModraVeverickaSearcher();
	}
	
	public static Searcher getDragonPricer(){
		return  new DragonHostSearcher();
	}

}
