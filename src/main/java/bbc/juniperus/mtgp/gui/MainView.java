package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

import bbc.juniperus.mtgp.cardsearch.SearchExecutor;
import bbc.juniperus.mtgp.gui.Controller.UserAction;

/**
 * The main class and the app entry point.
 */
public class MainView {
	
	
	private final static Color BORDER_COLOR = new Color(190,190,190);
	//private static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder();
	private static final Border LINE_BORDER = BorderFactory.createLineBorder(BORDER_COLOR);
	private static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
	private static final Border ETCHED_BORDER = BorderFactory.createCompoundBorder(PADDING_BORDER, LINE_BORDER);
	
	private static final int PADDING = 1;
	//private final static Border PADDING_BORDER = BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING);
	private static final int HEIGHT = 500;
 	private static final int WIDTH = 850;

 	private JPanel windowPane;
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
		windowPane = new JPanel(new BorderLayout());
		windowPane.setBorder(PADDING_BORDER);
		window.add(windowPane);
		this.controller = controller;
		setupGui();
	}
	
	public void show(){
		window.pack();
		window.setVisible(true);
	}
	
	public void searchStarted(SearchExecutor executor){
		findersPane.showSearchProgress(executor);
		addSpinner.setEnabled(false);
		addTextField.setEnabled(false);
		//cardGrid.setRowSelectionAllowed(false);
		window.pack();
	}
	
	
	public void newPricing(){
		addSpinner.setEnabled(true);
		addTextField.setEnabled(true);
		setNewFindersPane();
		findersPane.showFinderSettings();
	}
	
	
	public void clearAddCardTextField(){
		addTextField.setText("");
	}
	public void stopSearchIssued(){
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		findersPane.displayStoppingSearch();
	}
	
	public void searchStopped(){
		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		findersPane.displaySearchStopped();
		cardGrid.setRowSelectionAllowed(true);
	}
	
	public void searchFinished(){
		findersPane.displaySearchFinished();
		cardGrid.setRowSelectionAllowed(true);
	}
	
	
	/**
	 * Shows modal 'About' dialog.
	 */
	public void showAbout() {
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
		cardGrid.registerAction(controller.getAction(UserAction.REMOVE_CARD));
		tablePane.add(cardGrid);
		
		toolBar = createToolBar();
		windowPane.add(toolBar, BorderLayout.NORTH);
		
		windowPane.add(tablePane, BorderLayout.CENTER);
		window.setJMenuBar(createMenuBar());
		
	}
	
	private void setNewFindersPane(){
		if (findersPane != null)
			windowPane.remove(findersPane);
		findersPane = new CardFindersPane(controller);
		findersPane.setBorder(ETCHED_BORDER);
		windowPane.add(findersPane, BorderLayout.WEST);
		windowPane.revalidate();
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
		mi = new JMenuItem("About MtGPricer");
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
	
}
