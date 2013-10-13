package bbc.juniperus.mtgp.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import bbc.juniperus.mtgp.data.viewmodel.Cell;
import bbc.juniperus.mtgp.data.viewmodel.Column;
import bbc.juniperus.mtgp.data.viewmodel.Cell.Type;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.Source;

public class MtgTableModel extends AbstractTableModel implements DataChangeListener {

	private static final long serialVersionUID = 1L;
	private SearchData data;
	private Map<Integer,Card> cards = new HashMap<Integer,Card>();
	private List<Column> columns = new ArrayList<Column>();
	private Set<Source> sources = new LinkedHashSet<Source>();

	public MtgTableModel(SearchData data){
		this.data = data;
		data.addDataChangeListener(this);
		//Default columns.
		columns.add(new Column(Column.Type.NAME));
		columns.add(new Column(Column.Type.QUANTITY));
		
		//Add price column for each source.
		for (Source s : data.getSources())
			addSource(s);
		
		//Add all cards to row-card mapping.
		int i = 0;
		
		for (Card c : data.cards()){
			cards.put(i,c);
			i++;
		}
	}
	
	
	//========== AbstractTableModel implementation ======================
	@Override
	public Object getValueAt(int row,int column){
		
		Card card = cards.get(row);
		Column col = columns.get(column);
		
		if (column == 0)
			return new Cell(card.getName(),col, Cell.Type.TEXT);
		
		if (column == 1)
			return new Cell("" +data.getCardQuantity(card),col,Cell.Type.INTEGER);
		
		Column.Type colType = col.getType();
		SearchData.Result resType;
		
		if (colType == Column.Type.RESULT_PRICE)
			resType = SearchData.Result.PRICE;
		else if (colType == Column.Type.RESULT_EDITION)
			resType = SearchData.Result.EDITION;
		else if (colType == Column.Type.RESULT_NAME)
			resType = SearchData.Result.CARD_NAME;
		else if (colType == Column.Type.RESULT_TYPE)
			resType = SearchData.Result.TYPE;
		else
			throw new RuntimeException("The type is wrong!");
		
		String result = data.getStrResult(card, col.getSource(), resType);
		
		if (result == "")
			return new Cell("",col, Type.NOT_LOADED);
		
		//If its price. Convert it to double decimal place value + currency.
		if (resType == SearchData.Result.PRICE){
			try{
				Double d = new Double(result);
				result = String.format("%1$,.2f", d) + 
					 " " + data.getStrResult(card, col.getSource(), SearchData.Result.CURRENCY);
				return new Cell(result,col,Cell.Type.PRICE);
			}
			 catch(NumberFormatException e){
				 //Nothing to do here. Result will stay as it is.
			 }
		}
		
		return new Cell(result,col,Cell.Type.TEXT);
	}
	
	@Override
	public int getRowCount(){
		return data.getRowsCount();
	}
	
	@Override
	public int getColumnCount(){
		//System.out.println("Column ocunt je " +  columns.size());
		return columns.size();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false; //Cells are view-only.
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

	//===== DataChangeListener interface ================================
	@Override
	public void resultAdded() {
		this.fireTableDataChanged();
		
	}



	@Override
	public void cardAdded(Card card) {
		cards.put(cards.size(), card);
		this.fireTableStructureChanged();
		
	}

	//=========================================================
	
	private void addSource(Source s){
		if (!sources.contains(s))
			columns.add(new Column(Column.Type.RESULT_PRICE,s));
		
	//	System.out.println("columns su " + columns);
		
	}
	
	public Column getColumnInfo(int column){
		return columns.get(column);
	}


	@Override
	public void sourcesAdded(Collection<Source> sources) {
		for (Source s : sources)
			addSource(s);
		this.fireTableStructureChanged();
	}



	
	

}
