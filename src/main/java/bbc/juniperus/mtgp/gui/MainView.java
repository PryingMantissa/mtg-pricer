package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardFinderFactory;
import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.gui.Controller.UserAction;

/**
 * The main class and the app entry point.
 */
public class MainView {
	
	
	private static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder();
	private static final int HEIGHT = 500;
 	private static final int WIDTH = 850;

	private JPanel tablePane;
	private CardGrid cardGrid;
	private CardFindersPane findersPane;
	
	private JTextField addTextField;
	private JSpinner addSpinner;
	private JToolBar toolBar;
	private final JFrame window;
	private static final String TITLE = "Mtg Pricer";
	private JDialog aboutDialog;
	private Controller controller;
	
	
	public MainView(Controller controller){
		window = new JFrame();
		window.setTitle(TITLE);
		window.setIconImage(ResourceLoader.ICON_APP.getImage());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		this.controller = controller;
		setupGui();
	}
	
	public void show(){
		window.pack();
		window.setVisible(true);
	}
	
	
	public void clearAddCardTextField(){
		addTextField.setText("");
	}
	
	/**
	 * Shows modal 'About' dialog.
	 */
	void showAbout() {
		if (aboutDialog == null)
			aboutDialog = new AboutDialog(window);
		aboutDialog.setVisible(true);
    }
	
	
	
	
	public void reportError(String text){
		JOptionPane.showMessageDialog(window,
				text ,"Sorry...",JOptionPane.ERROR_MESSAGE);
	}
	
	public boolean askForConfirmation(String title, String text){
		int response = JOptionPane.showConfirmDialog(window, text, title, JOptionPane.YES_NO_OPTION);
		return (response == JOptionPane.YES_OPTION);
	}
	

	/**
	 * Setups all GUI elements.
	 */
	private void setupGui(){
		
		tablePane = new JPanel(new BorderLayout());
		tablePane.setBorder(ETCHED_BORDER);
		cardGrid = new CardGrid(controller.getTableModel());
		cardGrid.addGridListener(controller);
		tablePane.add(cardGrid);
		
		toolBar = createToolBar();
		window.add(toolBar, BorderLayout.NORTH);
		findersPane = new CardFindersPane(controller);
		window.add(findersPane, BorderLayout.WEST);
		window.add(tablePane, BorderLayout.CENTER);
		window.setJMenuBar(createMenuBar());
		findersPane.showFinderSettings();
		
		
	}
	
	
	private JToolBar createToolBar(){
		JToolBar tb = new JToolBar();
		
		tb.setFocusable(false);
		tb.add(controller.getAction(UserAction.IMPORT_CARDS));
		tb.addSeparator(new Dimension(10,20));

		addTextField = new JTextField(10);
		addTextField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
		addTextField.getActionMap().put("submit", controller.getAction(UserAction.ADD_CARD));
		
		addTextField.setMaximumSize(addTextField.getPreferredSize());
		addTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			private void informController(){
				controller.cardTextFieldValueChanged(addTextField.getText());
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				informController();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				informController();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				informController();
			}
		});
		
		
		tb.add(addTextField);
		
		addSpinner = new QuantitySpinner();
		addSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer) addSpinner.getValue();
				controller.quantitySpinnerValueChanged(value);
			}
		});

		tb.add(addSpinner);
		tb.add(controller.getAction(UserAction.ADD_CARD));
		tb.add(controller.getAction(UserAction.REMOVE_CARD));
		
		tb.addSeparator(new Dimension(10,20));
		tb.add(controller.getAction(UserAction.START_SEARCH));
		tb.add(controller.getAction(UserAction.STOP_SEARCH));
		tb.addSeparator(new Dimension(10,20));
		tb.add(controller.getAction(UserAction.OPEN_IN_BROWSER));
		
		//Make all buttons unfocusable.
		for (Component c : tb.getComponents())
			if (c instanceof AbstractButton)
				((AbstractButton)c).setFocusable(false);
		tb.setBorder(ETCHED_BORDER);
		
		return tb;
	}
	

	private JMenuBar createMenuBar(){
		JMenuBar  menuBar = new JMenuBar();
		JMenu menu = new JMenu();

		menu = new JMenu("Search");
		menu.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem mi = new JMenuItem(controller.getAction(UserAction.NEW_SEARCH));
		menu.add(mi);
		mi = new JMenuItem(controller.getAction(UserAction.START_SEARCH));
		mi.setIcon(null);
		menu.add(mi);
		mi = new JMenuItem(controller.getAction(UserAction.OPEN_IN_BROWSER));
		mi.setIcon(null);
		menu.add(mi);
		menuBar.add(menu);
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);

		mi = new JMenuItem(controller.getAction(UserAction.IMPORT_CARDS));
		mi.setIcon(null);
		menu.add(mi);
		mi = new JMenuItem(controller.getAction(UserAction.REMOVE_CARD));
		mi.setIcon(null);
		menu.add(mi);
		menuBar.add(menu);
		
		menu = new JMenu("Export");
		mi = new JMenuItem(controller.getAction(UserAction.EXPORT_TO_CSV));
		menu.add(mi);
		mi = new JMenuItem(controller.getAction(UserAction.EXPORT_TO_TXT));
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
//	
//	/** Disable actions which make no sense if table is empty and vice versa*/
//	private void emptyStateChanged(boolean isEmpty){
//		boolean enabled = !isEmpty;
//		actionMap.get(StartSearchAction.class).setEnabled(enabled);
//		actionMap.get(ExportTableCsvAction.class).setEnabled(enabled);
//		actionMap.get(ExportTableTxtAction.class).setEnabled(enabled);
//		actionMap.get(ExportCardListAction.class).setEnabled(enabled);
//		actionMap.get(NewSearchAction.class).setEnabled(enabled);
//	}
//	
//	private void pricingStarted(){
//		pricingInProgress = true;
//		actionMap.get(ImportCardsAction.class).setEnabled(false);
//		actionMap.get(RemoveAction.class).setEnabled(false);
//		actionMap.get(AddCardAction.class).setEnabled(false);
//		actionMap.get(StartSearchAction.class).setEnabled(false);
//		actionMap.get(StopSearchAction.class).setEnabled(true);
//		
//		addTextField.setEnabled(false);
//		addSpinner.setEnabled(false);
//	}
//	

	
//	private void startNewPricing(){
//		actionMap.get(ImportCardsAction.class).setEnabled(true);
//		addTextField.setEnabled(true);
//		addSpinner.setEnabled(true);
//		actionMap.get(RemoveAction.class).setEnabled(false);
//		actionMap.get(SearchInBrowserAction.class).setEnabled(false);
//		
//		pricer = new SearchExecutor();
//		pricer.addSearchObserver(this, null);
//		
//		
//		tablePane.removeAll();
//		MtgPricerTableModel model = new MtgPricerTableModel(pricer.data());
//		view = new CardsView(model);
//		view.addPropertyChangeListener(this);
//		view.setActionForKey(actionMap.get(RemoveAction.class), KeyStroke.getKeyStroke("DELETE"));
//		tablePane.add(view);
//		window.revalidate();
//	}
	
	
//	private Collection<CardFinder> getSelectedSearchers(){
//		List<CardFinder> selected = new ArrayList<CardFinder>(); 
//		for (JCheckBox cb : checkBoxes.keySet())
//			if (cb.isSelected())
//				selected.add(checkBoxes.get(cb));
//		return selected;
//	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent evt) {
//		if (evt.getPropertyName() == CardsView.GRID_SELECTED_PROPERTY){
//			boolean enabled =  (boolean) evt.getNewValue();
//			
//			System.out.print("Grid selected property fired:  " +  enabled );
//			actionMap.get(RemoveAction.class).setEnabled(enabled);
//			actionMap.get(SearchInBrowserAction.class).setEnabled(enabled);
//		}
//		if (evt.getPropertyName() == CardsView.EMPTY_STATE_CHANGED){
//			boolean empty =  (boolean) evt.getNewValue();
//			emptyStateChanged(empty);
//		}
//
//	}
	

//	private void addCard(Card card, int quantity){
//		if (pricer.containsCard(card)){
//			String text = card.getName() + " is already in the card list.\n"
//					+ "Should the quantity of the "
//					+ "card be incremented by " + quantity + " ?";
//			String title = "Duplicity detected"; 
//			int response = JOptionPane.showConfirmDialog(window, text,
//					title, JOptionPane.YES_NO_OPTION);
//			if (response == JOptionPane.NO_OPTION)
//				return;
//		}
//		pricer.addCard(card, quantity);
//	}
	
}
