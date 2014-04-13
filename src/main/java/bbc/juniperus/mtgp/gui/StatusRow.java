package bbc.juniperus.mtgp.gui;


import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchResults;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public class StatusRow extends JPanel implements SearchObserver{

	
	private JProgressBar progressBar;
	private SearchExecutor pricer;
	private JLabel lblFoundNumber;
	private JLabel lblPrice;
	private JLabel lblName;
	private boolean started;
	private int cardsLeft;
	
	Border brdRight = BorderFactory.createEmptyBorder(0, 0, 0, 30);
	Border brdLeft = BorderFactory.createEmptyBorder(0, 30, 0, 0);
	
	private int t =5;
	Border border = BorderFactory.createEmptyBorder(t, t, t, t);
	
	private static final long serialVersionUID = 1L;

	public StatusRow(SearchExecutor pricer, CardFinder searcher){
		
		//setBackground(Color.green);
		setBorder(border);
		this.pricer = pricer;
		MigLayout m =new MigLayout("ins 0");
		setLayout(m);
		pricer.addSearchObserver(this, searcher);
		
	
		progressBar = new JProgressBar();

		lblPrice = new JLabel("Search will begin shortly");
		lblPrice.setFont(lblPrice.getFont().deriveFont((float)11.0));

		lblName = new JLabel(searcher.getName());
		lblName.setFont(lblName.getFont().deriveFont(Font.BOLD));
		lblFoundNumber = new JLabel();
		lblFoundNumber.setVisible(false);
		
		add(lblName,"wrap");
		add(lblFoundNumber,"wrap");
		add(lblPrice,"width 170:170:170, wrap");
		add(progressBar,"width 150:150:150");
	}

	
	@Override
	public void startedSearchingForCard(final Card card, CardFinder searcher) {
		if (!started){
			started = true;
			cardsLeft = pricer.getCardListSize();
			progressBar.setMaximum(cardsLeft);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				lblPrice.setText(card.getName());
			}
		});
	}

	@Override
	public void finishedSearchingFor(CardResult result, final CardFinder searcher) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				progressBar.setValue(progressBar.getValue()+1);
				cardsLeft--;
			}
		});
	}


	@Override
	public void searchingFinished(final CardFinder searcher, final SearchResults data) {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				remove(progressBar);
				int notFound = data.getNotFoundCount();
				
				if (notFound == 0){
					lblFoundNumber.setForeground(new Color(0x009933));
					lblFoundNumber.setText("Found all cards");
				}else{
					lblFoundNumber.setForeground(Color.RED);
					lblFoundNumber.setText("Not found " +notFound + " card(s)");
				}
				
				
				lblFoundNumber.setVisible(true);
				lblPrice.setText("Total price: " + data.getTotalPriceString());
				repaint();
				//revalidate();
			}
			
		});
		
	}

	@Override
	public void searchingFailed(CardFinder searcher, Throwable t) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void searchingFinished(boolean interrupted) {
		// TODO Auto-generated method stub
		
	}
}
