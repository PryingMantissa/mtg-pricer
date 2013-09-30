package bbc.juniperus.mtgp.cardsearch;


public class SearcherFactory {

	public static final int CERNY_RYTIR = 777;
	public static final int MODRA_VEVERICKA = 778;
	public static final int DRAGON = 779;
	
	private static CernyRytirSearcher cernyRytir;
	private static ModraVeverickaSearcher modraVevericka;
	private static DragonHostSearcher dragon;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Searcher getSearcher(int searcherType){
		Searcher result = null;
	
		switch (searcherType) {
        case CERNY_RYTIR:		result = getCernyRytirPricer();
        						break;
        case MODRA_VEVERICKA:	result= getModraVeverickaPricer();
                 				break;
        case DRAGON:			result= getDragonPricer();
								break;    
		}
		
		return result;
	}
	
	public static Searcher[] getAll(){
		return new Searcher[]{getCernyRytirPricer(),getModraVeverickaPricer(),getDragonPricer()};
	}
	
	
	
	public static Searcher getCernyRytirPricer(){
		if (cernyRytir == null)
			cernyRytir = new CernyRytirSearcher();
		return cernyRytir;
	}
	
	public static Searcher getModraVeverickaPricer(){
		if (modraVevericka == null)
			modraVevericka = new ModraVeverickaSearcher();
		return modraVevericka;
	}
	
	public static Searcher getDragonPricer(){
		if (dragon == null)
			dragon = new DragonHostSearcher();
		return dragon;
	}

}
