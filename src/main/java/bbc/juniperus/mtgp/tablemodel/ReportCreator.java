package bbc.juniperus.mtgp.tablemodel;

import bbc.juniperus.mtgp.domain.Card;

/**
 * Generator of string reports in various formats. Used for export functionality 
 * or also for CLI usage.
 */
public class ReportCreator {
	
	private MtgPricerTableModel data;
	
	public ReportCreator(MtgPricerTableModel tableModel){
		this.data = tableModel;
	}
	
	/**
	 * Creates card pricing table in normal txt format.
	 * @return txt format card - price table.
	 */
	public String generateFormattedReport(){
		
		StringBuilder sb = new StringBuilder();
		
		int rowCount = data.getRowCount();
		int colCount = data.getColumnCount();

		for (int i = 0 ; i < colCount;i++){
			String text = alignLeft(data.getColumnName(i), data.getColumnInfo(i).getWidth());
			sb.append(text);
			if (i - 1 <  colCount)
				sb.append(" | ");
		}
		sb.append("\n");

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++){
				sb.append(getPaddedValue(i, j));
				if (j - 1 <  colCount)
					sb.append(" | ");
			}
			sb.append("\n");
		}
			
		return sb.toString();
	}
	
	/**
	 * Creates card pricing report table in CSV format
	 * @param separator CSV separator character
	 * @return string representing the CSV report/table of the priced cards
	 */
	public String createCSVReport(String separator){
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0 ; i < data.getColumnCount();i++)
			sb.append(data.getColumnName(i) +";");
			
		sb.append("\n");
		
		for (int i = 0; i < data.getRowCount(); i++) {
			for (int j = 0; j < data.getColumnCount(); j++){
				sb.append(data.getValueAt(i,j).toString());
				sb.append(separator);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Creates list of the cards and its quantity (basically deck) from the
	 * actual data in table.
	 * @return simple card - quantity list based on values in table
	 */
	public String createCardList(){
		
		StringBuilder sb = new StringBuilder();

		int qCol = -1;
		for (int i = 0 ; i < data.getColumnCount(); i++)
			if (data.getColumnInfo(i).getType() == Column.Type.QUANTITY){
				qCol = i;
				break;
			}
		
		if (qCol < 0)
			throw new IllegalStateException("There is no quantity columns. Aplication error.");
		
		
		for (int i = 0; i < data.getRowCount(); i++) {
			Card card = data.getCardAt(i);
			sb.append(card.getName() + " ");
			sb.append(data.getValueAt(i, qCol).toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	/**
	 * Gets the padding string inserted between columns based on
	 * column type and alignment.
	 * @param row
	 * @param column
	 * @return
	 */
	private String getPaddedValue(int row,int column){
		Column col = data.getColumnInfo(column);
		String res = data.getValueAt(row,column).toString();
		
		if (col.getAlignment() == Column.RIGHT)
			res = alignRight(res, col.getWidth());
		else
			res = alignLeft(res, col.getWidth());
		
		return res;
	}
	
	/**
	 * Pads the string based on the alignment in the text table.
	 * @param s string to be padded
	 * @param width padding width/quantity
	 * @return formatted string with padding
	 */
	private String alignLeft(String s, int width){
		return String.format("%-" + width + "s", s);
	}
	
	private String alignRight(String s, int width){
		return String.format("%" + width + "s", s);
	}
}
