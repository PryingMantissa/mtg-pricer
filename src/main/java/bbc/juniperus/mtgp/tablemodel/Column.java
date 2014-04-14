package bbc.juniperus.mtgp.tablemodel;

import javax.swing.SwingConstants;
import javax.xml.transform.Source;

/**
 * Class containing the meta information regarding the type of the column in a table.
 * Also contains rendering information and information used when creating text tables.
 *
 */
public class Column {
	
	public static final int LEFT = SwingConstants.LEFT;
	public static final int RIGHT = SwingConstants.RIGHT;
	
	private static final String[] HEADER_NAMES = new String[]{"Card name","Quantity","Card","Price","Type","Edition",
		"Price total","Lowest price"};
	
	/**
	 * Indicates type of the column. There can be several types. But not all of them
	 * are currently used.
	 */
	public static enum Type{
	
		NAME(HEADER_NAMES[0]), //Name of the card
		QUANTITY(HEADER_NAMES[1]), //Quantity specified
		RESULT_NAME(true, HEADER_NAMES[2]), //Name of the card result
		RESULT_PRICE(true, HEADER_NAMES[3]), //Price of the single card result
		RESULT_TYPE(true, HEADER_NAMES[4]),  //Type (Common,Uncommon,Rare) of the card result
		RESULT_EDITION(true, HEADER_NAMES[5]), //Edition of result card
		RESULT_TOTAL_PRICE(true, HEADER_NAMES[6]), //Quantity * single price of card result
		CHEAPEST_PRICE(HEADER_NAMES[7]); //Cheapest price in the row
		
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
	
	public Column(Type type){
		if (type.sourceColumn)
			throw new IllegalArgumentException("This is source column type."
					+ "You need to specify source.");
		
		this.type = type;
		detectAndSetAlligment();
		headerName = type.header; //Might be changed later.
	}
	

	public Column(Type type, Source source){
		
		if (!type.sourceColumn)
			throw new IllegalArgumentException("This is not source column type.");
		
		this.type = type;
		this.source = source;
		detectAndSetAlligment();
		//headerName = source.getName() + "|" + type.header;
		//headerName = "<html>" + source.getName() + "<br>" + type.header + "</html>";
		headerName = source.getName();
	}
	
	/**
	 * Automatically sets the alignment for the column based on its type.
	 */
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
	
	/**
	 * Determines whether column contains pricing results.
	 * @return <code>true</code> if contains pricing results, <code>false</code> if not
	 */
	public boolean isResultColumn(){
		
		return (source != null);
	}

	/**
	 * Returns {@link Source} for the column if it is a result column.
	 * Otherwise returns <code>null</code>.
	 * @return the column pricing source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Sets the width of the column.
	 * @param width column width to be set
	 */
	public void setWidth(int width) {
		
		this.width = width;
	}
	
	/**
	 * Returns the set width of the column.
	 * @return column width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns alignment of this columns. Integer values returned are the same
	 * as used in {@link SwingConstants}.
	 * @return the alignment of the column
	 */
	public int getAlignment() {
		return alligment;
	}

	/**
	 * Sets the alignment for the column. Integer alignment values are the same
	 * as used in {@link SwingConstants}.
	 * @param alignment alignment of the column
	 */
	public void setAlligment(int alignment) {
		this.alligment = alignment;
	}

	/**
	 * Returns the text of the header
	 * @return string with header text
	 */
	public String getHeaderName() {
		return headerName;
	}

	/**
	 * Sets the header text.
	 * @param headerName header text to be set
	 */
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	
}
