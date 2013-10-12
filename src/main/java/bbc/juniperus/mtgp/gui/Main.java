package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardParser;
import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.SearcherFactory;
import bbc.juniperus.mtgp.data.MtgTableModel;
import bbc.juniperus.mtgp.domain.Card;

public class Main {
	
	private JFrame window;
	private JPanel tableView;
	private JTabbedPane tabPane;
	private CardsView view;
	private JPanel resultsPane;
	private JSplitPane sp;
	private JPanel leftPane;
	private Pricer pricer;
	
	private Map<Class<? extends AbstractAction>,AbstractAction> actionMap 
					= new HashMap<Class<? extends AbstractAction>,AbstractAction>();
	
	static int t = 4;
	static Border eb = BorderFactory.createEmptyBorder(t,t,t,t);
	
	static Border eb1 = BorderFactory.createEmptyBorder(0,0,2,0);
	static Border tbEb = BorderFactory.createEmptyBorder(0,1,0,1);
	static Border eb2 = BorderFactory.createEmptyBorder(2,2,2,2);	
	static Border db = BorderFactory.createLineBorder(Color.gray);
	
	static Border emptyBorder = BorderFactory.createEmptyBorder(0,0,t,0);
	static int s = 1;
	static Border emptyBorder2 = BorderFactory.createEmptyBorder(0,0,t,s);
	static Border defBorder = BorderFactory.createLineBorder(Color.gray);
	static Border leBord = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	static Border etr = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	public static  Border titledB = BorderFactory.createTitledBorder(etr, "Results");
	
	static Border simpB = BorderFactory.createLineBorder(Color.gray);
	
	private Searcher[] searchers = SearcherFactory.getAll();
	
	private static int h = 20;
	private static int w = 20;
	
	static final ImageIcon iconAdd = loadIcon("/icons/014.png",w,h);
	static final ImageIcon iconRemove = loadIcon("/icons/013.png",w,h);
	static final ImageIcon iconImport = loadIcon("/icons/083.png",w,h);
	static final ImageIcon iconExport = loadIcon("/icons/103.png",w,h);
	static final ImageIcon iconSave = loadIcon("/icons/095.png",w,h);
	
	Border bl = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	
	
	public Main(){
		
		createActions();
		setupGui();
		show();
		pricer = new Pricer();
		
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				testView();
			}
			
		});
		
		
		
	}
	
	private void testView(){
		try {
			CardParser cp = new CardParser();
			Map<Card, Integer> m = cp.parseFromFile(new File("d:\\deck.txt"));
			pricer.addCards(m);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MtgTableModel tableModel = new MtgTableModel(pricer.data());
		CardsView view = new CardsView(tableModel);

	
		addView(view);
		view.prepare();
	}
	
	private void setupGui(){
		
		setLookAndFeel();
		window = new JFrame();
		window.setTitle("Mtg pricer");
		window.setSize(600, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		createMenuBar();
		
		JPanel parentPane = new JPanel(new BorderLayout());
		
		tableView = new JPanel(new BorderLayout());
		//tableView.setBorder(eb2);
		//tableView.setBorder(bl);
		resultsPane = new JPanel(new BorderLayout());
		resultsPane.setBorder(BorderFactory.createCompoundBorder(emptyBorder2, titledB));
		resultsPane.setVisible(false);
		
		Border b = BorderFactory.createEtchedBorder();
		//parentPane.setBorder(b);
		
		/*
		sp = new JSplitPane();
		sp.setBorder(BorderFactory.createCompoundBorder(eb1,b));
		sp.setResizeWeight(1);
		sp.setLeftComponent(createLeftPane());
		sp.setRightComponent(tableView);
		*/
		//parentPane.add(tableView, BorderLayout.CENTER);
		window.add(createToolBar(), BorderLayout.NORTH);
		//parentPane.add(createLeftPane(),BorderLayout.WEST);
		
		Border br = BorderFactory.createEtchedBorder();
		
		tableView.setBorder(br);
		JPanel cp = createLeftPane();
		cp.setBorder(br);
		window.add(cp, BorderLayout.WEST);
		window.add(tableView, BorderLayout.CENTER);
		
	}
	
	private Component createToolBar(){
		JToolBar tb = new JToolBar();
		
		tb.setFocusable(false);
		tb.add(actionMap.get(ImportCardsAction.class));
		tb.addSeparator(new Dimension(10,20));
		tb.add(actionMap.get(ExportAction.class));
		tb.add(actionMap.get(SaveAction.class));
		tb.addSeparator(new Dimension(10,20));
		tb.add(actionMap.get(AddAction.class));
		tb.add(actionMap.get(RemoveAction.class));
		
		for (Component c : tb.getComponents())
			if (c instanceof AbstractButton)
				((AbstractButton)c).setFocusable(false);
		
		
		//TODO refactor
		Border br = BorderFactory.createEtchedBorder();
		tb.setBorder(br);
		return tb;
	}
	
	
	private JPanel createLeftPane(){
		MigLayout ml = new MigLayout();
		ml.setRowConstraints("[]10[]0[]");
		leftPane = new JPanel(ml);
		JLabel lbl = new JLabel("Card pricing sources:");
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		leftPane.add(lbl,"wrap");
		
		for (Searcher s: searchers){
			lbl = new JLabel(s.getName());
			//lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
			lbl.setToolTipText(s.getURL());
			leftPane.add(lbl);
			JCheckBox cb = new JCheckBox();
			cb.setSelected(true);
			leftPane.add(cb,"wrap");
		}
		return leftPane;
	}
	
	private void modifyLeftPanel(Searcher[] ss){
		leftPane.removeAll();
		for (Searcher s: ss){
			StatusRow sp = new StatusRow(pricer,s);
			leftPane.add(sp,"wrap");
		}
		window.revalidate();
		window.repaint();
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
		window.setJMenuBar(menuBar);
	}
	
	public void createActions(){
		actionMap.put(ImportCardsAction.class, new ImportCardsAction());
		actionMap.put(StartSearchAction.class, new StartSearchAction());
		actionMap.put(AddAction.class, new AddAction());
		actionMap.put(RemoveAction.class, new RemoveAction());
		actionMap.put(ExportAction.class, new ExportAction());
		actionMap.put(SaveAction.class, new SaveAction());
	}
	
	public void show(){
		window.setVisible(true);
	}
	
	public void addView(final CardsView view){
		this.view = view;
		tableView.removeAll();
		tableView.add(view);
		window.revalidate();
	
	}
		
	private static ImageIcon loadIcon(String path, int width, int height){
		URL url = Main.class.getResource(path);
		ImageIcon icon = new ImageIcon(url);
		Image img = icon.getImage();
		img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		icon.setImage(img);
		return icon;
	}
	
	
	public static void main(String[] args){
		Main m = new Main();
	}
	
	private class AddAction extends AbstractAction{

		AddAction(){
			super("Add",iconAdd);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Adding");
			
		}
		
	}
	
	private class RemoveAction extends AbstractAction{
		
		RemoveAction(){
			super("Remove",iconRemove);
		}
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Removing");
			
		}
		
	}
	
	private class ExportAction extends AbstractAction{

		ExportAction(){
			super("Export",iconExport);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Exporting");
			
		}
		
	}
	
	private class SaveAction extends AbstractAction{

		SaveAction(){
			super("Save",iconSave);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Saving");
			
		}
		
	}
	
	private class ImportCardsAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		ImportCardsAction(){
			super("Import card list",iconImport);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//Create a file chooser
			final JFileChooser fc = new JFileChooser();
			//In response to a button click:
			fc.showOpenDialog(window);
			
			
			try {
				CardParser cp = new CardParser();
				Map<Card, Integer> m = cp.parseFromFile(fc.getSelectedFile());
				pricer.addCards(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MtgTableModel md = new MtgTableModel(pricer.data());
			CardsView view = new CardsView(md);
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

			pricer.addSearcher(cr);
			pricer.addSearcher(mv);
			pricer.addSearcher(fp);
			
			final Searcher[] ss = new Searcher[] {cr,mv,fp};
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					modifyLeftPanel(ss);
					try {
						pricer.runLookUp();
						view.prepare();
						
						//System.out.println(pricer.data().stringify());
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
