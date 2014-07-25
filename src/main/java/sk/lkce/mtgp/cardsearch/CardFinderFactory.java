package sk.lkce.mtgp.cardsearch;

import java.util.ArrayList;
import java.util.List;


/**
 * Factory which provides static methods to retrieve varius {@link CardFinder} implementations.
 */
public class CardFinderFactory {

	private static CernyRytirCardFinder cernyRytir;
	private static ModraVeverickaCardFinder modraVevericka;
	private static DragonHostCardFinder dragon;
	
	
	/**
	 * Returns new <code>List</code> with all the card finders be provided by this factory.
	 * @return
	 */
	public static List<CardFinder> allCardFinders(){

		List<CardFinder> finders = new ArrayList<>();
		finders.add(getCernyRytirPricer());
		finders.add(getDragonPricer());
		finders.add(getModraVeverickaPricer());
		
		return finders;
	}
	
	
	/**
	 * Returns single shared instance of {@link CernyRytirCardFinder}
	 * @return shared singleton instance of {@link CernyRytirCardFinder}
	 */
	public static CardFinder getCernyRytirPricer(){
		if (cernyRytir == null)
			cernyRytir = new CernyRytirCardFinder();
		return cernyRytir;
	}
	
	/**
	 * Returns single shared instance of {@link ModraVeverickaCardFinder}
	 * @return shared singleton instance of {@link ModraVeverickaCardFinder}
	 */
	public static CardFinder getModraVeverickaPricer(){
		if (modraVevericka == null)
			modraVevericka = new ModraVeverickaCardFinder();
		return modraVevericka;
	}
	
	/**
	 * Returns single shared instance of {@link DragonCardFinder}
	 * @return shared singleton instance of {@link DragonCardFinder}
	 */
	public static CardFinder getDragonPricer(){
		if (dragon == null)
			dragon = new DragonHostCardFinder();
		return dragon;
	}

}
