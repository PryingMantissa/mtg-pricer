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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.ViewportLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.datastruc.Cell;
import bbc.juniperus.mtgp.datastruc.DataModel;
import bbc.juniperus.mtgp.utils.Stack;

public class CardsView extends JPanel {

	private static final long serialVersionUID = 1L;
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
	private Border trueEmpty = BorderFactory.createEmptyBorder();
	private Component orig;
	private DataModel data;
	
	public CardsView(DataModel data){   
		this.data = data;
		setUpTable();
		setUpGui();
	}
	
	public String getName(){
		return name;
	}
	
	public void prepare(){
		updateTable();
	}
	
	public void updateTable(){
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
	
	
	
	private Color gridColor = new Color(225,225,225);
	
	private void setUpTable(){
		table = new JTable(data);
		//ColumnModel cm = new ColumnModel(table.getColumnModel());
		//table.setColumnModel(cm);
		
		table.setModel(data);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		
		/*
		table.getTableHeader().setDefaultRenderer(
				new HeaderCellRenderer(table.getTableHeader().getDefaultRenderer()));
		*/
		
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setGridColor(gridColor);
		table.getColumnModel();
		table.setDefaultRenderer(Object.class, new CellRenderer());
		table.setShowHorizontalLines(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void columnMoved(TableColumnModelEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void columnMarginChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void columnAdded(TableColumnModelEvent e) {
				updateTable();
				
			}
		});
		
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
		//scrollPane.setBorder(boderGreen);
		scrollPane.getViewport().add(table);
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		
		//scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		
		//scrollPane.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		
		//scrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		
		//scrollPane.setBorder(BorderFactory.createLineBorder(gridColor, 1));
		scrollPane.setColumnHeader(new JViewport(){
			@Override public Dimension getPreferredSize() {
		        Dimension d = super.getPreferredSize();
		        d.height = 25;
		        return d;
			}
		});
		
		Border rb = BorderFactory.createLineBorder(Color.red);
		
		scrollPane.setBorder(trueEmpty);
		table.setBorder(null);
		//table.setBackground(Color.white);
		  
		
		//pan.add(table.getTableHeader(), BorderLayout.NORTH);
		//pan.add(table, BorderLayout.CENTER);
		add(scrollPane);
		//setBorder(BorderFactory.createCompoundBorder(Main.titledB,Main.emptyBorder));
	}
	
	Color colorLightGray = new Color(200,200,200);
	
	private class CellRenderer extends JLabel implements TableCellRenderer{

		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			
			
			Cell cell = (Cell) val;
			String text = cell.getText();
			
			JPanel panel = new JPanel(new BorderLayout());
			JLabel lbl = new JLabel();
			
			if (text == null){
				text = "-not found-";
				lbl.setForeground(Color.red);
			}
			lbl.setText(text);
			lbl.setHorizontalAlignment(cell.getColumnMeta().getAlligment());
			
			
			Border brdThinEmpty = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border brdDefEmpty = BorderFactory.createEmptyBorder(2,2,2,2);
			lbl.setBorder(brdDefEmpty);
			Color color;
			
			Border brdDashed = BorderFactory.createDashedBorder(Color.gray);
			
			Border brd = brdThinEmpty;

			if (isSelected)
				color = gridColor;
			else
				color = Color.WHITE;
			
			if (hasFocus)
				brd = brdDashed;
			
			
			
			setText("kokot");
			//setBorder(brd);
			
			panel.add(lbl);
			panel.setBackground(color);
			panel.setBorder(brd);
			
			return panel;
		}
		
	}
	
	
	
	
	
	
	
	
	private class HeaderCellRenderer extends DefaultTableCellRenderer{
		
		private static final long serialVersionUID = 1L;
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
	
	@Override
	public Dimension getPreferredSize(){
		Dimension dim = new Dimension();
		dim.width = table.getWidth();
		dim.height = 300;
		return dim;
	}
	
	
	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	/*
	private class ColumnModel extends DefaultTableColumnModel{
		
		
		ColumnModel(TableColumnModel model){
			for (int i = 0; i < model.getColumnCount() ; i++)
				addColumn(model.getColumn(i));
		}
		
		
		@Override
		public TableColumn getColumn(int colIndex){
			TableColumn col = super.getColumn(colIndex);
			
			//TableColumn col = new TableColumn();
			col.setHeaderValue("pica");
			
			System.out.println(col);
			return col;
		}
		
	}
	
	*/
	

}
