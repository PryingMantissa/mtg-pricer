package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardParser;
import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.SearcherFactory;
import bbc.juniperus.mtgp.data.MtgTableModel;
import bbc.juniperus.mtgp.data.viewmodel.ReportCreator;
import bbc.juniperus.mtgp.domain.Card;

public class Main implements PropertyChangeListener {
	
	private JFrame window;
	private JPanel tablePane;
	private CardsView view;
	private JPanel leftPane;
	
	private JTextField addTextField;
	private JSpinner addSpinner;
	
	
	private Pricer pricer;
	private Map<JCheckBox,Searcher> checkBoxes = new LinkedHashMap<JCheckBox,Searcher>();
	private boolean afterSearch;
	
	
	private Map<Class<? extends AbstractAction>,AbstractAction> actionMap 
					= new HashMap<Class<? extends AbstractAction>,AbstractAction>();
	
	private static Searcher[] allSearchers = SearcherFactory.getAll();
	
	private static int h = 20;
	private static int w = 20;
	static final ImageIcon iconAdd = loadIcon("/icons/014.png",w,h);
	static final ImageIcon iconRemove = loadIcon("/icons/013.png",w,h);
	static final ImageIcon iconImport = loadIcon("/icons/083.png",w,h);
	static final ImageIcon iconExport = loadIcon("/icons/103.png",w,h);
	static final ImageIcon iconSave = loadIcon("/icons/095.png",w,h);
	static Border etchedBorder = BorderFactory.createEtchedBorder();
	
	
	public Main(){
		createActions();
		setupGui();
		window.setVisible(true);
		
		startNewPricing();
		/*
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				testView();
			}
			
		});
		*/
	}
	
	private void setupGui(){
		setLookAndFeel();
		window = new JFrame();
		window.setTitle("Mtg pricer");
		window.setSize(700, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		
		tablePane = new JPanel(new BorderLayout());
		tablePane.setBorder(etchedBorder);
		
		window.add(createToolBar(), BorderLayout.NORTH);
		window.add(createSearchersPane(), BorderLayout.WEST);
		window.add(tablePane, BorderLayout.CENTER);
		window.setJMenuBar(createMenuBar());
	}
	
	private Component createToolBar(){
		JToolBar tb = new JToolBar();
		
		tb.setFocusable(false);
		tb.add(actionMap.get(ImportCardsAction.class));
		tb.addSeparator(new Dimension(10,20));
		tb.add(actionMap.get(ExportTableCsvAction.class));
		tb.addSeparator(new Dimension(10,20));

		addTextField = new JTextField(10);
		addTextField.setMaximumSize(addTextField.getPreferredSize());
		tb.add(addTextField);
		
		addSpinner = new JSpinner();
		addSpinner.setModel(new SpinnerNumberModel(1,1,99,1));
		addSpinner.setEditor(new JSpinner.NumberEditor(addSpinner,"##"));
		JFormattedTextField txt = ((JSpinner.NumberEditor) addSpinner.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		addSpinner.setPreferredSize(new Dimension(35,addSpinner.getPreferredSize().height));
		addSpinner.setMaximumSize(addSpinner.getPreferredSize());
		tb.add(addSpinner);
		tb.add(actionMap.get(AddCardAction.class));
		tb.add(actionMap.get(RemoveAction.class));
		
		
		for (Component c : tb.getComponents())
			if (c instanceof AbstractButton)
				((AbstractButton)c).setFocusable(false);
		tb.setBorder(etchedBorder);
		return tb;
	}
	
	private JPanel createSearchersPane(){
		MigLayout ml = new MigLayout();
		leftPane = new JPanel(ml);
		JLabel lbl = new JLabel("Card pricing sources:");
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		leftPane.add(lbl,"wrap");
		
		for (Searcher s: allSearchers){
			lbl = new JLabel(s.getName());
			lbl.setToolTipText(s.getURL());
			leftPane.add(lbl);
			JCheckBox cb = new JCheckBox();
			cb.setSelected(true);
			leftPane.add(cb,"wrap");
			//Add ti to map
			checkBoxes.put(cb, s);
		}
		
		leftPane.setPreferredSize(new Dimension(180, leftPane.getPreferredSize().height));
		leftPane.setBorder(etchedBorder);
		return leftPane;
	}
	
	private void updateLeftPanel(final Collection<Searcher> ss){
		//Should run on  Swing thread.
		leftPane.removeAll();
		for (Searcher s: ss){
			StatusRow sp = new StatusRow(pricer,s);
			leftPane.add(sp,"wrap");
		}
		window.revalidate();
		window.repaint();
	}
	
	private void setLookAndFeel(){
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
			javax.swing.UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JMenuBar createMenuBar(){
		JMenuBar  menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();

		fileMenu = new JMenu("Search");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.setDisplayedMnemonicIndex(0);

		JMenuItem importMI = new JMenuItem(actionMap.get(ImportCardsAction.class));
		fileMenu.add(importMI);
		
		JMenu pricingMenu = new JMenu();

		pricingMenu = new JMenu("Edit");
		pricingMenu.setMnemonic(KeyEvent.VK_P);
		pricingMenu.setDisplayedMnemonicIndex(0);

		JMenuItem priceMI = new JMenuItem(actionMap.get(StartSearchAction.class));
		JMenuItem aa = new JMenuItem(new SearchInBrowserAction());
		pricingMenu.add(priceMI);
		pricingMenu.add(aa);
		
		menuBar.add(fileMenu);
		menuBar.add(pricingMenu);
		
		JMenu f2 = new JMenu("Export");
		JMenu f3= new JMenu("Other");
		JMenu f4= new JMenu("Help");
		
		menuBar.add(f2);
		//menuBar.add(f3);
		menuBar.add(f4);
		
		
		
		return menuBar;

	}
	
	private void createActions(){
		actionMap.put(ImportCardsAction.class, new ImportCardsAction());
		
		AbstractAction action = new StartSearchAction();
		//action.setEnabled(false);
		actionMap.put(StartSearchAction.class, action);
		action = new AddCardAction();
		action.setEnabled(false);
		actionMap.put(AddCardAction.class, action);
		action = new RemoveAction();
		action.setEnabled(false);
		actionMap.put(RemoveAction.class, action);
		action = new ExportTableCsvAction();
		action.setEnabled(false);
		actionMap.put(ExportTableCsvAction.class, action);
	}
	
	
	private void setEditable(boolean editable){
		actionMap.get(ImportCardsAction.class).setEnabled(editable);
		actionMap.get(RemoveAction.class).setEnabled(editable);
		actionMap.get(AddCardAction.class).setEnabled(editable);
	}
	
	
	//TODO used?
	private void setPricingActionsEnabled(boolean enabled){
		actionMap.get(AddCardAction.class).setEnabled(enabled);
		actionMap.get(ExportTableCsvAction.class).setEnabled(enabled);
	}
	
	
	private void startNewPricing(){
		pricer = new Pricer();
		tablePane.removeAll();
		MtgTableModel model = new MtgTableModel(pricer.data());
		view = new CardsView(model);
		view.addPropertyChangeListener(this);
		tablePane.add(view);
		window.revalidate();
		setPricingActionsEnabled(true);
	}
	
	
	private Collection<Searcher> getSelectedSearchers(){
		List<Searcher> selected = new ArrayList<Searcher>(); 
		for (JCheckBox cb : checkBoxes.keySet())
			if (cb.isSelected())
				selected.add(checkBoxes.get(cb));
		return selected;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CardsView.GRID_SELECTED_PROPERTY){
			boolean enabled =  (boolean) evt.getNewValue();
			actionMap.get(RemoveAction.class).setEnabled(enabled);
		}
	}

	private static ImageIcon loadIcon(String path, int width, int height){
		URL url = Main.class.getResource(path);
		ImageIcon icon = new ImageIcon(url);
		Image img = icon.getImage();
		img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		icon.setImage(img);
		return icon;
	}
	
	private void reportError(String text){
		JOptionPane.showMessageDialog(window, text ,"Sorry...",JOptionPane.ERROR_MESSAGE);
	}
	
	
	private void addCard(Card card, int quantity){
		if (pricer.containsCard(card)){
			String text = card.getName() + " is already in the card list.\n"
					+ "Should the quantity of the "
					+ "card be incremented by " + quantity + " ?";
			String title = "Duplicity detected"; 
			int response = JOptionPane.showConfirmDialog(window, text,
					title, JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION)
				return;
		}
		pricer.addCard(card, quantity);
	}
	
	
	//============================ Devel ========================================
	
	public static void main(String[] args){
		new Main();
	}
	
	
	//=================== Actions ===================================================
	
	private class NewSearchAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		NewSearchAction(){
			super("NewSearchAction");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			startNewPricing();
		}
		
	}
	
	private class AddCardAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		AddCardAction(){
			super("Add",iconAdd);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cardName =  addTextField.getText();
			int quantity = (int) addSpinner.getValue();
			addCard(new Card(cardName), quantity);
		}
		
	}
	
	private class RemoveAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;

		RemoveAction(){
			super("Delete",iconRemove);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Removing");
			pricer.removeCards(view.getSelectedCards());
		}
		
	}
	
	private class ImportCardsAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		ImportCardsAction(){
			super("Import card list",iconImport);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			final JFileChooser fc = new JFileChooser();
			fc.showOpenDialog(window);
			File f = fc.getSelectedFile();
			
			if (f == null)
				return;
			
			Map<Card, Integer> m = null;
			try{ 
				CardParser cp = new CardParser();
				m = cp.parseFromFile(f);
			} catch (IOException e) {
				reportError("An expception ocurred while "
						+ "attempting to read from the file\n " + f.getAbsolutePath()
						+ "\n\n"
						+ e.getMessage());
				e.printStackTrace();
			} catch (ParseException e) {
				reportError(e.getMessage());
				e.printStackTrace();
			}
			
			if (m == null)
				return;
			
			for (Card c : m.keySet()){
				addCard(c, m.get(c));
			}
		}	
	}
	
	private class ExportTableCsvAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		ExportTableCsvAction(){
			super("Export to .csv");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {		
			JFileChooser chooser = new JFileChooser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String name = "exported-deck_" + sdf.format(new Date()) + ".csv";
			chooser.setSelectedFile(new File(name));
			chooser.showSaveDialog(window);
			File f = chooser.getSelectedFile();
			if (f == null)
				return;
			
			ReportCreator report = new ReportCreator(view.tableModel());
			try {
				FileWriter fw = new FileWriter(f);
				BufferedWriter  bw = new BufferedWriter(fw);
				bw.write(report.createCSVReport(","));
				bw.close();
			} catch (IOException ex) {
				reportError("An I/O exception occurred while writing to file\n" +
							f.getName() +
							"\n\n" +
							ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
	
	private class ExportTableTxtAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		ExportTableTxtAction(){
			super("Export to .txt");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String name = "exported-deck_" + sdf.format(new Date()) + ".txt";
			chooser.setSelectedFile(new File(name));
			chooser.showSaveDialog(window);
			File f = chooser.getSelectedFile();
			if (f == null)
				return;
			
			ReportCreator report = new ReportCreator(view.tableModel());
			try {
				FileWriter fw = new FileWriter(f);
				BufferedWriter  bw = new BufferedWriter(fw);
				bw.write(report.createCSVReport(","));
				bw.close();
			} catch (IOException ex) {
				reportError("An I/O exception occurred while writing to file\n" +
							f.getName() +
							"\n\n" +
							ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
	
	private class ExportCardListAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
	
	private class StartSearchAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		
		StartSearchAction(){
			super("Start search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			final Collection<Searcher> searchers = getSelectedSearchers(); 
			pricer.setSearchers(searchers);
			updateLeftPanel(searchers);
			pricer.runLookUp();
			window.pack();
		}
	}
	
	private class SearchInBrowserAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		SearchInBrowserAction(){
			super("Find via browser");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//TODO handle this somewhere. Not enable the action.
			assert Desktop.isDesktopSupported();
			
			Collection<Card> cards = view.getSelectedCards();
			Collection<Searcher> searchers = null;
			
			if (afterSearch)
				pricer.getSearchers();
			else
				searchers = getSelectedSearchers();
			
			for (Card c : cards)
				for (Searcher s : searchers){
					URL url = s.getURLForCard(c.getName());
					try {
						Desktop.getDesktop().browse(url.toURI());
					} catch (IOException | URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}


	
}
