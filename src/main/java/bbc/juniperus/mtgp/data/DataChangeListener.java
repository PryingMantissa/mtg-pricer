package bbc.juniperus.mtgp.data;

import java.util.Collection;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.Source;

public interface DataChangeListener {
	
	void resultAdded();
	void sourcesAdded(Collection<Source> sources);
	void cardAdded(Card card);
}
