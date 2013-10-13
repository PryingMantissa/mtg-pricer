package bbc.juniperus.mtgp.cardsearch;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public interface ProgressListener {
	
	void startedSearchingFor(Card card, Searcher searcher);
	void finishedSearchingFor(CardResult result, Searcher searcher);
	void finishedSearch(Searcher searcher, Pricer.HarvestData data);
	
}
