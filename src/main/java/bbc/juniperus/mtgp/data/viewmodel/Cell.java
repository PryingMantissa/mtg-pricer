package bbc.juniperus.mtgp.data.viewmodel;


public class Cell implements Comparable{
	
	public static enum Type {TEXT,PRICE, INTEGER, NOT_LOADED,NA};
	
	private String text;
	private Column colMeta;
	private Type type;
	
	public Cell(String text, Column colMeta, Type type){
		
		this.text = text;
		this.colMeta = colMeta;
		if (text.equalsIgnoreCase("N/A"))
			this.type = Type.NA;
		else
			this.type = type;
	}
	
	public String getText(){
		return text;
	}
	
	public Type getType(){
		return type;
	}
	
	public Column getColumnMeta(){
		return colMeta;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString(){
		return text;
	}
	
}
