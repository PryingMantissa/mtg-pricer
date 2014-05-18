package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import com.sun.naming.internal.ResourceManager;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;

@SuppressWarnings("serial")
public class CardFindersPane extends JPanel{
	
	private static final int TOTAL_WIDTH = 170;
	
	private Map<JCheckBox, CardFinder> checkBoxMap = new HashMap<>();
	private Controller controller;
	private JLabel headLabelLeft;
	private JLabel headLabelRight;
	private JPanel header;
	private JPanel body;
	private static final int BORDER_WIDTH = 5;
	private final static Color BORDER_COLOR = new Color(190,190,190);
	private final static Border PADDING_BORDER = BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH - 3, BORDER_WIDTH);
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
		header = new JPanel(new BorderLayout());
		headLabelLeft = new JLabel();
		headLabelRight = new JLabel();
		header.add(headLabelLeft, BorderLayout.WEST);
		header.add(headLabelRight, BorderLayout.EAST);
		
		header.setOpaque(true);
		//header.setBackground(Color.blue);
		
		//Fix the size of the header to 25 px so it does not change size with differen icons.
		header.setPreferredSize(new Dimension(header.getPreferredSize().width,25));
		
		header.setBorder(HEADER_BORDER);
		body.setBackground(Color.white);
		add(body);
		add(header, BorderLayout.NORTH);
	}
	
	
	@Override
	public Dimension getPreferredSize(){
		Dimension dim = super.getPreferredSize();
		dim.width = TOTAL_WIDTH;
		return dim;
	}

	public void showFinderSettings(){
		
		headLabelLeft.setText("<html><b> Card pricing sources</b></html>");
		headLabelRight.setIcon(ResourceLoader.ICON_SEARCH_SETTINGS);
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
		headLabelLeft.setText("<html><b>Search in progress</b></html>");
		headLabelRight.setIcon(ResourceLoader.ICON_LOADING);
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
		headLabelLeft.setText("<html><b>Stopping the search</b></html>");
		headLabelRight.setIcon(ResourceLoader.ICON_STOPPING);
	}
	
	public void displaySearchStopped(){
		headLabelLeft.setText("<html><b>Search stopped by user</b></html>");
		headLabelRight.setIcon(ResourceLoader.ICON_SEARCH_FINISHED);
	}
	
	public void displaySearchFinished(){
		headLabelLeft.setText("<html><b>Search finished</b></html>");
		headLabelRight.setIcon(ResourceLoader.ICON_SEARCH_FINISHED);
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
