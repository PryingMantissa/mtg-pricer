package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import bbc.juniperus.mtgp.cardsearch.CardParser;
import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.cardsearch.Searcher;
import bbc.juniperus.mtgp.cardsearch.SearcherFactory;
import bbc.juniperus.mtgp.data.MtgTableModel;
import bbc.juniperus.mtgp.domain.Card;

public class Main implements PropertyChangeListener {
	
	private JFrame window;
	private JPanel tablePane;
	private CardsView view;
	private JPanel leftPane;
	private Pricer pricer;
	
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
		window.add(createLeftPane(), BorderLayout.WEST);
		window.add(tablePane, BorderLayout.CENTER);
		window.setJMenuBar(createMenuBar());
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
		tb.add(new JTextField());
		
		for (Component c : tb.getComponents())
			if (c instanceof AbstractButton)
				((AbstractButton)c).setFocusable(false);
		tb.setBorder(etchedBorder);
		return tb;
	}
	
	private JPanel createLeftPane(){
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
		}
		
		leftPane.setPreferredSize(new Dimension(180, leftPane.getPreferredSize().height));
		leftPane.setBorder(etchedBorder);
		return leftPane;
	}
	
	private void updateLeftPanel(final Searcher[] ss){
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
		JMenuItem aa = new JMenuItem(new AutoAction());
		pricingMenu.add(priceMI);
		pricingMenu.add(aa);
		
		menuBar.add(fileMenu);
		menuBar.add(pricingMenu);
		
		return menuBar;

	}
	
	private void createActions(){
		actionMap.put(ImportCardsAction.class, new ImportCardsAction());
		
		AbstractAction action = new StartSearchAction();
		action.setEnabled(false);
		actionMap.put(StartSearchAction.class, action);
		action = new AddAction();
		action.setEnabled(false);
		actionMap.put(AddAction.class, action);
		action = new RemoveAction();
		action.setEnabled(false);
		actionMap.put(RemoveAction.class, action);
		action = new ExportAction();
		action.setEnabled(false);
		actionMap.put(ExportAction.class, action);
		action = new SaveAction();
		action.setEnabled(false);
		actionMap.put(SaveAction.class, action);
	}
	
	private void setPricingActionsEnabled(boolean enabled){
		actionMap.get(AddAction.class).setEnabled(enabled);
		actionMap.get(ExportAction.class).setEnabled(enabled);
		actionMap.get(SaveAction.class).setEnabled(enabled);
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
	
	
	//============================ Devel ========================================
	
	public static void main(String[] args){
		Main m = new Main();
	}
	
	
	private void testView(){
		try {
			startNewPricing();
			CardParser cp = new CardParser();
			Map<Card, Integer> m = cp.parseFromFile(new File("d:\\deck.txt"));
			pricer.addCards(m);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//======================================================================
	
	private class AddAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		AddAction(){
			super("Add",iconAdd);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Adding");	
		}
		
	}
	
	private class RemoveAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;

		RemoveAction(){
			super("Remove",iconRemove);
		}
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Removing");
			pricer.removeCards(view.getSelectedCards());
		}
		
	}
	
	private class ExportAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		ExportAction(){
			super("Export",iconExport);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Exporting");
		}
		
	}
	
	private class SaveAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

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
			File f = fc.getSelectedFile();
			
			
			
			try{ 
				startNewPricing();
				if (f == null)
					return;
				CardParser cp = new CardParser();
				Map<Card, Integer> m = cp.parseFromFile(f);
				pricer.addCards(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

			
			List<Searcher> searchers = new ArrayList<Searcher>();
			
			searchers.add(cr);
			searchers.add(mv);
			searchers.add(fp);
			
			pricer.setSearchers(searchers);
			
			final Searcher[] ss = new Searcher[] {cr,mv,fp};
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					updateLeftPanel(ss);
					try {
						pricer.runLookUp();
						window.pack();
					//	view.prepare();
						
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
	
	private class AutoAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		AutoAction(){
			super("AutoAction");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
		
		}
		
	}


	
}
