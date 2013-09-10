package bbc.juniperus.mtgp.deckpricing;

import bbc.juniperus.mtgp.domain.CardResult;

public interface CardPricingProgressListener {
	
	void cardEvaluated(CardResult card);
	
}
