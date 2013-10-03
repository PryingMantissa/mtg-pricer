package bbc.juniperus.mtgp.datastruc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

public class DataModel extends AbstractTableModel implements Serializable{

	
	//TODO take controller (how data is displayed) out
	//to its own class.
	
	private static final long serialVersionUID = 1L;

	private Map<Integer,CardRow> rows = new HashMap<Integer,CardRow>();
	private List<Column> columns = new ArrayList<Column>();
	private List<Source> sources = new ArrayList<Source>();
	
	private List<TableModelListener> listeners = new ArrayList<TableModelListener>();
	
	public DataModel(){
		columns.add(new Column(Column.Type.NAME));
		columns.add(new Column(Column.Type.QUANTITY));
	}
	
	@Override
	public Object getValueAt(int row,int column){
		Column col = columns.get(column);
		String res = getValue(col,row);

		return new Cell(res,col);
	}
	
	@Override
	public int getRowCount(){
		return rows.size();
	}
	
	@Override
	public int getColumnCount(){
		return columns.size();
	}
	
	public Column getColumnInfo(int column){
		return columns.get(column);
	}
	

	//TODO move to utility class out of this
	public  static String formatDouble(double d){
		return String.format("%1$,.2f", d);
	}
	
	public void addCard(Card card, int quantity){
		if (quantity <1)
			throw new IllegalArgumentException("Quantity must be at least 1");
		rows.put(rows.size(), new CardRow(card, quantity));
	}
	

	public void addResults(Map<Card,CardResult> results, String sourceName){
		Source source = new Source(sourceName);
		for (Card c : results.keySet())
				addResult(c, results.get(c), source);
		fireTableStructureChanged();
	}
	
	private CardRow getRow(Card card){
		for (CardRow row : rows.values())
			if (row.getCard().equals(card))
				return row;
		
		return null;
	}
	
	private void addResult(Card card, CardResult result, Source source){
		getRow(card).addResult(source, result);
		
		if (!sources.contains(source)){
			sources.add(source);
			//columns.add(new ColumnMeta(ColumnMeta.Type.RESULT_NAME,source));
			columns.add(new Column(Column.Type.RESULT_PRICE,source));
		}
	}
	

	public int getColumnWidth(int col){
		return columns.get(col).getWidth();
		
	}
	
		
	private String getValue(Column column, int row){
		
		String res;
		Column.Type colType = column.getType();
		CardRow cardRow = rows.get(row);
		Source source = column.getSource();
		
		if (source == null){
			if (colType == Column.Type.NAME)
				res = cardRow.getCard().getName();
			else if (colType == Column.Type.QUANTITY )
				res = "" + cardRow.getQuantity();
			else
				throw new IllegalArgumentException("Type of column not supported.");
			
			return res;
		}
			
		CardResult result = rows.get(row).getResult(source);
		if (result == null)
			return null;
		
		if (colType == Column.Type.RESULT_NAME)
			res =  result.getName();
		else if (colType == Column.Type.RESULT_PRICE )
			res = createPriceString(result);
		else
			throw new IllegalArgumentException("Type of column not supported.");
		
		return res;
	}
	
	

	/**Util*/
	private  String createPriceString(CardResult result){
		String price = formatDouble(result.getPrice());
		price += " " + result.getCurrency().getCurrencyCode();
		
		return price;
		
	}

	public void createCSVReport(String path) throws FileNotFoundException{
		
		String sep = ";";
		
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			
			for (int i = 0; i < getRowCount(); i++) {
				for (int j = 0; j < getColumnCount(); j++){
					writer.print(getValueAt(i,j));
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
	
	
	/* TODO repai!!???
	public String stringify(){
		
		StringBuilder sb = new StringBuilder();
		String sep = ", ";
		String sep2 = " | ";
		for (int i=0; i < cards.size() ;i++){
			Card c = cards.get(i);
			
			sb.append(c).append(sep);
			sb.append(c.getName()).append(sep);
			sb.append(quantities.get(c)).append(sep2);
		
			Map<Source,CardResult> resultSet = results.get(c);
			
			if (resultSet != null)
				for (Source s : resultSet.keySet())
					sb.append(resultSet.get(s)).append(sep2);
			else
				sb.append("- no result -");
			
			sb.append("\n");
			
		}
		return sb.toString();
	}
	*/

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		//Just as broad as possible. Could be changed to String maybe.
		return Object.class;
	}


	@Override
	public String getColumnName(int columnIndex) {
		
		Column col = columns.get(columnIndex);
		
		if (columnIndex == 0)
			return col.getHeaderName();
			
		Source source = col.getSource();
		Source sourceBefore = columns.get(columnIndex-1).getSource();
		
		if (source == null || !source.equals(sourceBefore))
			return col.getHeaderName();
		
		return "";
	}


	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//Cells are view only.
		return false;
	}
}
