package bbc.juniperus.mtgp.cardsearch;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public interface ProgressListener {
	
	void cardSearched(Card card, CardResult result, Searcher searcher);
	
}
