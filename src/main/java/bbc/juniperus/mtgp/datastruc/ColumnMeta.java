package bbc.juniperus.mtgp.datastruc;

import bbc.juniperus.mtgp.domain.Source;

public class ColumnMeta {
	
	public static final int LEFT = 8;
	public static final int RIGHT = 9;
	
	public static enum Type{
		NAME, //Name of the card
		QUANTITY, //Quantity specified
		RESULT_NAME(true), //Name of the card result
		RESULT_PRICE(true), //Price of the single card result
		RESULT_TYPE(true),  //Type (Common,Uncommon,Rare) of the card result
		RESULT_EDITION(true), //Edition of result card
		RESULT_TOTAL_PRICE(true), //Quantity * single price of card result
		CHEAPEST_PRICE; //Cheapest price in the row
		
		//Define if the column is containing card results.
		private boolean sourceColumn;
		
		Type(boolean sourceColumn){this.sourceColumn = sourceColumn;}
		
		Type(){}
		
		boolean isSourceColumn(){
			return sourceColumn;
		}
	}

	private Type type;
	private Source source;
	private int width;
	private int alligment;
	
	public ColumnMeta(Type type){
		if (type.isSourceColumn())
			throw new IllegalArgumentException("This is source column type."
					+ "You need to specify source.");
		
		this.type = type;
		detectAndSetAlligment();
	}
	

	public ColumnMeta(Type type, Source source){
		
		if (!type.isSourceColumn())
			throw new IllegalArgumentException("This is not source column type.");
		
		this.type = type;
		this.source = source;
		detectAndSetAlligment();
	}

	
	
	private void detectAndSetAlligment(){
		if (type == Type.QUANTITY ||
				type == Type.RESULT_PRICE ||
				type == Type.RESULT_TOTAL_PRICE ||
				type == Type.CHEAPEST_PRICE)
			alligment = RIGHT;
		else
			alligment = LEFT;
			
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isResultColumn(){
		return (source != null);
	}

	public Source getSource() {
		return source;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}


	public int getAlligment() {
		return alligment;
	}


	public void setAlligment(int alligment) {
		this.alligment = alligment;
	}
	
	
	
	
	/**
	@Override
	public int hashCode() {
		return source.hashCode() + type.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof ColumnMeta))
			return false;
		ColumnMeta col2 = (ColumnMeta) obj;
		
		if (source == null)
			return (type.equals(col2.type));
		else{
			return (source.equals(col2.source) &&
						type.equals(col2.type));
		}

	}
	*/
	
}
