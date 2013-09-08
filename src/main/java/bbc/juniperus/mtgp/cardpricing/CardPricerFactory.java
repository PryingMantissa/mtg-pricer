package bbc.juniperus.mtgp.cardpricing;


public class CardPricerFactory {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static CardPricer getCernyRytirPricer(){
		return new CernyRytirCardPricer();
	}
	
	public static CardPricer getModraVeverickaPricer(){
		return new ModraVeverickaCardPricer();
	}
	
	public static CardPricer getDragonPricer(){
		return  new DragonHostCardPricer();
	}

}
