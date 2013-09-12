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

import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.Source;

public class DataTable implements Serializable{

	private static final long serialVersionUID = 1L;

	//Quantity of card.
	private Map<Card,Integer> quantities = new HashMap<Card,Integer>();
	//Map of results.
	private Map<Card,Map<Source,CardResult>> results = new HashMap<Card,Map<Source,CardResult>>();
	//Position of card in row.
	private Map<Integer,Card> cards = new HashMap<Integer,Card>();
	
	private List<ColumnMeta> columns = new ArrayList<ColumnMeta>();
	private List<Source> sources = new ArrayList<Source>();
	private List<Source> shownSources = new ArrayList<Source>();
	
	public DataTable(){

	}
	

	public String getValue(int row,int column){
		
		ColumnMeta col = columns.get(column);
		String res = getRow(col.getSource(),col.getType(),row);

		return res;
	}
	
	
	public String getPaddedValue(int row,int column){
		ColumnMeta col = columns.get(column);
		String res = getRow(col.getSource(),col.getType(),row);
		
		if (col.getAlligment() == ColumnMeta.RIGHT)
			res = allignRight(res, col.getWidth());
		else
			res = allignLeft(res, col.getWidth());
		
		return res;
	}
	
	public int getRowCount(){
		return cards.size();
	}
	
	public int getColumnCount(){
		return columns.size();
	}
	
	private String getRow(ColumnMeta.Type colType, int row){
		
		String res;
		if (colType == ColumnMeta.Type.NAME)
			res = cards.get(row).getName();
		else if (colType == ColumnMeta.Type.QUANTITY )
			res = quantities.get(cards.get(row)).toString();
		else
			throw new IllegalArgumentException("Type of column not supported for this method");
		
		return res;
	}
	
	private String getRow(Source s, ColumnMeta.Type type, int row){
		
		if (s == null)
			return getRow(type,row);
		
		String na = "-not found-";
		Card c = cards.get(row);
		Map<Source,CardResult>  resultSet = results.get(c);
		
		if (resultSet == null)
			throw new IllegalStateException("The card " + c +
					" has not created result-set yet.");
		
		CardResult result = resultSet.get(s);

		if (result == null)
			return na;
		
		String stringRes;
		if (type == ColumnMeta.Type.RESULT_NAME)
			stringRes =  result.getName();
		else if (type == ColumnMeta.Type.RESULT_PRICE )
			stringRes = createPriceString(result);
		else
			throw new IllegalArgumentException("Type of column not supported for this method");
		
		return stringRes;
	}
	
	

	/**Util*/
	private  String createPriceString(CardResult result){
		String price = formatDouble(result.getPrice());
		price += " " + result.getCurrency().getCurrencyCode();
		
		return price;
		
	}
	
	/**Util*/
	private  String formatDouble(double d){
		return String.format("%1$,.2f", d);
	}
	
	public void addCard(Card card, int quantity){
		if (quantity <1)
			throw new IllegalArgumentException("Quantity must be at least 1");
		
		quantities.put(card, quantity);
		cards.put(cards.size(),card);
	}
	
	public Collection<Card> getCards(){
		return cards.values();
	}
	
	public int getQuantity(Card card){
		return quantities.get(card);
	}
	
	
	public void addResult(Card card, CardResult result, String sourceName){
		
		Source source = new Source(sourceName);
		Map<Source,CardResult> resultSet = results.get(card);
		
		if (resultSet == null){
			resultSet = new HashMap<Source,CardResult>();
			results.put(card, resultSet);
		}
		resultSet.put(source, result);
		
		if (!sources.contains(source)){
			sources.add(source);
			shownSources.add(source);
		}
	}
	
	public void setUp(){
		createColumnsMetaData();
		findMaxColumnsWidth();
	}
	
	private void createColumnsMetaData(){
		
		columns.add(new ColumnMeta(ColumnMeta.Type.NAME));
		columns.add(new ColumnMeta(ColumnMeta.Type.QUANTITY));
		
		for (Source s : sources){
			columns.add(new ColumnMeta(ColumnMeta.Type.RESULT_NAME,s));
			columns.add(new ColumnMeta(ColumnMeta.Type.RESULT_PRICE,s));
		}
		
		//columns.add(new Column(Column.Type.CHEAPEST_PRICE));
	}
	
	
	public int getColumnWidth(int col){
		return columns.get(col).getWidth();
		
	}
	
	private  void findMaxColumnWidth(ColumnMeta col){
		int maxW = 0;
		for (int i = 0; i < getRowCount(); i++) {
			int curW = getRow(col.getSource(),col.getType(),i).length();
			if (curW > maxW)
				maxW = curW;
		}
		col.setWidth(maxW);
	}
	
	
	
	private void findMaxColumnsWidth(){
		for (ColumnMeta cm : columns)
			findMaxColumnWidth(cm);
	}
	
	
	public void createCSVReport(String path) throws FileNotFoundException{
		
		String sep = ";";
		
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			
			for (int i = 0; i < getRowCount(); i++) {
				for (int j = 0; j < getColumnCount(); j++){
					writer.print(getValue(i,j));
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
	
	public String generateReportString(){
		
		StringBuilder sb = new StringBuilder();
	
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++){
				sb.append(getPaddedValue(i, j));
				if (j-1 !=  getColumnCount())
					sb.append(" | ");
			}
			sb.append("\n");
		}
			
			
		return sb.toString();
	}
	
	public static String allignLeft(String s, int width){
		return String.format("%-" + width + "s", s);
	}
	
	public static String allignRight(String s, int width){
		return String.format("%" + width + "s", s);
	}
	
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
	
}
