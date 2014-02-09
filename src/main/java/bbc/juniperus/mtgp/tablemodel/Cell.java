package bbc.juniperus.mtgp.tablemodel;


/**
 * Class representing the cell in the table containing necessary information in order
 * to be rendered/ordered properly.
 *
 */
public class Cell{
	
	/**
	 * Type of cell. Useful for comparing and ordering of the cells.
	 */
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
	
	/**
	 * Returns text inside the cell.
	 * @return cell text
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * Returns cell type.
	 * @return cell type.
	 */
	public Type getType(){
		return type;
	}
	
	/**
	 * Returns reference to the cell's column meta informa object {@link Column}.
	 * @return related column object
	 */
	public Column getColumnMeta(){
		return colMeta;
	}

	/**
	 * Overridden {@link Object#toString()} method.
	 */
	@Override
	public String toString(){
		return text;
	}
	
}
