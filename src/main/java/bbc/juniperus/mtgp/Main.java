package bbc.juniperus.mtgp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardFinderFactory;
import bbc.juniperus.mtgp.cardsearch.CardParser;
import bbc.juniperus.mtgp.cardsearch.SearchResults;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.gui.AboutDialog;
import bbc.juniperus.mtgp.gui.CardsView;
import bbc.juniperus.mtgp.gui.QuantitySpinner;
import bbc.juniperus.mtgp.gui.StatusRow;
import bbc.juniperus.mtgp.tablemodel.MtgPricerTableModel;
import bbc.juniperus.mtgp.tablemodel.ReportCreator;

/**
 * The main class and the app entry point.
 */
public class Main implements PropertyChangeListener, SearchObserver {
	
	private static int ICON_HEIGHT = 20;
	private static int ICON_WIDTH = 20;
	private static final ImageIcon ICON_ADD = loadIcon("/icons/file_add.png",ICON_WIDTH,ICON_HEIGHT);
	private static final ImageIcon ICON_REMOVE = loadIcon("/icons/file_delete2.png",ICON_WIDTH,ICON_HEIGHT);
	//Slightly thinner as its not symmetric.
	private static final ImageIcon ICON_IMPORT = loadIcon("/icons/import1.png",ICON_WIDTH-1,ICON_HEIGHT);
	/*
	private static final ImageIcon ICON_EXPORT = loadIcon("/icons/103.png",ICON_WIDTH,ICON_HEIGHT);
	private static final ImageIcon ICON_SAVE = loadIcon("/icons/095.png",ICON_WIDTH,ICON_HEIGHT);
	*/
	private static final ImageIcon ICON_GO = loadIcon("/icons/play.png",ICON_WIDTH,ICON_HEIGHT);
	private static final ImageIcon ICON_STOP = loadIcon("/icons/stop-icon_40.png",ICON_WIDTH,ICON_HEIGHT);
	private static final ImageIcon ICON_BROWSER = loadIcon("/icons/browser.png",ICON_WIDTH,ICON_HEIGHT);
	private static final ImageIcon ICON_APP = loadIcon("/icons/app_icon.png",ICON_WIDTH,ICON_HEIGHT);
	
	private static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder();
	private static final String TITLE = "Mtg Pricer";
	private static final int HEIGHT = 500;
 	private static final int WIDTH = 850;  
	
	private JFrame window;
	private JPanel tablePane;
	private JPanel leftPane;
	private JDialog aboutDialog;
	private CardsView view;
	private JTextField addTextField;
	private JSpinner addSpinner;
	private JToolBar toolBar;
	private SearchExecutor pricer;
	private Map<JCheckBox,CardFinder> checkBoxes = new LinkedHashMap<JCheckBox,CardFinder>();
	private boolean afterSearch;
	private boolean pricingInProgress;
	private Map<Class<? extends AbstractAction>,AbstractAction> actionMap 
					= new HashMap<Class<? extends AbstractAction>,AbstractAction>();
	
	private static CardFinder[] allSearchers = CardFinderFactory.allCardFinders();
	
	public Main(){
		createActions();
		setupGui();
		window.setVisible(true);
		startNewPricing();
	}
	
	/**
	 * Shows modal 'About' dialog.
	 */
	void showAbout() {
		if (aboutDialog == null)
			aboutDialog = new AboutDialog(window);
		aboutDialog.setVisible(true);
    }

	/**
	 * Setups all GUI elements.
	 */
	private void setupGui(){
		setLookAndFeel();
		window = new JFrame();
		window.setTitle(TITLE);
		window.setIconImage(ICON_APP.getImage());
		window.setSize(WIDTH, HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		
		tablePane = new JPanel(new BorderLayout());
		tablePane.setBorder(ETCHED_BORDER);
		
		toolBar = createToolBar();
		window.add(toolBar, BorderLayout.NORTH);
		window.add(createCardFindersPane(), BorderLayout.WEST);
		window.add(tablePane, BorderLayout.CENTER);
		window.setJMenuBar(createMenuBar());
		
	}
	
	/**
	 * Creates the toolbar and sets the buttons to proper state (enabled/disabled).
	 * @return
	 */
	private JToolBar createToolBar(){
		JToolBar tb = new JToolBar();
		
		tb.setFocusable(false);
		tb.add(actionMap.get(ImportCardsAction.class));
		tb.addSeparator(new Dimension(10,20));

		addTextField = new JTextField(10);
		addTextField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
		addTextField.getActionMap().put("submit", actionMap.get(AddCardAction.class));
		
		addTextField.setMaximumSize(addTextField.getPreferredSize());
		addTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				actionMap.get(AddCardAction.class).setEnabled(
						addTextField.getText().length() > 1);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				actionMap.get(AddCardAction.class).setEnabled(
						addTextField.getText().length() > 1);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				actionMap.get(AddCardAction.class).setEnabled(
						addTextField.getText().length() > 1);
			}
		});
		
		
		tb.add(addTextField);
		
		addSpinner = new QuantitySpinner();

		tb.add(addSpinner);
		tb.add(actionMap.get(AddCardAction.class));
		tb.add(actionMap.get(RemoveAction.class));
		
		tb.addSeparator(new Dimension(10,20));
		tb.add(actionMap.get(StartSearchAction.class));
		tb.add(actionMap.get(StopSearchAction.class));
		tb.addSeparator(new Dimension(10,20));
		tb.add(actionMap.get(SearchInBrowserAction.class));
		
		for (Component c : tb.getComponents())
			if (c instanceof AbstractButton)
				((AbstractButton)c).setFocusable(false);
		tb.setBorder(ETCHED_BORDER);
		
		return tb;
	}
	
	private JPanel createCardFindersPane(){
		
		int width = 210; 
		
		MigLayout ml = new MigLayout();
		leftPane = new JPanel(ml);
		JLabel lbl = new JLabel("Card pricing sources:");
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		leftPane.add(lbl,"wrap");
		checkBoxes.clear();
		for (CardFinder s: allSearchers){
			lbl = new JLabel(s.getName());
			lbl.setToolTipText(s.getURL());
			leftPane.add(lbl);
			JCheckBox cb = new JCheckBox();
			cb.setSelected(true);
			leftPane.add(cb,"wrap");
			//Add ti to map
			checkBoxes.put(cb, s);
		}
		
		leftPane.setPreferredSize(new Dimension(width, leftPane.getPreferredSize().height));
		leftPane.setBorder(ETCHED_BORDER);
		return leftPane;
	}
	
	private void updateLeftPanel(final Collection<CardFinder> ss){
		//Should run on  dispatch thread.
		leftPane.removeAll();
		for (CardFinder s: ss){
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
		JMenu menu = new JMenu();

		menu = new JMenu("Search");
		menu.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem mi = new JMenuItem(actionMap.get(NewSearchAction.class));
		menu.add(mi);
		mi = new JMenuItem(actionMap.get(StartSearchAction.class));
		mi.setIcon(null);
		menu.add(mi);
		mi = new JMenuItem(actionMap.get(SearchInBrowserAction.class));
		mi.setIcon(null);
		menu.add(mi);
		menuBar.add(menu);
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);

		mi = new JMenuItem(actionMap.get(ImportCardsAction.class));
		mi.setIcon(null);
		menu.add(mi);
		mi = new JMenuItem(actionMap.get(RemoveAction.class));
		mi.setIcon(null);
		menu.add(mi);
		menuBar.add(menu);
		
		menu = new JMenu("Export");
		mi = new JMenuItem(actionMap.get(ExportTableCsvAction.class));
		menu.add(mi);
		mi = new JMenuItem(actionMap.get(ExportTableTxtAction.class));
		menu.add(mi);
		menuBar.add(menu);
		menu= new JMenu("Help");
		mi = new JMenuItem("About");
		mi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAbout();
				
			}
		});
		menu.add(mi);
		menuBar.add(menu);
		
		return menuBar;
	}
	
	/**
	 * Creates all actions. Lot of manual code.
	 */
	private void createActions(){
		
		AbstractAction action = new StartSearchAction();
		action.setEnabled(false);
		actionMap.put(StartSearchAction.class, action);
		
		action = new StopSearchAction();
		action.setEnabled(false);
		actionMap.put(StopSearchAction.class, action);
		
		action = new ImportCardsAction();
		action.setEnabled(false);
		actionMap.put(ImportCardsAction.class, action);
		
		action = new AddCardAction();
		action.setEnabled(false);
		actionMap.put(AddCardAction.class, action);
		
		action = new RemoveAction();
		action.setEnabled(false);
		actionMap.put(RemoveAction.class, action);
		
		action = new ExportTableCsvAction();
		action.setEnabled(false);
		actionMap.put(ExportTableCsvAction.class, action);
		
		action = new ExportTableTxtAction();
		action.setEnabled(false);
		actionMap.put(ExportTableTxtAction.class, action);
		
		action = new ExportCardListAction();
		action.setEnabled(false);
		actionMap.put(ExportCardListAction.class, action);
		
		action = new NewSearchAction();
		action.setEnabled(false);
		actionMap.put(NewSearchAction.class, action);
		
		action = new SearchInBrowserAction();
		action.setEnabled(false);
		actionMap.put(SearchInBrowserAction.class, action);
		
	}
	
	/** Disable actions which make no sense if table is empty and vice versa*/
	private void emptyStateChanged(boolean isEmpty){
		boolean enabled = !isEmpty;
		actionMap.get(StartSearchAction.class).setEnabled(enabled);
		actionMap.get(ExportTableCsvAction.class).setEnabled(enabled);
		actionMap.get(ExportTableTxtAction.class).setEnabled(enabled);
		actionMap.get(ExportCardListAction.class).setEnabled(enabled);
		actionMap.get(NewSearchAction.class).setEnabled(enabled);
	}
	
	private void pricingStarted(){
		pricingInProgress = true;
		actionMap.get(ImportCardsAction.class).setEnabled(false);
		actionMap.get(RemoveAction.class).setEnabled(false);
		actionMap.get(AddCardAction.class).setEnabled(false);
		actionMap.get(StartSearchAction.class).setEnabled(false);
		actionMap.get(StopSearchAction.class).setEnabled(true);
		
		addTextField.setEnabled(false);
		addSpinner.setEnabled(false);
	}
	

	
	private void startNewPricing(){
		actionMap.get(ImportCardsAction.class).setEnabled(true);
		addTextField.setEnabled(true);
		addSpinner.setEnabled(true);
		actionMap.get(RemoveAction.class).setEnabled(false);
		actionMap.get(SearchInBrowserAction.class).setEnabled(false);
		
		pricer = new SearchExecutor();
		pricer.addSearchObserver(this, null);
		
		
		tablePane.removeAll();
		MtgPricerTableModel model = new MtgPricerTableModel(pricer.data());
		view = new CardsView(model);
		view.addPropertyChangeListener(this);
		view.setActionForKey(actionMap.get(RemoveAction.class), KeyStroke.getKeyStroke("DELETE"));
		tablePane.add(view);
		window.revalidate();
	}
	
	
	private Collection<CardFinder> getSelectedSearchers(){
		List<CardFinder> selected = new ArrayList<CardFinder>(); 
		for (JCheckBox cb : checkBoxes.keySet())
			if (cb.isSelected())
				selected.add(checkBoxes.get(cb));
		return selected;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CardsView.GRID_SELECTED_PROPERTY){
			boolean enabled =  (boolean) evt.getNewValue();
			
			System.out.print("Grid selected property fired:  " +  enabled );
			actionMap.get(RemoveAction.class).setEnabled(enabled);
			actionMap.get(SearchInBrowserAction.class).setEnabled(enabled);
		}
		if (evt.getPropertyName() == CardsView.EMPTY_STATE_CHANGED){
			boolean empty =  (boolean) evt.getNewValue();
			emptyStateChanged(empty);
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
	
	
	public static void main(String[] args){
		new Main();
	}
	
	
	//=================== Actions ===================================================
	
	private class NewSearchAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		
		NewSearchAction(){
			super("New pricing");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String msg = "This will";
			if (pricer.isSearchInProgress())
				msg += " interrupt the current search and";
			msg += " clear the card list.\n Do you want to continue?";
			String title = "For sure?";
			
			int response = JOptionPane.showConfirmDialog(window, msg,
					title, JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION)
				return;
			
			//Interrup. Just in case the search is in progress.
			pricer.interrupt();
			
			//Disable them as no notification will be received from the table
			//that table has no rows.
			actionMap.get(NewSearchAction.class).setEnabled(false);
			actionMap.get(StartSearchAction.class).setEnabled(false);

			window.remove(leftPane);
			window.add(createCardFindersPane(), BorderLayout.WEST);
			window.validate();
			window.repaint();
			
			startNewPricing();
		}
		
	}
	
	private class AddCardAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		AddCardAction(){
			super("Add",ICON_ADD);
			putValue(Action.SHORT_DESCRIPTION, "Add a new card row");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cardName =  addTextField.getText();
			int quantity = (int) addSpinner.getValue();
			addCard(new Card(cardName), quantity);
			addTextField.setText("");
		}
		
	}
	
	private class RemoveAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;

		RemoveAction(){
			super("Delete",ICON_REMOVE);
			putValue(Action.SHORT_DESCRIPTION, "Remove selected card row(s)");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Removing");
			pricer.removeCards(view.getSelectedCards());
		}
		
		@Override
		public boolean isEnabled(){
			
			boolean b = (super.isEnabled() && !pricingInProgress);
			System.out.println("is remove enabled? " + b);
			return b;
		}
		@Override
		public void setEnabled(boolean b){
			System.out.println("setting enabled " + b);
			StackTraceElement[] s = Thread.currentThread().getStackTrace();
			System.out.println(s[2]);
			super.setEnabled(b);
		}
		
	}
	
	private class ImportCardsAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		ImportCardsAction(){
			super("Import card list",ICON_IMPORT);
			putValue(Action.SHORT_DESCRIPTION, "Append cards from the .txt file");
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
				m = CardParser.parseFromFile(f);
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
				bw.write(report.generateFormattedReport());
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
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ExportCardListAction() {
			super("Export card list");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
	
	private class StartSearchAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		
		StartSearchAction(){
			super("Start search",ICON_GO);
			putValue(Action.SHORT_DESCRIPTION, "Run card prices search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			final Collection<CardFinder> searchers = getSelectedSearchers(); 
			pricer.setCardFinders(searchers);
			updateLeftPanel(searchers);
			actionMap.get(RemoveAction.class).setEnabled(false);
			pricingStarted();
			pricer.startSearch();
			//window.pack();
		}
	}
	
	private class StopSearchAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		
		StopSearchAction(){
			super("Stop search",ICON_STOP);
			putValue(Action.SHORT_DESCRIPTION, "Stop the running search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			setEnabled(false);
			pricer.interrupt();
			
		}
	}
	
	private class SearchInBrowserAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		SearchInBrowserAction(){
			super("Find via browser",ICON_BROWSER);
			putValue(Action.SHORT_DESCRIPTION, "Open web pages with search result");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//TODO handle this somewhere. Do not enable the action.
			assert Desktop.isDesktopSupported();
			
			Collection<Card> cards = view.getSelectedCards();
			Collection<CardFinder> searchers = null;
			
			if (afterSearch)
				pricer.getCardFinders();
			else
				searchers = getSelectedSearchers();
			
			for (Card c : cards)
				for (CardFinder s : searchers){
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

	@Override
	public void startedSearchingForCard(Card card, CardFinder finder) {
		//Empty
	}

	@Override
	public void finishedSearchingFor(CardResult result, CardFinder finder) {
		//Empty
	}

	@Override
	public void searchingFinished(CardFinder finder, SearchResults data) {
		//Empty
	}

	@Override
	public void searchingFailed(CardFinder finder, Throwable t) {
		// TODO Auto-generated method stub
	}

	@Override
	public void searchingFinished(boolean interrupted) {
		System.out.println("Pricing ended");
		pricingInProgress = false;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				System.out.println("iz enabled ? " +actionMap.get(RemoveAction.class).isEnabled());
				//Hack as the call to setEnabled(true) does not result in action button in toolbar to be set to enabled when
				//the user click to the table during process of stopping search.
				//TODO investigate more.
				actionMap.get(RemoveAction.class).setEnabled(false); 
				actionMap.get(RemoveAction.class).setEnabled(true);
				window.setCursor(Cursor.getDefaultCursor());
			}
		});
	}
}
