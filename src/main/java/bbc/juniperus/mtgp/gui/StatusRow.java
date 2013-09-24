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
import javax.swing.border.Border;

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
		m.setColumnConstraints("[]20[]");
	//	m.setRowConstraints("");
		
	   // setLayout(gridBag);
		pricer.addProgressListener(this, searcher);
		
		name = new JLabel(searcher.getName());
		name.setFont(name.getFont().deriveFont(Font.BOLD));
		status = new JLabel("Starting");
		progressBar = new JProgressBar();
		info = new JLabel("Search will start shortly");
		
		//name.setBorder(BorderFactory.createLineBorder(Color.red));
		add(name);
		add(status);
		add(progressBar);
		add(info);
	}

	public int getNameLabelWidth(){
		return name.getPreferredSize().width;
	}
	
	public void setNameLabelWidth(int width){
		
		Dimension dim = name.getPreferredSize();
		dim.width = width;
		name.setPreferredSize(dim);
		
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
				status.setText("Downloading");
				info.setText(card.getName());
				
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
					status.setText("Total price ");
					remove(progressBar);
					info.setHorizontalAlignment(SwingConstants.RIGHT);
					info.setText(DataModel.formatDouble(totalPrice) +
								" " +searcher.getCurrency());
					
				}
				
			});
			
				
		}
		System.out.println(name.getPreferredSize());
		
	}
}
