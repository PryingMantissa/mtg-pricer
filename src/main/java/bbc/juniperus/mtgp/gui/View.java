package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.ViewportLayout;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.datastruc.DataModel;

public class View extends JPanel {

	private static final long serialVersionUID = 1L;
	private Pricer pricer;
	private JTable table;
	private String name;
	private JScrollPane scrollPane;
	private int t = 7;
	private Border boderRed = BorderFactory.createLineBorder(Color.red, t);
	private Border boderGreen = BorderFactory.createLineBorder(Color.GREEN,t);
	private Border boderBlue = BorderFactory.createLineBorder(Color.blue, t);
	private Border borderHeader = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.gray);
	private Border emptyBorder = BorderFactory.createEmptyBorder(t,t, t, t);
	private Border lowB = BorderFactory.createEtchedBorder();
	
	
	
	public View(String name, DataModel savedSearchData){
		pricer = new Pricer(savedSearchData);
		this.name = name;
		setUpTable();
		setUpGui();
	}

	public View(String name){   
		pricer = new Pricer();
		this.name = name;
		setUpTable();
		setUpGui();
	}
	
	
	public void loadCardListFromFile(File file) throws IOException, ParseException{
		pricer.loadCardsFromFile(file);
		
		int c = table.getColumnCount();
		System.out.println("cc " + c);
	}
	
	public String getName(){
		return name;
	}
	

	
	
	public void prepare(){
		autoWidthColumns();
		System.out.println(table.getTableHeader().getDefaultRenderer());
		
		
		System.out.println(UIManager.getDefaults().getUIClass("TableHeader"));
		//System.out.println("cc model " + table.getModel().getColumnCount());
		System.out.println("cc " + table.getColumnCount());
	}
	
	private void autoWidthColumns(){
		
	int margin =5;
	for (int i = 0; i < table.getColumnCount();i++){	
		
		TableColumn col = table.getColumnModel().getColumn(i);
		
		TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();

        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

        int width = comp.getPreferredSize().width;
		
		for (int row = 0; row < table.getRowCount(); row++) {
		     renderer = table.getCellRenderer(row, i);
		     comp = table.prepareRenderer(renderer, row, i);
		     width = Math.max (comp.getPreferredSize().width, width);
		     }
		
		width+= margin;
		System.out.println(width);
		col.setPreferredWidth(width);
	}
	}
	
	
	private void setUpTable(){
		table = new JTable(pricer.data());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		//table.setBorder(boderRed);
		//table.getTableHeader().setBackground(Color.white);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(Color.LIGHT_GRAY);
		
		//table.getTableHeader().setBackground(Color.yellow);
		//table.getTableHeader().setBorder(boderRed);
		
		
		table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1,
					boolean arg2, boolean arg3, int arg4, int arg5) {
				
				
				System.out.println(arg1.getClass());
				
				setText(arg1.toString());
				setHorizontalAlignment(JLabel.CENTER);
				
				Border eb = BorderFactory.createEmptyBorder(1,1,0,0);
				setBackground(Color.lightGray);
				setBorder(borderHeader);
				
				JPanel p = new JPanel(new BorderLayout());
				
				
				JLabel a = new JLabel("hore");
				JLabel b = new JLabel("dole");
				
				
				a.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,Color.gray));
				b.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,Color.gray));
				
				
				p.add(a, BorderLayout.NORTH);
				p.add(b, BorderLayout.CENTER);
				
				return p;
			}
		});
		
		
		
		
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}
	
	
	private void setUpGui(){
		setLayout(new BorderLayout());
		setBackground(Color.white);
		setBorder(emptyBorder);
		
		scrollPane = new JScrollPane();
		//table.setBorder(emptyBorder);
		
		JPanel pan = new JPanel(new BorderLayout());
		//pan.setBorder(lowB);
		//table.setGridColor(Color.blue);
		//pan.add(table, BorderLayout.WEST);
		
		scrollPane.getViewport().setBackground(Color.white);
		scrollPane.setBorder(boderGreen);
		scrollPane.setBackground(Color.green);
		scrollPane.getViewport().add(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(null);
		//table.setBackground(Color.white);
		add(scrollPane);
	}
	
	public Pricer pricer(){
		return pricer;
	}
	
	
	private class MtgColumnModel extends DefaultTableColumnModel {
		
	}
	
	
	private class Kokot implements TableCellRenderer{

		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
