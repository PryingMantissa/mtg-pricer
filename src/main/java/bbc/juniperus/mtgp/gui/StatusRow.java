package bbc.juniperus.mtgp.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.HarvestData;
import bbc.juniperus.mtgp.cardsearch.PricingWorker;
import bbc.juniperus.mtgp.cardsearch.SearchListener;
import bbc.juniperus.mtgp.cardsearch.finder.CardFinder;
import bbc.juniperus.mtgp.data.ResultsTableModel;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public class StatusRow extends JPanel implements SearchListener{

	
	private JProgressBar progressBar;
	private PricingWorker pricer;
	private JLabel lblFoundNumber;
	private JLabel lblPrice;
	private JLabel lblName;
	private boolean started;
	private int cardsLeft;
	private double totalPrice;
	
	Border brdRight = BorderFactory.createEmptyBorder(0, 0, 0, 30);
	Border brdLeft = BorderFactory.createEmptyBorder(0, 30, 0, 0);
	
	private int t =5;
	Border border = BorderFactory.createEmptyBorder(t, t, t, t);
	
	private static final long serialVersionUID = 1L;

	public StatusRow(PricingWorker pricer, CardFinder searcher){
		
		//setBackground(Color.green);
		setBorder(border);
		this.pricer = pricer;
		MigLayout m =new MigLayout("ins 0");
		setLayout(m);
		pricer.addProgressListener(this, searcher);
		
	
		progressBar = new JProgressBar();
		//progressBar.setStringPainted(true);
		//progressBar.setForeground(Color.red);
		
		/*
		progressBar.setUI(new BasicProgressBarUI() {
		      protected Color getSelectionBackground() { return Color.black; }
		      protected Color getSelectionForeground() { return Color.white; }
		    });
		*/
	
		//progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD));
		lblPrice = new JLabel("Search will begin shortly");
		lblPrice.setFont(lblPrice.getFont().deriveFont((float)11.0));
		
		/*lblPrice.setPreferredSize(
				new Dimension(100,lblPrice.getHeight()));*/
		lblName = new JLabel(searcher.getName());
		lblName.setFont(lblName.getFont().deriveFont(Font.BOLD));
		lblFoundNumber = new JLabel();
		lblFoundNumber.setVisible(false);
		
		add(lblName,"wrap");
		add(lblFoundNumber,"wrap");
		add(lblPrice,"width 150:150:150, wrap");
		add(progressBar,"width 150:150:150");
	}

	
	@Override
	public void startedSearchingFor(final Card card, CardFinder searcher) {
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
		progressBar.setValue(progressBar.getValue()+1);
		cardsLeft--;
	}


	@Override
	public void finishedSearch(final CardFinder searcher, final HarvestData data) {
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
	public void failedSearch(CardFinder searcher, Throwable t) {
		// TODO Auto-generated method stub
		
	}
}
