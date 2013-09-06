package bbc.juniperus.mtgp.deckpricing;

import bbc.juniperus.mtgp.domain.Card;

public interface CardProgressListener {
	
	void cardEvaluated(Card card);
	
}
