package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import bbc.juniperus.mtgp.data.MtgTableModel;
import bbc.juniperus.mtgp.data.viewmodel.Cell;
import bbc.juniperus.mtgp.domain.Card;

public class CardsView extends JPanel {

	private static final long serialVersionUID = 1L;
	public static String GRID_SELECTED_PROPERTY = "grid_selected_property";
	
	private JTable table;
	private JScrollPane scrollPane;
	private Border trueEmpty = BorderFactory.createEmptyBorder();
	private MtgTableModel model;
	private Color selectColor = new Color(225,225,225);
	private boolean isGridSelected;
	
	public CardsView(MtgTableModel data){   
		this.model = data;
		setUpTable();
		setUpGui();
	}
	
	
	public Collection<Card> getSelectedCards(){
		int[] rows= table.getSelectedRows();

		List<Card> l = new ArrayList<Card>();
		for (int i : rows)
			l.add(model.getCardAt(i));
		return l;
	}
	
	private void setColumnsAutoWidth(){
		
		final int margin =2;
		final TableColumnModel model = table.getColumnModel();
		
		final int w1 = getMaxColumnWidth(0);
		int w2 = -97;
		if (model.getColumnCount() > 1)
			w2 = getMaxColumnWidth(1);
		
		//Find out the widest cell among the result rows.
		int w = 0;
		for (int col = 2; col < model.getColumnCount(); col++){
			w =  Math.max(w, getMaxColumnWidth(col));
		}
		
		final int fW2= w2;
		final int fW = w; 
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				model.getColumn(0).setPreferredWidth(w1 + margin);
				model.getColumn(1).setPreferredWidth(fW2 + margin);
				for (int col = 2; col < table.getColumnCount(); col++)
					model.getColumn(col).setPreferredWidth(fW+ margin);
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
		table = new JTable(model);
		table.setDefaultEditor(Object.class, new TheCellEditor());
		table.setModel(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
		table.setRowSorter(sorter);
		sorter.setModel((MtgTableModel) table.getModel());
		sorter.setComparator(0,new CellComparator());
		sorter.setComparator(1,new CellComparator());
		
		table.setDefaultRenderer(Object.class, new CellRenderer());
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		//table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
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
				@SuppressWarnings("unchecked")
				TableRowSorter<TableModel> trs = (TableRowSorter<TableModel>) table.getRowSorter();
				
				for (int i = e.getFromIndex() ; i <= e.getToIndex() ;i++)
					trs.setComparator(i, new CellComparator());
				setColumnsAutoWidth();
				
			}
		});
		
		table.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				//If we delete row. No row is selected.
				if (e.getType() == TableModelEvent.DELETE)
					gridSelectionChanged(false);
			}
		});
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				System.out.println("selection changed " + e.getFirstIndex() + "  " + table.getSelectedRow());
				int row = table.getSelectedRow();
				System.out.println(row > -1);
				gridSelectionChanged(row > -1);
			}
		});
		
		
		//table.getColumnModel().getColumn(0).setCellEditor(new QuantityEditor());
		
		
		
	}
	
	private void setUpGui(){
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		scrollPane.getViewport().setBackground(Color.white);
		scrollPane.getViewport().add(table);
		scrollPane.setColumnHeader(new JViewport(){
			private static final long serialVersionUID = 1L;

			@Override public Dimension getPreferredSize() {
		        Dimension d = super.getPreferredSize();
		        d.height = 25;
		        return d;
			}
		});
		scrollPane.setBorder(trueEmpty);
		table.setBorder(null);
		add(scrollPane);
	}
	
	/**
	 * Let the main class know that grid is selected and that corresponding
	 * actions can be enabled.
	 * @param isSelected
	 */
	private void gridSelectionChanged(boolean isSelected){
		if (isSelected == isGridSelected)
			return;
		System.out.println("grid secl ch " + isSelected);
		this.firePropertyChange(GRID_SELECTED_PROPERTY, isGridSelected, isSelected);
		isGridSelected = isSelected;
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
			
			Border brd = brdThinEmpty;

			if (isSelected)
				color = selectColor;
			else
				color = Color.WHITE;
			
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


	
	
	public MtgTableModel tableModel() {
		return model;
	}
	
	
	
	
}
