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
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.ViewportLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import bbc.juniperus.mtgp.cardsearch.Pricer;
import bbc.juniperus.mtgp.data.MtgTableModel;
import bbc.juniperus.mtgp.data.viewmodel.Cell;
import bbc.juniperus.mtgp.data.viewmodel.Cell.Type;
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
	private MtgTableModel data;
	private Color gridColor = new Color(225,225,225);

	public CardsView(MtgTableModel data){   
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
		setColumnsAutoWidth();
		//System.out.println(table.getTableHeader().getDefaultRenderer());
		//System.out.println(UIManager.getDefaults().getUIClass("TableHeader"));

		//pricer.data().fireTableStructureChanged();
		
		//System.out.println("cc model " + table.getModel().getColumnCount());
	}
	
	private void setColumnsAutoWidth(){
		final int margin =2;
		
		System.out.println("auto w called");
		final TableColumnModel model = table.getColumnModel();

		
		final int w1 = getMaxColumnWidth(0);
		int ew2 = -97;
		if (model.getColumnCount() > 1)
			ew2 = getMaxColumnWidth(1);
		
		final int w2= ew2;
			
		
		//Find out the widest cell among the result rows.
		int w = 0;
		for (int col = 2; col < model.getColumnCount(); col++){
			w =  Math.max(w, getMaxColumnWidth(col));
		}
		
		final int fW = w; 
		System.out.println("mc1 " + w1);
		System.out.println("mc2 " + w2);
		System.out.println("w is " + w);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("updating "   + model.getColumnCount());
				model.getColumn(0).setPreferredWidth(w1 + margin);
				
				if (model.getColumnCount() < 2){
					table.validate();
					table.repaint();
					return;
				}
				model.getColumn(1).setPreferredWidth(w2 + margin);
				for (int col = 2; col < table.getColumnCount(); col++){
					model.getColumn(col).setPreferredWidth(fW+ margin);
				}
				
	
				
			}
		});

	}
	
	private int getMaxColumnWidth(int column){
		int width = 0;
		//For header.
		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        Component comp = headerRenderer.getTableCellRendererComponent(
        		table, table.getColumnName(column), false, false, 0, 0);
        width = Math.max(width,comp.getPreferredSize().width);
        //Loop all rows and look for widest cell so far.
		for (int row = 0; row < table.getRowCount(); row++) {
			TableCellRenderer  renderer = table.getCellRenderer(row, column);
			comp = table.prepareRenderer(renderer, row, column);
		    width = Math.max (comp.getPreferredSize().width, width);
		}
		return width;
	}
	
	private void setUpTable(){
		table = new JTable(data);
		table.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				System.out.println("table changed");
			//	setColumnsAutoWidth();
				
			}
		});
		
		table.setModel(data);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		//table.setAutoCreateRowSorter(true);
		
		 TableRowSorter sorter = new TableRowSorter(){
			 
			 private void ewew(){
		
			 }
		 };
		 table.setRowSorter(sorter);
		 sorter.setModel(table.getModel());
		 sorter.setComparator(0,new CellComparator());
		 sorter.setComparator(1,new CellComparator());
		//table.setRowSorter(sorter);
		
		table.setDefaultRenderer(Object.class, new CellRenderer());
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {}
			@Override
			public void columnRemoved(TableColumnModelEvent e) {}
			@Override
			public void columnMoved(TableColumnModelEvent e) {}
			@Override
			public void columnMarginChanged(ChangeEvent e) {}
			@Override
			public void columnAdded(TableColumnModelEvent e) {
				TableRowSorter trs = (TableRowSorter) table.getRowSorter();
				
				for (int i = e.getFromIndex() ; i <= e.getToIndex() ;i++)
					trs.setComparator(i, new CellComparator());
				setColumnsAutoWidth();
				
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
	
	
	
	private class CellRenderer extends JLabel implements TableCellRenderer{

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			
			Cell cell = (Cell) val;
			String text = cell.getText();
			
			JPanel panel = new JPanel(new BorderLayout());
			JLabel lbl = new JLabel();
			
			if (cell.getType() == Cell.Type.NA){
				text = "not found";
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

			panel.add(lbl);
			panel.setBackground(color);
			panel.setBorder(brd);
			
			return panel;
		}
		
	}
	
	
	private class CellComparator implements Comparator<Cell>{

		@Override
		public int compare(Cell c1, Cell c2) {
			
			//If the first is not loaded
			if (c1.getType() == Cell.Type.NOT_LOADED)
				//..and the second is not.
				if (c2.getType() != Cell.Type.NOT_LOADED)
					return -1;
				//if both are NA.
				else
					return 0;
			//If only second is NA.
			if (c2.getType() == Cell.Type.NOT_LOADED)
				return 1;
			
			//If the first is NA...
			if (c1.getType() == Cell.Type.NA)
				//..and the second is not.
				if (c2.getType() != Cell.Type.NA)
					return -1;
				//if both are NA.
				else
					return 0;
			//If only second is NA.
			if (c2.getType() == Cell.Type.NA)
				return 1;
			
			
			if (c1.getType() != c2.getType())
				throw new IllegalArgumentException("Cells are not of the same type!");
			
			if (c1.getType() == Cell.Type.TEXT)
				return c1.getText().compareTo(c2.getText());
			
			if (c1.getType() == Cell.Type.PRICE){
				Double d1 = Double.parseDouble(c1.getText().substring(0,5));
				Double d2 = Double.parseDouble(c1.getText().substring(0,5));
				return d1.compareTo(d2); 
			}
			if (c1.getType() == Cell.Type.INTEGER){
				Integer i1 = Integer.parseInt(c1.getText());
				Integer i2 = Integer.parseInt(c2.getText());
				return i1.compareTo(i2); 
			}
			
			throw new IllegalArgumentException("We were not supposed to get here");
		}
		
	}
}
