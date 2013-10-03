package bbc.juniperus.mtgp.datastruc;

public class ReportCreator {
	
	private DataModel data;
	
	public ReportCreator(DataModel data){
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
	
	
}
