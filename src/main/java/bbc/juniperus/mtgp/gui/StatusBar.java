package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.ProgressListener;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public class StatusBar extends JPanel implements ProgressListener{

	
	JProgressBar progressBar;
	Pricer pricer;
	JLabel infoLbl;
	private boolean started;
	
	private static final long serialVersionUID = 1L;

	public StatusBar(Pricer pricer, Searcher searcher){
		this.pricer = pricer;
		setLayout(new BorderLayout());
		pricer.addProgressListener(this, searcher);
		JLabel name = new JLabel(searcher.getName());
		progressBar = new JProgressBar();
		infoLbl = new JLabel("Search will start shortly");
		
		add(name, BorderLayout.WEST);
		add(progressBar, BorderLayout.CENTER);
		add(infoLbl, BorderLayout.EAST);
		
	}


	@Override
	public void cardSearchStarted(Card card, Searcher searcher) {
		if (!started){
			started = true;
			progressBar.setMaximum(pricer.getCardListSize());
		}
		
		infoLbl.setText("Searchinf for: " + card.getName());
		
	}

	@Override
	public void cardSearchEnded(CardResult result, Searcher searcher) {
		progressBar.setValue(progressBar.getValue()+1);
		
	}
}
