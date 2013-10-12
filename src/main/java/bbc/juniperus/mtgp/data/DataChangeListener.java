package bbc.juniperus.mtgp.data;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.Source;

public interface DataChangeListener {
	
	void resultAdded();
	void sourceAdded(Source s);
	void cardAdded(Card card);
}
