package bbc.juniperus.mtgp.tablemodel;

import java.util.HashMap;
import java.util.Map;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.tablemodel.MtgPricerTableModel.PricerColumn;

/**
 * Generator of string reports in various formats. Used for export functionality
 * or also for CLI usage.
 */
public class ReportCreator {

	private MtgPricerTableModel tableModel;

	public ReportCreator(MtgPricerTableModel tableModel) {
		this.tableModel = tableModel;
	}

	/**
	 * Creates card pricing table in normal txt format.
	 * 
	 * @return txt format card - price table.
	 */
	public String generateTxtReport() {

		
		
		int rowCount = tableModel.getRowCount();
		int colCount = tableModel.getColumnCount();

		// Find out maximum widths.
		Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>();
		for (int column = 0; column < colCount; column++) {
			int maxWidth = tableModel.getColumnName(column).length();
			for (int row = 0; row < rowCount; row++) {
				Cell val = (Cell) tableModel.getValueAt(row, column);
				maxWidth = Math.max(maxWidth, val.getText().length());
			}
			columnWidths.put(column, maxWidth);
		}
		
		//Ensure the width of the first column is at least the size of the last row label.
		String totalStr = "Total price";
		if (columnWidths.get(0) < totalStr.length())
			columnWidths.put(0, totalStr.length());
		

		StringBuilder sb = new StringBuilder();
		// Write headers.
		for (int i = 0; i < colCount; i++) {
			String text = alignLeft(tableModel.getColumnName(i),
					columnWidths.get(i));
			sb.append(text);
			if (i - 1 < colCount)
				sb.append(" | ");
		}
		sb.append("\n");
		// Separating line.
		for (int i = 0; i < colCount; i++) {
			for (int j = 0; j < columnWidths.get(i); j++)
				sb.append("-");
			if (i - 1 < colCount)
				sb.append(" | ");
		}
		sb.append("\n");

		// Write rest of values.
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				sb.append(getPaddedValue(i, j, columnWidths.get(j)));

				if (j - 1 < colCount)
					sb.append(" | ");
			}
			sb.append("\n");
		}


		// Separating line.
		for (int i = 0; i < colCount; i++) {
			for (int j = 0; j < columnWidths.get(i); j++)
				sb.append("-");
			if (i - 1 < colCount)
				sb.append(" | ");
		}
		sb.append("\n");
		
		//TODO add totals
		
		return sb.toString();
	}

	/**
	 * Creates card pricing report table in CSV format
	 * 
	 * @param separator
	 *            CSV separator character
	 * @return string representing the CSV report/table of the priced cards
	 */
	public String createCSVReport(String separator) {

		StringBuilder sb = new StringBuilder();

		//Headers
		for (int i = 0; i < tableModel.getColumnCount(); i++)
			sb.append(tableModel.getColumnName(i) + separator);

		sb.append("\n");

		//Body
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			for (int j = 0; j < tableModel.getColumnCount(); j++) {
				Cell cell  = (Cell) tableModel.getValueAt(i, j);
				sb.append(cell.getText());
				sb.append(separator);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Creates list of the cards and its quantity (basically deck) from the
	 * actual data in table.
	 * 
	 * @return simple card - quantity list based on values in table
	 */
	public String createCardList() {

		StringBuilder sb = new StringBuilder();
		/*
		 * int qCol = -1; for (int i = 0 ; i < tableModel.getColumnCount(); i++)
		 * if (tableModel.getColumnInfo(i).getType() == Column.Type.QUANTITY){
		 * qCol = i; break; }
		 * 
		 * if (qCol < 0) throw new
		 * IllegalStateException("There is no quantity columns. Aplication error."
		 * );
		 * 
		 * 
		 * for (int i = 0; i < tableModel.getRowCount(); i++) { Card card =
		 * tableModel.getCardAt(i); sb.append(card.getName() + " ");
		 * sb.append(tableModel.getValueAt(i, qCol).toString());
		 * sb.append("\n"); }
		 */
		return sb.toString();
	}

	/**
	 * Gets the padding string inserted between columns based on column type and
	 * alignment.
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	private String getPaddedValue(int row, int column, int width) {
		PricerColumn col = tableModel.getColumnType(column);
		Cell val = (Cell) tableModel.getValueAt(row, column);
		String res = val.getText();

		if (col == PricerColumn.QUANTITY || col == PricerColumn.RESULT)
			res = alignRight(res, width);
		else
			res = alignLeft(res, width);

		return res;
	}

	/**
	 * Pads the string based on the alignment in the text table.
	 * 
	 * @param s
	 *            string to be padded
	 * @param width
	 *            padding width/quantity
	 * @return formatted string with padding
	 */
	private String alignLeft(String s, int width) {
		return String.format("%-" + width + "s", s);
	}

	private String alignRight(String s, int width) {
		return String.format("%" + width + "s", s);
	}
}
