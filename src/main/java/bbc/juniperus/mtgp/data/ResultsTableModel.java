package bbc.juniperus.mtgp.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import bbc.juniperus.mtgp.data.viewmodel.Cell;
import bbc.juniperus.mtgp.data.viewmodel.Cell.Type;
import bbc.juniperus.mtgp.data.viewmodel.Column;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.Source;

public class ResultsTableModel extends AbstractTableModel implements DataChangeListener {

	private static final long serialVersionUID = 1L;
	private DataStorage data;
	private List<Card> cards = new ArrayList<Card>();
	private List<Column> columns = new ArrayList<Column>();
	private Set<Source> sources = new LinkedHashSet<Source>();
	
	private JTable table;

	public ResultsTableModel(DataStorage data){
		this.data = data;
		data.addDataChangeListener(this);
		//Default columns.
		columns.add(new Column(Column.Type.NAME));
		columns.add(new Column(Column.Type.QUANTITY));
		
		//Add price column for each source.
		for (Source s : data.getSources())
			addSource(s);
		
		//Add all cards to row-card mapping.
		for (Card c : data.cards())
			cards.add(c);
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
		DataStorage.Result resType;
		
		if (colType == Column.Type.RESULT_PRICE)
			resType = DataStorage.Result.PRICE;
		else if (colType == Column.Type.RESULT_EDITION)
			resType = DataStorage.Result.EDITION;
		else if (colType == Column.Type.RESULT_NAME)
			resType = DataStorage.Result.CARD_NAME;
		else if (colType == Column.Type.RESULT_TYPE)
			resType = DataStorage.Result.TYPE;
		else
			throw new RuntimeException("The type is wrong!");
		
		String result = data.getStrResult(card, col.getSource(), resType);
		
		if (result == "")
			return new Cell("",col, Type.NOT_LOADED);
		
		//If its price. Convert it to double decimal place value + currency.
		if (resType == DataStorage.Result.PRICE){
			try{
				Double d = new Double(result);
				result = String.format("%1$,.2f", d) + 
					 " " + data.getStrResult(card, col.getSource(), DataStorage.Result.CURRENCY);
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
		return data.isMutable();
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
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		System.out.println("setting value at " + value);
		Card card = cards.get(rowIndex);
		
		if (columnIndex == 0){
			Card newCard = new Card(value.toString());
			int index = cards.indexOf(card);
			cards.remove(card);
			cards.add(index, newCard);
			
			data.replaceCard(card, newCard);
		}
		else if (columnIndex == 1)
			data.setCardQuantity(card, (int) value);
		else
			throw new IllegalArgumentException("Cannot edit other column than 0 or 1");
	}

	
	/**
	 * Hacky method to provide notification to table that it should be repainted without
	 * losing the curent focus.	 
	 * This cannot be achieved via firing events for {@link TableModelListener}
	 * because {@link #fireTableDataChanged()} results in losing the focus of the table. <br><br>
	 * 
	 * TODO: refactor to something less hacky
	 *   
	 * @param table table to be notified
	 */
	public void setTable(JTable table){
		this.table = table;
	}
	
	//===== DataChangeListener interface ================================
	@Override
	public void resultAdded() {
		findMaxColumnsWidth();
		//Invoking repaint directly on the reference to table (hacky style).
		table.repaint();
	}
	
	@Override
	public void cardAdded(Card card) {
		cards.add(card);
		findMaxColumnsWidth();
		fireTableStructureChanged();
	}
	
	@Override
	public void cardsRemoved(Collection<Card> cardsList) {
		this.cards.removeAll(cardsList);
		findMaxColumnsWidth();
		fireTableDataChanged();
	}
	
	@Override
	public void sourcesAdded(Collection<Source> sources) {
		for (Source s : sources)
			addSource(s);
		findMaxColumnsWidth();
		fireTableStructureChanged();
	}


	@Override
	public void rowChanged(Card card) {
		findMaxColumnsWidth();
		fireTableDataChanged();
	}

	//=========================================================
	
	private void addSource(Source s){
		if (!sources.contains(s))
			columns.add(new Column(Column.Type.RESULT_PRICE,s));
		
	}
	
	public Column getColumnInfo(int column){
		return columns.get(column);
	}
	
	public Card getCardAt(int row){
		return cards.get(row);
	}



	private  void findMaxColumnsWidth(){
		
		int i = 0;
		for (Column col: columns){
			int maxW = 0;
			for (int row = 0; row < cards.size(); row++) {
				String text = getValueAt(row, i).toString();
				int curW = Math.max(col.getHeaderName().length() ,text.length());
				if (curW > maxW)
					maxW = curW;
			}
			col.setWidth(maxW);
			i++;
		}
		
	}
	

	
	

}
