package bbc.juniperus.mtgp.datastruc;

import bbc.juniperus.mtgp.domain.Source;

public class ColumnMeta {
	
	public static final int LEFT = 8;
	public static final int RIGHT = 9;
	
	private static final String[] headerNames = new String[]{"<html>Card name</html>","Quantity","Card","Price","Type","Edition",
		"Price total","Lowest price"};
	
	public static enum Type{
	
		NAME(headerNames[0]), //Name of the card
		QUANTITY(headerNames[1]), //Quantity specified
		RESULT_NAME(true, headerNames[2]), //Name of the card result
		RESULT_PRICE(true, headerNames[3]), //Price of the single card result
		RESULT_TYPE(true, headerNames[4]),  //Type (Common,Uncommon,Rare) of the card result
		RESULT_EDITION(true, headerNames[5]), //Edition of result card
		RESULT_TOTAL_PRICE(true, headerNames[6]), //Quantity * single price of card result
		CHEAPEST_PRICE(headerNames[7]); //Cheapest price in the row
		
		//Define if the column is containing card results.
		private boolean sourceColumn;
		private String header;

		Type(boolean sourceColumn, String header){
			this.sourceColumn = sourceColumn;
			this.header = header;
		}
		
		Type(String header){
			this.header = header;
		}		
	}

	private Type type;
	private Source source;
	private int width;
	private int alligment;
	private String headerName;
	
	public ColumnMeta(Type type){
		if (type.sourceColumn)
			throw new IllegalArgumentException("This is source column type."
					+ "You need to specify source.");
		
		this.type = type;
		detectAndSetAlligment();
		headerName = type.header; //Might be changed later.
	}
	

	public ColumnMeta(Type type, Source source){
		
		if (!type.sourceColumn)
			throw new IllegalArgumentException("This is not source column type.");
		
		this.type = type;
		this.source = source;
		detectAndSetAlligment();
		headerName = source.getName() + "|" + type.header;
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


	public String getHeaderName() {
		return headerName;
	}


	public void setHeaderName(String headerName) {
		this.headerName = headerName;
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
