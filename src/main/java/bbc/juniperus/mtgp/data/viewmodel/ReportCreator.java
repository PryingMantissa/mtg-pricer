package bbc.juniperus.mtgp.data.viewmodel;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import bbc.juniperus.mtgp.data.MtgTableModel;

public class ReportCreator {
	
	private MtgTableModel data;
	
	public ReportCreator(MtgTableModel data){
		this.data = data;
	}
	
	public String generateReportString(){
		
		StringBuilder sb = new StringBuilder();
		
		int rowCount = data.getRowCount();
		int colCount = data.getColumnCount();
	
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++){
				sb.append(getPaddedValue(i, j));
				if (j - 1 !=  colCount)
					sb.append(" | ");
			}
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
	
	private  void findMaxColumnWidth(Column col){
		//TODO repair!!
		/*
		int maxW = 0;
		for (int row = 0; row < data.getRowCount(); row++) {
			//int curW = data.getValue(col.getSource(),col.getType(),row).length();
			if (curW > maxW)
				maxW = curW;
		}
		col.setWidth(maxW);
		*/
	}
	
	public void createCSVReport(String path) throws FileNotFoundException{
		
		String sep = ";";
		
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			
			for (int i = 0; i < data.getRowCount(); i++) {
				for (int j = 0; j < data.getColumnCount(); j++){
					writer.print(data.getValueAt(i,j));
					writer.print(sep);
				}
				writer.print("\n");
			}
			
			writer.close();
				
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
