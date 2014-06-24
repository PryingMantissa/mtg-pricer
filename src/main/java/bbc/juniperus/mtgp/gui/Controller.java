package bbc.juniperus.mtgp.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardFinderFactory;
import bbc.juniperus.mtgp.cardsearch.CardParser;
import bbc.juniperus.mtgp.cardsearch.CardSearchResults;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.cardsearch.SearchObserver;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.PricingSettings;
import bbc.juniperus.mtgp.tablemodel.MtgPricerTableModel;
import bbc.juniperus.mtgp.tablemodel.ReportCreator;

public class Controller implements SearchObserver, GridListener {

	/**
	 * A user action type.
	 */
	public enum UserAction {
		NEW_SEARCH, ADD_CARD, REMOVE_CARD, IMPORT_CARDS, EXPORT_TO_CSV, 
		EXPORT_TO_TXT, OPEN_IN_BROWSER, START_SEARCH, STOP_SEARCH
	}

	/**
	 * A phase of pricing type.
	 */
	public enum Phase {
		SETTING, SEARCHING, PRICING_FINISHED
	}

	private Map<UserAction, AbstractAction> actionMap = new HashMap<>();
	private SearchExecutor searchExecutor;
	private PricingSettings pricingSettings;
	private List<CardFinder> finders;
	private MtgPricerTableModel tableModel;
	private Phase currentPhase;
	private MainView mainView;
	private String cardNameTextFieldValue;
	private int quantitySpinnerValue = 1; // Default (and also minimal)spinner
											// value
	private List<Card> selectedCards = new ArrayList<>();

	public Controller() {
		createActions();
		finders = CardFinderFactory.allCardFinders();
		tableModel = new MtgPricerTableModel(this);
		mainView = new MainView(this);
		mainView.show();
	}

	public void newPricing() {
		currentPhase = Phase.SETTING;
		pricingSettings = new PricingSettings();
		for (CardFinder finder : finders)
			// Add all card finders as default
			pricingSettings.addFinder(finder);
		tableModel.newPricing(pricingSettings);
		mainView.newPricing();
		setDefaultActionAvailability();
		tableModel.fireTableStructureChanged();
	}

	public PricingSettings getPricingSettings() {
		return pricingSettings;
	}

	public MtgPricerTableModel getTableModel() {
		return tableModel;
	}

	/**
	 * Enables/disable given {@link CardFinder} for pricing. Possible only
	 * during {@link Phase#SETTING} phase.
	 * 
	 * @param finder
	 *            the finder to be enabled/disabled
	 * @param enabled
	 *            <code>true</code> if
	 */
	public void setFinderEnabled(CardFinder finder, boolean enabled) {
		if (currentPhase != Phase.SETTING)
			throw new IllegalStateException("Changing pricing settings " + "is only possible during the "
					+ Phase.SETTING + " phase.");

		if (enabled && !pricingSettings.getFinders().contains(finder))
			pricingSettings.addFinder(finder);

		if (!enabled && pricingSettings.getFinders().contains(finder))
			pricingSettings.removeFinder(finder);
		actionMap.get(UserAction.START_SEARCH).setEnabled((pricingSettings.getFinders().size() > 0));

	}

	/**
	 * Returns all possible card finders regardless whether they are selected
	 * for the search or not.
	 */
	public Collection<CardFinder> getCardFinders() {
		return Collections.unmodifiableList(finders);
	}

	public void cardTextFieldValueChanged(String newText) {
		if (newText.isEmpty()) {
			cardNameTextFieldValue = null;
			disableAction(UserAction.ADD_CARD);
		} else {
			cardNameTextFieldValue = newText;
			enableAction(UserAction.ADD_CARD);
		}
	}

	public void quantitySpinnerValueChanged(int newValue) {
		quantitySpinnerValue = newValue;
	}

	public void displayErroMessage(String txt) {
		mainView.reportError(txt);
	}

	@Override
	public void searchStarted(int numberOfCards) {

	}

	@Override
	public void cardSearchStarted(Card card, CardFinder finder) {
		// Empty
	}

	@Override
	public void searchThreadFailed(CardFinder finder, Throwable t) {
		// TODO Auto-generated method stub
	}

	@Override
	public void cardSearchFinished(Card card, CardResult result, CardFinder finder) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				tableModel.fireTableRowsUpdated(0, Integer.MAX_VALUE);

			}

		});

	}

	@Override
	public void searchThreadFinished(CardFinder finder, CardSearchResults results) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchingFinished(final boolean interrupted) {
		currentPhase = Phase.PRICING_FINISHED;

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (interrupted)
					mainView.searchStopped(); // In case the search was stopped
												// by user and the view was set
												// to busy state
				else
					mainView.searchFinished();
				disableAction(UserAction.STOP_SEARCH);
				enableAction(UserAction.NEW_SEARCH);
			}

		});

	}

	private void addCardLeniently(Card card, int quantity) {

		if (pricingSettings.getCards().contains(card)) {
			int oldQ = pricingSettings.getQuantity(card);
			String msg = "<html>Card <b><i> " + card.getName() + "</i></b> is already"
					+ " present in the deck (quantity: " + oldQ + ")." + " Do you really want to add "
					+ quantity + " pieces of this card to the deck?";

			String title = "Duplicate detected";
			if (mainView.askForConfirmation(title, msg)) {
				pricingSettings.setNewQuantity(card, oldQ + quantity);
			}

		} else
			pricingSettings.addCard(card, quantity);
	}

	@Override
	public void gridFocusLost() {
		// Empty.
	}

	@Override
	public void gridFocusGained() {
		// Empty.
	}

	@Override
	public void gridSelectionChanged(int[] selectedRows) {

		selectedCards.clear();
		for (int rowIndex : selectedRows)
			selectedCards.add(tableModel.getCardAt(rowIndex));

		if (selectedRows.length == 0) {
			disableAction(UserAction.REMOVE_CARD);
			disableAction(UserAction.OPEN_IN_BROWSER);
		} else {
			enableAction(UserAction.REMOVE_CARD);
			enableAction(UserAction.OPEN_IN_BROWSER);
		}
	}

	/**
	 * Creates all actions.
	 */
	private void createActions() {

		AbstractAction action = new StartSearchAction();
		actionMap.put(UserAction.START_SEARCH, action);
		action = new StopSearchAction();
		actionMap.put(UserAction.STOP_SEARCH, action);
		action = new ImportCardsAction();
		actionMap.put(UserAction.IMPORT_CARDS, action);
		action = new AddCardAction();
		actionMap.put(UserAction.ADD_CARD, action);
		action = new RemoveCardAction();
		actionMap.put(UserAction.REMOVE_CARD, action);
		action = new ExportTableCsvAction();
		actionMap.put(UserAction.EXPORT_TO_CSV, action);
		action = new ExportTableTxtAction();
		actionMap.put(UserAction.EXPORT_TO_TXT, action);
		action = new NewPricingAction();
		actionMap.put(UserAction.NEW_SEARCH, action);
		action = new SearchInBrowserAction();
		actionMap.put(UserAction.OPEN_IN_BROWSER, action);
		setDefaultActionAvailability();
	}

	/**
	 * Primitive implementation. For better code readability & debugging.
	 */
	private void enableAction(UserAction action) {
		actionMap.get(action).setEnabled(true);
	}

	/**
	 * Primitive implementation. For better code readability & debugging.
	 */
	private void disableAction(UserAction action) {
		actionMap.get(action).setEnabled(false);
	}

	private void setDefaultActionAvailability() {
		enableAction(UserAction.IMPORT_CARDS);
		disableAction(UserAction.REMOVE_CARD);
		disableAction(UserAction.ADD_CARD);
		disableAction(UserAction.OPEN_IN_BROWSER);
		disableAction(UserAction.START_SEARCH);
		disableAction(UserAction.STOP_SEARCH);
		disableAction(UserAction.REMOVE_CARD);
	}

	public Action getAction(UserAction action) {
		return actionMap.get(action);
	}

	@SuppressWarnings("serial")
	private class NewPricingAction extends AbstractAction {

		NewPricingAction() {
			super("New pricing");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			assert currentPhase != Phase.SEARCHING;
			newPricing();
		}

	}

	@SuppressWarnings("serial")
	private class AddCardAction extends AbstractAction {

		AddCardAction() {
			super("Add", ResourceLoader.ICON_ADD);
			putValue(Action.SHORT_DESCRIPTION, "Add a new card row");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (cardNameTextFieldValue == null)
				throw new AssertionError();
			Card card = new Card(cardNameTextFieldValue);
			addCardLeniently(card, quantitySpinnerValue);
			tableModel.fireTableDataChanged();
			mainView.clearAddCardTextField();
			cardNameTextFieldValue = null;

			if (pricingSettings.getCards().size() > 0)
				enableAction(UserAction.START_SEARCH); // Enable start search if
														// we added some cards
		}

		@Override
		public void setEnabled(boolean newValue) {
			if (newValue) {
				if (currentPhase != Phase.SETTING)
					return;
			}
			super.setEnabled(newValue);
		}

	}

	@SuppressWarnings("serial")
	private class RemoveCardAction extends AbstractAction {

		RemoveCardAction() {
			super("Delete", ResourceLoader.ICON_REMOVE);
			putValue(Action.SHORT_DESCRIPTION, "Remove selected card row(s)");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedCards.size() < 1)
				throw new AssertionError();
			for (Card card : selectedCards)
				pricingSettings.removeCard(card);

			tableModel.fireTableStructureChanged();

			if (pricingSettings.getCards().size() < 1) // Disable start search
														// if there are no cards
														// left
				disableAction(UserAction.START_SEARCH);
		}

		@Override
		public void setEnabled(boolean newValue) {
			if (newValue) {
				if (currentPhase == Phase.SEARCHING)
					return;
			}
			super.setEnabled(newValue);
		}

	}

	private class ImportCardsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		ImportCardsAction() {
			super("Import card list", ResourceLoader.ICON_IMPORT);
			putValue(Action.SHORT_DESCRIPTION, "Append cards from the .txt file");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.showOpenDialog(null);
			File f = fileChooser.getSelectedFile();

			if (f == null)
				return;

			Map<Card, Integer> result = null;
			try {
				result = CardParser.parseFromFile(f);
			} catch (IOException e) {
				mainView.reportError("An expception ocurred while " + "attempting to read from the file\n "
						+ f.getAbsolutePath() + "\n\n" + e.getMessage());
				e.printStackTrace();
			} catch (ParseException e) {
				mainView.reportError(e.getMessage());
				e.printStackTrace();
			}

			if (result == null)
				return;

			for (Card c : result.keySet())
				addCardLeniently(c, result.get(c));
			tableModel.fireTableStructureChanged();

			if (pricingSettings.getCards().size() > 0) // Enable start search if
														// we added some cards
				enableAction(UserAction.START_SEARCH);

		}
	}

	private class ExportTableCsvAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		ExportTableCsvAction() {
			super("Export to .csv");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String name = "exported-deck_" + sdf.format(new Date()) + ".csv";
			chooser.setSelectedFile(new File(name));
			chooser.showSaveDialog(null);
			File f = chooser.getSelectedFile();
			if (f == null)
				return;

			ReportCreator report = new ReportCreator(tableModel);
			try {
				FileWriter fw = new FileWriter(f);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(report.createCSVReport(","));
				bw.close();
			} catch (IOException ex) {
				mainView.reportError("An I/O exception occurred while writing to file\n" + f.getName()
						+ "\n\n" + ex.getMessage());
				ex.printStackTrace();
			}

		}

	}

	private class ExportTableTxtAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		ExportTableTxtAction() {
			super("Export to .txt");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			JFileChooser chooser = new JFileChooser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String name = "exported-deck_" + sdf.format(new Date()) + ".txt";
			chooser.setSelectedFile(new File(name));
			chooser.showSaveDialog(null);
			File f = chooser.getSelectedFile();
			if (f == null)
				return;

			ReportCreator report = new ReportCreator(tableModel);
			try {
				FileWriter fw = new FileWriter(f);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(report.generateTxtReport());
				bw.close();
			} catch (IOException ex) {
				mainView.reportError("An I/O exception occurred while writing to file\n" + f.getName()
						+ "\n\n" + ex.getMessage());
				ex.printStackTrace();
			}

		}

	}

	@SuppressWarnings("serial")
	private class StartSearchAction extends AbstractAction {

		StartSearchAction() {
			super("Start search", ResourceLoader.ICON_GO);
			putValue(Action.SHORT_DESCRIPTION, "Run card prices search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			searchExecutor = new SearchExecutor(pricingSettings.getCards(), pricingSettings.getFinders());
			searchExecutor.addSearchObserver(Controller.this);
			currentPhase = Phase.SEARCHING;
			mainView.searchStarted(searchExecutor);
			searchExecutor.startSearch();
			tableModel.startPresentingResults(searchExecutor.getResultsStorage());
			enableAction(UserAction.STOP_SEARCH);
			disableAction(UserAction.START_SEARCH);
			disableAction(UserAction.ADD_CARD);
			disableAction(UserAction.REMOVE_CARD);
			disableAction(UserAction.IMPORT_CARDS);
			disableAction(UserAction.NEW_SEARCH);

		}

		/**
		 * Intercepts the call and inspects if the action can really be enabled.
		 */
		@Override
		public void setEnabled(boolean newValue) {
			if (newValue) {
				// There must be at least 1 card and card pricer selected.
				if (pricingSettings.getCards().size() == 0 || pricingSettings.getFinders().size() == 0)
					return;
			}
			super.setEnabled(newValue);
		}

	}

	@SuppressWarnings("serial")
	private class StopSearchAction extends AbstractAction {

		StopSearchAction() {
			super("Stop search", ResourceLoader.ICON_STOP);
			putValue(Action.SHORT_DESCRIPTION, "Stop the running search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			searchExecutor.stopSearch();
			mainView.stopSearchIssued(); // Active state will be set in observer
											// method when the search has been
											// reported to finish
			setEnabled(false);
		}
	}

	@SuppressWarnings("serial")
	private class SearchInBrowserAction extends AbstractAction {

		SearchInBrowserAction() {
			super("Find via browser", ResourceLoader.ICON_BROWSER);
			putValue(Action.SHORT_DESCRIPTION, "Open web pages with search result");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO handle this somewhere. Do not enable the action.
			// assert Desktop.isDesktopSupported();

			Collection<CardFinder> finders = pricingSettings.getFinders();

			for (Card c : selectedCards)
				for (CardFinder f : finders) {
					URL url = f.getURLForCard(c.getName());
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
