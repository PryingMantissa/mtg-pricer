package bbc.juniperus.mtgp.deckpricing;

import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public interface SearchProgressListener {
	
	void cardSearched(Card card, CardResult result, Searcher searcher);
	
}
