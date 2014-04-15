package bbc.juniperus.mtgp.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardFinderFactory;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.data.PricingSettings;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.tablemodel.PricerTableModel;

public class Controller implements SearchObserver, GridListener {
	
	public enum UserAction { NEW_SEARCH, ADD_CARD, REMOVE_CARD, 
		IMPORT_CARDS, EXPORT_TO_CSV, EXPORT_TO_TXT, OPEN_IN_BROWSER, START_SEARCH, STOP_SEARCH}
	
	public enum Phase {SETTING, SEARCHING, PRICING_FINISHED}
	
	
	private Map<UserAction,AbstractAction> actionMap = new HashMap<>();
	private SearchExecutor searchExecutor;
	private PricingSettings pricingSettings;
	private List<CardFinder> finders;
	private PricerTableModel tableModel;
	private Phase currentPhase;
	private MainView mainView;
	private String addCardFieldText;
	private int addCardSpinnerValue = 1;
	private List<Card> selectedCards = new ArrayList<>();
	
	
	public Controller(){
		createActions();
		finders = CardFinderFactory.allCardFinders();
		tableModel = new PricerTableModel(this);
		newPricing();
		mainView = new MainView(this); //TODO maybe we dont need it here
		mainView.show();
	}
	
	public void newPricing(){
		currentPhase = Phase.SETTING;
		pricingSettings = new PricingSettings();
		tableModel.setPricingSettings(pricingSettings);
		//TODO testing code
		pricingSettings.addCard(new Card("Card A"),3);
		pricingSettings.addCard(new Card("Card B"),3);
		pricingSettings.addCard(new Card("Card C"),2);
		pricingSettings.addCard(new Card("Card D"),1);
		pricingSettings.addCard(new Card("Card E"),5);
		
		
	}
	
	public PricingSettings getPricingSettings(){
		return pricingSettings;
	}
	
	public PricerTableModel getTableModel(){
		System.out.println("returnin Tm: " + tableModel);
		return tableModel;
	}
	
	/**
	 * Enables/disable given {@link CardFinder} for pricing. Possible only during {@link Phase#SETTING} phase. 
	 * @param finder the finder to be enabled/disabled
	 * @param enabled <code>true</code> if 
	 */
	public void setFinderEnabled(CardFinder finder, boolean enabled){
		if(currentPhase != Phase.SETTING)
			throw new IllegalStateException("Changing pricing settings "
					+ "is only possible during the " + Phase.SETTING + " phase.");
		
		if (enabled && !pricingSettings.getFinders().contains(finder))
				pricingSettings.addFinder(finder);
		
		if (!enabled && pricingSettings.getFinders().contains(finder))
			pricingSettings.removeFinder(finder);
	}
	
	
	/**
	 * Returns all possible card finders regardless whether they are selected for the search or not.
	 */
	public Collection<CardFinder> getCardFinders(){
		return Collections.unmodifiableList(finders);
	}
	
	
	public void addCardTextFieldValueChanged(String newText){
		System.out.println("field val changed " + newText);
		addCardFieldText = newText;
	}
	
	public void addCardSpinnerValueChanged(int newValue){
		System.out.println("spinner val changed" + newValue);
		addCardSpinnerValue = newValue;
	}
	
	
	
	
	@Override
	public void startedSearchingForCard(Card card, CardFinder finder) {
		//Empty
	}

	@Override
	public void searchingFailed(CardFinder finder, Throwable t) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void finishedSearchingForCard(Card card, CardResult result,
			CardFinder finder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void searchingFinished(CardFinder finder) {
		// TODO Auto-generated method stub
		
	}
	
	

	@Override
	public void searchingFinished(boolean interrupted) {
		System.out.println("Pricing ended");
		/*
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
		*/
	}
	

	/**
	 * Creates all actions.
	 */
	private void createActions(){
		
		AbstractAction action = new StartSearchAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.START_SEARCH, action);
		
		action = new StopSearchAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.STOP_SEARCH, action);
		
		action = new ImportCardsAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.IMPORT_CARDS, action);
		
		action = new AddCardAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.ADD_CARD, action);
		
		action = new RemoveCardAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.REMOVE_CARD, action);
		
		action = new ExportTableCsvAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.EXPORT_TO_CSV, action);
		
		action = new ExportTableTxtAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.EXPORT_TO_TXT, action);
		
		/*
		action = new ExportCardListAction();
		action.setEnabled(false);
		actionMap.put(ExportCardListAction.class, action);
		*/
		
		action = new NewSearchAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.NEW_SEARCH, action);
		
		action = new SearchInBrowserAction();
		//action.setEnabled(false);
		actionMap.put(UserAction.OPEN_IN_BROWSER, action);
		
	}
	
	public Action getAction(UserAction action){
		return actionMap.get(action);
	}
	
	
	@SuppressWarnings("serial")
	private class NewSearchAction extends AbstractAction{

		NewSearchAction(){
			super("New pricing");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			System.out.println("New pricing");
			
			/*
			String msg = "This will";
			if (searchExecutor.isSearchInProgress())
				msg += " interrupt the current search and";
			msg += " clear the card list.\n Do you want to continue?";
			String title = "For sure?";
			
			int response = JOptionPane.showConfirmDialog(window, msg,
					title, JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION)
				return;
			
			//Interrup. Just in case the search is in progress.
			searchExecutor.interrupt();
			
			//Disable them as no notification will be received from the table
			//that table has no rows.
			actionMap.get(NewSearchAction.class).setEnabled(false);
			actionMap.get(StartSearchAction.class).setEnabled(false);

			window.remove(leftPane);
			window.add(createCardFindersPane(), BorderLayout.WEST);
			window.validate();
			window.repaint();
			
			startNewPricing();*/
		}
		
	}
	
	@SuppressWarnings("serial")
	private class AddCardAction extends AbstractAction{

		AddCardAction(){
			super("Add",ResourceLoader.ICON_ADD);
			putValue(Action.SHORT_DESCRIPTION, "Add a new card row");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			if (addCardFieldText == null)
				throw new AssertionError();*/
			System.out.println("adding card " + addCardFieldText + " " + addCardSpinnerValue);
			Card card = new Card(addCardFieldText);
			pricingSettings.addCard(card, addCardSpinnerValue);
			tableModel.fireTableDataChanged();
			//tableModel.fireTableStructureChanged();
			mainView.clearAddCardTextField();
			addCardFieldText = null;
			
//			for (Card c : pricingSettings.getCards())
//				System.out.println(c);
			
		}
		
	}
	
	private class RemoveCardAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;

		RemoveCardAction(){
			super("Delete",ResourceLoader.ICON_REMOVE);
			putValue(Action.SHORT_DESCRIPTION, "Remove selected card row(s)");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedCards.size() < 1)
				throw new AssertionError();
			for (Card card : selectedCards)
				pricingSettings.removeCard(card);
			
			tableModel.fireTableStructureChanged();
		}
		
		@Override //TODO remove
		public boolean isEnabled(){
			return true;
//			boolean b = (super.isEnabled() && !pricingInProgress);
//			System.out.println("is remove enabled? " + b);
//			return b;
		}
		
		@Override //TODO remove
		public void setEnabled(boolean b){
			
			super.setEnabled(b);
		}
		
	}
	
	private class ImportCardsAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		ImportCardsAction(){
			super("Import card list",ResourceLoader.ICON_IMPORT);
			putValue(Action.SHORT_DESCRIPTION, "Append cards from the .txt file");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			System.out.println("Importing cards");
			
			/*
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
			}*/
		}	
	}
	
	private class ExportTableCsvAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		ExportTableCsvAction(){
			super("Export to .csv");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {	
			
			System.out.println("Exporting cards");
			
			/*
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
			*/
		}
		
	}
	
	private class ExportTableTxtAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		ExportTableTxtAction(){
			super("Export to .txt");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			System.out.println("Exporting cards to txt");
			/*
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
			*/
		}
		
	}
	
	private class ExportCardListAction extends AbstractAction{
		
		public ExportCardListAction() {
			super("Export card list");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
	
	@SuppressWarnings("serial")
	private class StartSearchAction extends AbstractAction{
		
		StartSearchAction(){
			super("Start search",ResourceLoader.ICON_GO);
			putValue(Action.SHORT_DESCRIPTION, "Run card prices search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Starting search");
			/*
			final Collection<CardFinder> searchers = getSelectedSearchers(); 
			pricer.setCardFinders(searchers);
			updateLeftPanel(searchers);
			actionMap.get(RemoveAction.class).setEnabled(false);
			pricingStarted();
			pricer.startSearch();
			//window.pack();*/
		}
	}
	
	@SuppressWarnings("serial")
	private class StopSearchAction extends AbstractAction{
		
		StopSearchAction(){
			super("Stop search",ResourceLoader.ICON_STOP);
			putValue(Action.SHORT_DESCRIPTION, "Stop the running search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Stopping search");
			/*
			window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			setEnabled(false);
			pricer.interrupt();*/
			
		}
	}
	
	private class SearchInBrowserAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		SearchInBrowserAction(){
			super("Find via browser",ResourceLoader.ICON_BROWSER);
			putValue(Action.SHORT_DESCRIPTION, "Open web pages with search result");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Searching in browser");
			
			/*
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
				*/
		}
	}

	@Override
	public void gridFocusLost() {
		//System.out.println("Grid focus lost");
	}

	@Override
	public void gridFocusGained() {
		//System.out.println("Grid focus gained");
	}

	@Override
	public void gridSelectionChanged(int[] selectedRows) {
		//System.out.println("Grid selection changed " + Arrays.toString(selectedRows));
		selectedCards.clear();
		for (int rowIndex : selectedRows)
			selectedCards.add(tableModel.getCardAt(rowIndex));
		
	}


}

