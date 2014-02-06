package bbc.juniperus.mtgp.data.viewmodel;

import bbc.juniperus.mtgp.data.ResultsTableModel;
import bbc.juniperus.mtgp.domain.Card;

public class ReportCreator {
	
	private ResultsTableModel data;
	
	public ReportCreator(ResultsTableModel tableModel){
		this.data = tableModel;
	}
	
	public String generateFormattedReport(){
		
		StringBuilder sb = new StringBuilder();
		
		int rowCount = data.getRowCount();
		int colCount = data.getColumnCount();

		for (int i = 0 ; i < colCount;i++){
			String text = allignLeft(data.getColumnName(i), data.getColumnInfo(i).getWidth());
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
	
	public String createCardList(){
		
		StringBuilder sb = new StringBuilder();

		int qCol = -1;
		for (int i = 0 ; i < data.getColumnCount(); i++)
			if (data.getColumnInfo(i).getType() == Column.Type.QUANTITY){
				qCol = i;
				break;
			}
		
		if (qCol < 0)
			throw new RuntimeException("There is no quantity columns. Aplication error.");
		
		
		for (int i = 0; i < data.getRowCount(); i++) {
			Card card = data.getCardAt(i);
			sb.append(card.getName() + " ");
			sb.append(data.getValueAt(i, qCol).toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	
	
	private String getPaddedValue(int row,int column){
		Column col = data.getColumnInfo(column);
		String res = data.getValueAt(row,column).toString();
		
		if (col.getAlligment() == Column.RIGHT)
			res = allignRight(res, col.getWidth());
		else
			res = allignLeft(res, col.getWidth());
		
		return res;
	}
	
	private String allignLeft(String s, int width){
		return String.format("%-" + width + "s", s);
	}
	
	private String allignRight(String s, int width){
		return String.format("%" + width + "s", s);
	}
}
