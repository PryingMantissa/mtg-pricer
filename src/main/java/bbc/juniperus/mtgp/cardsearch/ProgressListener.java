package bbc.juniperus.mtgp.cardsearch;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public interface ProgressListener {
	
	void cardSearchStarted(Card card, Searcher searcher);
	void cardSearchEnded(CardResult result, Searcher searcher);
	
}
