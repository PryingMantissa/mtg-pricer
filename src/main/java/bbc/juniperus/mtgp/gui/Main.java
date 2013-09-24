package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableModel;

import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.SearcherFactory;

public class Main {
	
	private JFrame window;
	private JPanel parentView;
	private JTabbedPane tabPane;
	private View view;
	private JPanel resultsPane;
	
	private Map<Class<? extends AbstractAction>,AbstractAction> actionMap 
					= new HashMap<Class<? extends AbstractAction>,AbstractAction>();
	static int t =4;
	static Border emptyBorder = BorderFactory.createEmptyBorder(t,t, t, t);
	static Border defBorder = BorderFactory.createLineBorder(Color.gray);
	static Border leBord = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	static Border etr = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	public static  Border titledB = BorderFactory.createTitledBorder(etr, "Results");
	
	public Main(){
		createActions();
		setupGui();
		show();
		
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				testView();
			}
			
		});
		
	}
	
	private void testView(){
		View view = new View("Test");
		try {
			view.loadCardListFromFile(new File("d:\\deck.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(view.pricer().data().stringify());
		
		addView(view);
		view.prepare();
	}
	
	private void setupGui(){
		
		setLookAndFeel();
		window = new JFrame();
		parentView = new JPanel(new BorderLayout());
		window.setTitle("Mtg pricer");
		window.setSize(600, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		
		parentView = new JPanel(new BorderLayout());
		parentView.setBorder(emptyBorder);
		
		createMenuBar();
		
		resultsPane = new JPanel(new BorderLayout());
		
		resultsPane.setBorder(BorderFactory.createCompoundBorder(emptyBorder, titledB));
		
		
		resultsPane.setVisible(false);
		
		window.add(parentView, BorderLayout.CENTER);
		window.add(resultsPane, BorderLayout.SOUTH);
		
		
	}
	
	private Component getFilePicker(){
		return null;
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
	
	private void createMenuBar(){
		JMenuBar  menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu();

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.setDisplayedMnemonicIndex(0);

		JMenuItem importMI = new JMenuItem(actionMap.get(ImportCardsAction.class));
		fileMenu.add(importMI);
		
		JMenu pricingMenu = new JMenu();

		pricingMenu = new JMenu("Price");
		pricingMenu.setMnemonic(KeyEvent.VK_P);
		pricingMenu.setDisplayedMnemonicIndex(0);

		JMenuItem priceMI = new JMenuItem(actionMap.get(StartSearchAction.class));
		pricingMenu.add(priceMI);
		
		menuBar.add(fileMenu);
		menuBar.add(pricingMenu);
		window.add(menuBar, BorderLayout.NORTH);
	}
	
	public void createActions(){
		actionMap.put(ImportCardsAction.class, new ImportCardsAction());
		actionMap.put(StartSearchAction.class, new StartSearchAction());
	}
	
	public void show(){
		window.setVisible(true);
	}
	

	public void addView(View view){
		this.view = view;
		parentView.removeAll();
		parentView.add(view);
		window.validate();
	}
	
	public static void main(String[] args){
		new Main();
	}
	
	
	private class ImportCardsAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		ImportCardsAction(){
			super("Import card list");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//Create a file chooser
			final JFileChooser fc = new JFileChooser();
			//In response to a button click:
			fc.showOpenDialog(window);
			
			View view = new View("Test");
			try {
				view.loadCardListFromFile(fc.getSelectedFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(view.pricer().data().stringify());
			view.prepare();
			addView(view);
			
			
		}
		
	}
	
	private class StartSearchAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		StartSearchAction(){
			super("Start search");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			final Searcher cr = SearcherFactory.getCernyRytirPricer();
			final Searcher mv = SearcherFactory.getModraVeverickaPricer();
			final Searcher fp = SearcherFactory.getDragonPricer();

			view.pricer().addSearcher(cr);
			view.pricer().addSearcher(mv);
			view.pricer().addSearcher(fp);
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					
					ResultsPanel resultsPanel = new ResultsPanel(view.pricer());
					
					resultsPanel.addFeedBackRow(cr);
					resultsPanel.addFeedBackRow(mv);
					resultsPanel.addFeedBackRow(fp);
					resultsPane.add(resultsPanel, BorderLayout.WEST);
					resultsPane.revalidate();
					resultsPane.setVisible(true);
					try {
						view.pricer().runLookUp();
						view.prepare();
						
						System.out.println(view.pricer().data().stringify());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			t.start();
			//getActiveView().validate();
			
		}
		
	}
	
}
