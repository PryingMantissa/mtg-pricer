package bbc.juniperus.mtgp.datastruc;

public class Cell {
	
	String text;
	ColumnMeta colMeta;
	
	public Cell(String text, ColumnMeta colMeta){
		this.text = text;
		this.colMeta = colMeta;
	}
	
	public String getText(){
		return text;
	}
	
	public ColumnMeta getColumnMeta(){
		return colMeta;
	}
	
}
