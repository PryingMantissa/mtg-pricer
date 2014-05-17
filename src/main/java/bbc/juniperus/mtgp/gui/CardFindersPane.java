package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;

@SuppressWarnings("serial")
public class CardFindersPane extends JPanel{
	
	
	private Map<JCheckBox, CardFinder> checkBoxMap = new HashMap<>();
	private Controller controller;
	private JLabel header;
	private JPanel body;
	private static final int WIDTH = 5;
	private final static Color BORDER_COLOR = new Color(190,190,190);
	private final static Border PADDING_BORDER = BorderFactory.createEmptyBorder(WIDTH, WIDTH, WIDTH - 3, WIDTH);
	private final static Border LINE_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR);
	private final static Border HEADER_BORDER = BorderFactory.createCompoundBorder(LINE_BORDER, PADDING_BORDER);
	private Color bcgColor = Color.white;
	
	
	public CardFindersPane(Controller controller){
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder());
		body = new JPanel();
		MigLayout mig = new MigLayout("gap 0"); //Specify no gaps
		body.setLayout(mig);
		this.controller = controller;
		//JLabel lbl = new JLabel(searcher.getName());
		//add(lbl, BorderLayout.NORTH);
		//add(new SearchThreadView(pricer,searcher), BorderLayout.CENTER);
		header = new JLabel();
		
		header.setOpaque(true);
		//header.setBackground(Color.blue);
		
		header.setBorder(HEADER_BORDER);
		body.setBackground(Color.white);
		add(body);
		add(header, BorderLayout.NORTH);
	}
	
	
	public void showFinderSettings(){
		
		header.setText("<html><b> Card pricing sources</b></html>");
		
		CheckBoxListener listener = new CheckBoxListener();
		checkBoxMap.clear();
		
		body.removeAll();
		for (CardFinder finder : controller.getCardFinders()){
			JCheckBox checkBox = new JCheckBox(finder.getName());
			
			if (controller.getPricingSettings().getFinders().contains(finder)) //Set selected it its part of the settings
				checkBox.setSelected(true);
			
			checkBox.addActionListener(listener);
			checkBoxMap.put(checkBox, finder);
			body.add(checkBox, "wrap");
			checkBox.setBackground(bcgColor);
		}
		
	}
	
	public void showSearchProgress(SearchExecutor searchExecutor){
		header.setText("<html><b>Search in progress</b></html>");
		body.removeAll();
		body.setLayout(new MigLayout()); //New Miglayout with gaps between rows.
		
		for (CardFinder finder : searchExecutor.getCardFinders()){
			SearchThreadProgressView view = new SearchThreadProgressView(finder);
			view.setBackground(bcgColor);
			searchExecutor.addSearchObserver(view);
			body.add(view, "wrap");
		}
		body.revalidate();
	}
	
	public void displayStoppingSearch(){
		header.setText("<html ><b>Stopping the search...</b></html>");
	}
	
	public void displaySearchStopped(){
		header.setText("<html><b>Search stopped by user</b></html>");
	}
	
	public void displaySearchFinished(){
		header.setText("<html><b>Search succesfully finished</b></html>");
		body.revalidate();
	}
	
	
	private class CheckBoxListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			
			controller.setFinderEnabled(checkBoxMap.get(checkBox), checkBox.isSelected());
		}
		
	}
	
	
}
