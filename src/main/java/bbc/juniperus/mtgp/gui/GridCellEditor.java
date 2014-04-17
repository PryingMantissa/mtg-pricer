package bbc.juniperus.mtgp.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import bbc.juniperus.mtgp.tablemodel.Cell;

@SuppressWarnings("serial")
public class GridCellEditor extends AbstractCellEditor implements TableCellEditor{
	
	private final JSpinner spinner = new QuantitySpinner();
	private final JTextField textField = new JTextField();
	private Component editor; 
	private String originalValue;
	
	public GridCellEditor() {
	
	}

	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		System.out.println("getting edti value");
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
		String val = cell.getText();
		originalValue = val;
		
		
		if (cell.getType() == Cell.Type.INTEGER){
			spinner.setValue(Integer.parseInt(val));
			editor = spinner;
		}else{
			textField.setText(val);
			editor = textField;
		}
		
		
		return editor;
	}
	
	
	
	
	@Override
	public boolean isCellEditable(EventObject e) {
	    if (e instanceof MouseEvent) {
            return ((MouseEvent)e).getClickCount() >= 2;
        }
		return true;
	}


	@Override
	public boolean stopCellEditing() {
		if (getCellEditorValue().equals(originalValue))
			cancelCellEditing();
		
		
		return super.stopCellEditing();
	}
	
	
	
}