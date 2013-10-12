package bbc.juniperus.mtgp.data.viewmodel;


public class Cell {
	
	String text;
	Column colMeta;
	
	public Cell(String text, Column colMeta){
		this.text = text;
		this.colMeta = colMeta;
	}
	
	public String getText(){
		return text;
	}
	
	public Column getColumnMeta(){
		return colMeta;
	}
	
}
