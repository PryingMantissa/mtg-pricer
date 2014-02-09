package bbc.juniperus.mtgp.gui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import bbc.juniperus.mtgp.tablemodel.Cell;

public class TheCellEditor extends DefaultCellEditor
	implements TableCellEditor {
	

	private static final long serialVersionUID = 1L;
	
	JSpinner spinner = new QuantitySpinner();
	Component editor;
	
	public TheCellEditor() {
		super(new JTextField());
	}

	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		
		if (editor instanceof JSpinner)
			return ((JSpinner) editor).getValue();
		
		if (editor instanceof JTextField)
			return ((JTextField) editor).getText();
		
		return spinner.getValue();
	}
	
	//Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
	                        int row, int column) {
		Cell cell = (Cell) value;
		String val = cell.toString();

		
		if (cell.getType() != Cell.Type.INTEGER){
			editor = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			return editor;
		}
		
		spinner.setValue(Integer.parseInt(val));
			
		editor = spinner;
		return spinner;
	}
}