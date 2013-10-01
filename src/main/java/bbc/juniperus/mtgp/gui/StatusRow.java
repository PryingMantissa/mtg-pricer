package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.ProgressListener;
import bbc.juniperus.mtgp.datastruc.DataModel;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;

public class StatusRow extends JPanel implements ProgressListener{

	
	private JProgressBar progressBar;
	private Pricer pricer;
	private JLabel info;
	private JLabel status;
	private JLabel name;
	private boolean started;
	private int cardsLeft;
	private double totalPrice;
	
	Border brdRight = BorderFactory.createEmptyBorder(0, 0, 0, 30);
	Border brdLeft = BorderFactory.createEmptyBorder(0, 30, 0, 0);
	
	private int t =5;
	Border border = BorderFactory.createEmptyBorder(t, t, t, t);
	
	private static final long serialVersionUID = 1L;

	public StatusRow(Pricer pricer, Searcher searcher){
		

		setBorder(border);
		this.pricer = pricer;
		MigLayout m =new MigLayout("ins 0");
		setLayout(m);
		pricer.addProgressListener(this, searcher);
		
		UIManager.put("ProgressBar.selectionForeground", Color.black);
		UIManager.put("ProgressBar.selectionBackground", Color.black);
		
		progressBar = new JProgressBar();
		progressBar.setString("Search will start shortly");
		//progressBar.setStringPainted(true);
		//progressBar.setForeground(Color.red);
		
		/*
		progressBar.setUI(new BasicProgressBarUI() {
		      protected Color getSelectionBackground() { return Color.black; }
		      protected Color getSelectionForeground() { return Color.white; }
		    });
		*/
	
		//progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD));
		status = new JLabel("Search will begin shortly");
		status.setFont(status.getFont().deriveFont((float)11.0));
		name = new JLabel(searcher.getName());
		name.setFont(name.getFont().deriveFont(Font.BOLD));
		add(name,"wrap");
		add(status,"wrap");
		add(progressBar,"width 200:200:200, height 17:17:17");
	}

	
	@Override
	public void cardSearchStarted(final Card card, Searcher searcher) {
		if (!started){
			started = true;
			cardsLeft = pricer.getCardListSize();
			progressBar.setMaximum(cardsLeft);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				status.setText("Looking for " + card.getName());
			}
		});
	
		
	}

	@Override
	public void cardSearchEnded(CardResult result, final Searcher searcher) {
		progressBar.setValue(progressBar.getValue()+1);
		cardsLeft--;
		
		if (cardsLeft < 1){
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					remove(progressBar);
					status.setText("Total price 100 EUR");
					
				}
				
			});
			
				
		}
	}
}
