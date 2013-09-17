package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
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
import bbc.juniperus.mtgp.utils.Stack;

public class View extends JPanel {

	private static final long serialVersionUID = 1L;
	private Pricer pricer;
	private JTable table;
	private String name;
	private JScrollPane scrollPane;
	private int t = 5;
	private Border boderRed = BorderFactory.createLineBorder(Color.red, t);
	private Border boderGreen = BorderFactory.createLineBorder(Color.GREEN,t);
	private Border boderBlue = BorderFactory.createLineBorder(Color.blue, t);
	private Border borderHeader = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.gray);
	private Border emptyBorder = BorderFactory.createEmptyBorder(t,t, t, t);
	private Border lowB = BorderFactory.createLoweredBevelBorder();
	
	private Component orig;
	
	
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
	}
	
	public String getName(){
		return name;
	}
	

	
	
	public void prepare(){
		autoWidthColumns();
		//System.out.println(table.getTableHeader().getDefaultRenderer());
		//System.out.println(UIManager.getDefaults().getUIClass("TableHeader"));

		//pricer.data().fireTableStructureChanged();
		
		//System.out.println("cc model " + table.getModel().getColumnCount());
	}
	
	private void autoWidthColumns(){
		
		int margin =5;
		for (int i = 0; i < table.getColumnCount();i++){	
			
			TableColumn col = table.getColumnModel().getColumn(i);
			
			TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
	
	        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
	        
	        //System.out.println(comp);
	        
	        int width = comp.getPreferredSize().width;
	        System.out.println("col " + i + " width " + width);
			
	        
			for (int row = 0; row < table.getRowCount(); row++) {
			     renderer = table.getCellRenderer(row, i);
			     comp = table.prepareRenderer(renderer, row, i);
			     width = Math.max (comp.getPreferredSize().width, width);
			     }
			
			width+= margin;
			col.setPreferredWidth(width);
		}
	}
	
	
	private void setUpTable(){
		table = new JTable(pricer.data());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		table.setGridColor(Color.red);
		//table.setBorder(boderRed);
		//table.getTableHeader().setBackground(Color.white);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(Color.LIGHT_GRAY);
		
		//table.getColumn(null).getHeaderRenderer();
		
		//table.getTableHeader().setBackground(Color.yellow);
		//table.getTableHeader().setBorder(boderRed);
		
		table.getTableHeader().setDefaultRenderer(
				new HeaderCellRenderer(table.getTableHeader().getDefaultRenderer()));
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	
	private void setUpGui(){
		setLayout(new BorderLayout());
		//setBackground(Color.white);
		//setBorder(lowB);
		
		scrollPane = new JScrollPane();
		//table.setBorder(emptyBorder);
		
		JPanel pan = new JPanel(new BorderLayout());
		//pan.setBorder(lowB);
		//table.setGridColor(Color.blue);
		//pan.add(table, BorderLayout.WEST);
		
		scrollPane.getViewport().setBackground(Color.white);
		scrollPane.setBorder(boderGreen);
		scrollPane.getViewport().add(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(null);
		
		
		scrollPane.setColumnHeader(new JViewport(){
			@Override public Dimension getPreferredSize() {
		        Dimension d = super.getPreferredSize();
		        d.height = 25;
		        return d;
			}
		});
		
		//table.setBackground(Color.white);
		
		
		//pan.add(table.getTableHeader(), BorderLayout.NORTH);
		//pan.add(table, BorderLayout.CENTER);
		add(scrollPane);
	}
	
	public Pricer pricer(){
		return pricer;
	}
	
	
	private class HeaderCellRenderer extends DefaultTableCellRenderer{
		
		
		TableCellRenderer  o;
		
		public HeaderCellRenderer(TableCellRenderer  original){
			o = original;
		}
		
		@Override
	    public Component getTableCellRendererComponent(
	            JTable table, Object value, boolean isSelected,
	            boolean hasFocus, int row, int column) {

	        // returns component used for default header rendering
	        // makes it independent on current L&F

	        Component retr = o.getTableCellRendererComponent(
	                table, value, isSelected, hasFocus, row, column);
	        if ( JLabel.class.isAssignableFrom(retr.getClass()) ) {

	            JLabel jl = (JLabel) retr;
	           // jl.setText("" + jl.getText());
	            jl.setFont(jl.getFont().deriveFont(Font.BOLD));
	           // jl.setHorizontalAlignment(SwingConstants.CENTER);
	            jl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray));
	            
	        
	            //jl.setPreferredSize(null);
	            
	            
	        }
	        return retr;

	    }
		
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
