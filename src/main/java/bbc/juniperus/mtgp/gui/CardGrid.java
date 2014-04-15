package bbc.juniperus.mtgp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.tablemodel.Cell;
import bbc.juniperus.mtgp.tablemodel.PricerTableModel;

public class CardGrid extends JPanel {

	private static final long serialVersionUID = 1L;
	public static final String EMPTY_STATE_CHANGED = "empty_state_changed";
	public static String GRID_SELECTED_PROPERTY = "grid_selected_property";
	
	private JTable table;
	private JScrollPane scrollPane;
	private Border trueEmpty = BorderFactory.createEmptyBorder();
	private PricerTableModel tableModel;
	private Color selectColor = new Color(225,225,225);
	private boolean isGridSelected;
	private boolean isGridEmpty = true;
	private Set<GridListener> listeners = new HashSet<>();
	private InternalTableListener internalTableListener;
	
	public CardGrid(PricerTableModel tableModel){   
		this.tableModel = tableModel;
		internalTableListener = new InternalTableListener();
		setUpTable();
		setUpGui();
	}
	
	public void addGridListener(GridListener listener){
		listeners.add(listener);
	}
	
	public Collection<Card> getSelectedCards(){
		int[] rows= table.getSelectedRows();

		List<Card> l = new ArrayList<Card>();
		for (int i : rows)
			l.add(tableModel.getCardAt(i));
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
	
	
	//Remove later
	private class MyTable extends JTable{
		
		MyTable(TableModel model){
			super(model);
		}
		
		@Override
		public void tableChanged(TableModelEvent e){
			System.out.println("---------------------------    model changed");
			super.tableChanged(e);
		}
	}
	
	
	private void setUpTable(){
		
		table = new JTable(tableModel);
		table.addFocusListener(internalTableListener);
		table.getSelectionModel().addListSelectionListener(internalTableListener);
		table.setDefaultEditor(Cell.class, new TheCellEditor());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
		table.setRowSorter(sorter);
		sorter.setModel(tableModel);
		sorter.setComparator(0,new CellComparator());
		sorter.setComparator(1,new CellComparator());
		
		table.setDefaultRenderer(Object.class, new CellRenderer());
		
		
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		//table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		/*
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
				
				boolean empty = true;
				if (table.getRowCount() > 0)
					empty = false;
				
				System.out.println("Has focus ? " + table.hasFocus());
				String s = table.getSelectedRow() + "";
				System.out.println(s);
				
				if (empty != isGridEmpty){
					CardGrid.this.firePropertyChange(EMPTY_STATE_CHANGED, isGridEmpty, empty);
					isGridEmpty = empty;
				}
			}
		});
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				System.out.println("selection changed " + e.getFirstIndex() + "  " + table.getSelectedRow());
				int row = table.getSelectedRow();
				gridSelectionChanged(row > -1);
			}
		});
	
		*/
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
		System.out.println("CW: Grid selection changed ");
		firePropertyChange(GRID_SELECTED_PROPERTY, isGridSelected, isSelected);
		isGridSelected = isSelected;
	}
	
	
	public void setActionForKey(Action action, KeyStroke keyStroke){
		table.getInputMap().put(keyStroke, keyStroke.toString());
		table.getActionMap().put(keyStroke.toString(),action);
	}
	
	
	
	private class CellRenderer extends JLabel implements TableCellRenderer{

		private static final long serialVersionUID = 1L;

		
		private int getAllignment(Cell.Type type){
			if (type == Cell.Type.INTEGER
					|| type == Cell.Type.PRICE)
				return SwingConstants.RIGHT;
			else
				return SwingConstants.LEFT;
		}
		
		
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
			
			lbl.setHorizontalAlignment(getAllignment(cell.getType()));
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
			
			if (c1.getType() == Cell.Type.STRING)
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

	private class InternalTableListener implements ListSelectionListener, FocusListener{

		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() != table.getSelectionModel())
				throw new AssertionError();
			
			if (e.getValueIsAdjusting())
				return; //We are interested in finished selection.
			
			for (GridListener listener : listeners)
				listener.gridSelectionChanged(table.getSelectedRows());
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() != table)
				throw new AssertionError();
			for (GridListener listener : listeners)
				listener.gridFocusGained();
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() != table)
				throw new AssertionError();
			for (GridListener listener : listeners)
				listener.gridFocusLost();
		}
		
	}
	
}
