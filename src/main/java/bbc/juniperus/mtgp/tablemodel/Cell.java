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
	public static enum Type {STRING, PRICE, INTEGER, NOT_LOADED,NA};
	
	private String text;
	private Type type;
	
	public Cell(String text, Type type){
		this.text = text;
		this.type = type;
		/*
		if (text.equalsIgnoreCase("N/A"))
			this.type = Type.NA;
		else
			this.type = type;
			*/
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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString(){
		return getClass().getSimpleName() + " [ text:"  + text  + ", type: " + type + "]";
	}
	
}
