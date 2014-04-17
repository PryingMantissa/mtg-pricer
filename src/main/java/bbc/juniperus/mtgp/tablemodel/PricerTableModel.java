package bbc.juniperus.mtgp.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import bbc.juniperus.mtgp.data.DataStorage;
import bbc.juniperus.mtgp.data.PricingSettings;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.gui.Controller;
import bbc.juniperus.mtgp.gui.Controller.Phase;

/**
 * Implementation of {@link TableModel} which serves as a table model for GUI app table.
 * It is backed by {@link DataStorage}.
 *
 */
@SuppressWarnings("serial")
public class PricerTableModel extends AbstractTableModel {

	private List<Column> columns = new ArrayList<Column>();
	private PricingSettings pricingSettings;
	private Phase phase;
	
	private enum Column {NAME, QUANTITY}
	private Controller controller;
	
	
	/**
	 * Constructs table model around {@link DataStorage}.
	 * @param data central data storage.
	 */
	public PricerTableModel(Controller controller){
		//data.addDataChangeListener(this);
		//Default columns.
		this.controller = controller;
		phase = Phase.SETTING;
	}
	
	public void setPricingSettings(PricingSettings settings){
		pricingSettings = settings;
	}
	
	
	//========== AbstractTableModel implementation ======================
	
	/**
	 * Refer to {@link TableModel}. This implementation returns
	 * {@link Cell} object.
	 */
	@Override
	public Object getValueAt(int row,int column){
	//	System.out.println("Getting value at " + row + " " + column); 
//		
//		
//		Card card = cards.get(row);
//		Column col = columns.get(column);
//		
//		if (column == 0)
//			return new Cell(card.getName(),col, Cell.Type.TEXT);
//		
//		if (column == 1)
//			return new Cell("" +data.getCardQuantity(card),col,Cell.Type.INTEGER);
//		
//		Column.Type colType = col.getType();
//		DataStorage.Result resType;
//
//		if (colType == Column.Type.RESULT_PRICE)
//			resType = DataStorage.Result.PRICE;
//		else if (colType == Column.Type.RESULT_EDITION)
//			resType = DataStorage.Result.EDITION;
//		else if (colType == Column.Type.RESULT_NAME)
//			resType = DataStorage.Result.CARD_NAME;
//		else if (colType == Column.Type.RESULT_TYPE)
//			resType = DataStorage.Result.TYPE;
//		else
//			throw new RuntimeException("The type is wrong!");
//		
//		String result = data.getStrResult(card, col.getSource(), resType);
//		
//		if (result == "")
//			return new Cell("",col, Cell.Type.NOT_LOADED);
//		
//		//If its price. Convert it to double decimal place value + currency.
//		if (resType == DataStorage.Result.PRICE){
//			try{
//				Double d = new Double(result);
//				result = String.format("%1$,.2f", d) + 
//					 " " + data.getStrResult(card, col.getSource(), DataStorage.Result.CURRENCY);
//				return new Cell(result,col,Cell.Type.PRICE);
//			}
//			 catch(NumberFormatException e){
//				 //Nothing to do here. Result will stay as it is.
//			 }
//		}
//		
		//return new Cell(result,col,Cell.Type.TEXT);
		
		
		Card card  = getCardAt(row);
		
		if (column == Column.NAME.ordinal())
			return new Cell(card.getName(), Cell.Type.STRING); 
		else if (column == Column.QUANTITY.ordinal())
			return new Cell(pricingSettings.getQuantity(card) + "", Cell.Type.INTEGER);
		else
			throw new AssertionError();

	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int index){
		/*
		if (index == Column.NAME.ordinal())
			return String.class;
		else if (index == Column.QUANTITY.ordinal())
			return Integer.class;
		else
			throw new AssertionError();
			*/
		
		return Cell.class;
					
	}
	

	@Override
	public int getRowCount(){
	//	System.out.println("getting row count");
		return pricingSettings.getCards().size();
	}
	

	@Override
	public int getColumnCount(){
		if (phase == Phase.SETTING)
			return 2;
		else
			throw new UnsupportedOperationException();
	}
	

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (phase == Phase.SETTING) //Can edit either card name or quantity when in settings phase
			return true;
		else
			throw new UnsupportedOperationException();
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return "col " + columnIndex;
	}
	
	
	/**
	 * Refer to {@link TableModel}. The value taken into consideration
	 * is the result of {@link Object#toString()} invoked on <b>value</b> object.
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){

		
		Card card = getCardAt(rowIndex);
		
		if (columnIndex == Column.NAME.ordinal()){
			String str = (String) value;
			Card newCard = new Card(str.trim());
			
			if (pricingSettings.getCards().contains(newCard)){
				controller.displayErroMessage("<html>The name could not be changed."
						+ " Card <b><i>"  +  newCard.getName()  + "</i></b> is already in the deck </html>");
				return;
			}
			
			pricingSettings.replaceCard(card, newCard);
		}
		else if (columnIndex == 1)
			pricingSettings.setNewQuantity(card, (int) value);
		else
			throw new AssertionError();
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
//	public void setTable(JTable table){
//		this.table = table;
//	}
	
	//===== DataChangeListener interface ================================
	
	/**
	 * Refer to {@link DataChangeListener}.
	 */
//	@Override
//	public void resultAdded() {
//		findMaxColumnsWidth();
//		//Invoking repaint directly on the reference to table (hacky style).
//		table.repaint();
//	}
//	
//	/**
//	 * Refer to {@link DataChangeListener}.
//	 */
//	@Override
//	public void cardAdded(Card card) {
//		cards.add(card);
//		findMaxColumnsWidth();
//		fireTableStructureChanged();
//	}
//	
//	/**
//	 * Refer to {@link DataChangeListener}.
//	 */
//	@Override
//	public void cardsRemoved(Collection<Card> cardsList) {
//		this.cards.removeAll(cardsList);
//		findMaxColumnsWidth();
//		fireTableDataChanged();
//	}
//	
//	/**
//	 * Refer to {@link DataChangeListener}.
//	 */
//	@Override
//	public void sourcesAdded(Collection<Source> sources) {
//		for (Source s : sources)
//			addSource(s);
//		findMaxColumnsWidth();
//		fireTableStructureChanged();
//	}
//
//	/**
//	 * Refer to {@link DataChangeListener}.
//	 */
//	@Override
//	public void rowChanged(Card card) {
//		findMaxColumnsWidth();
//		fireTableDataChanged();
//	}
//
//	
//	
//	/**
//	 * Adds a new source to this table model. If its duplicate it is ignored.
//	 * @param s new source
//	 */
//	private void addSource(Source s){
//		if (!sources.contains(s))
//			columns.add(new Column(Column.Type.RESULT_PRICE,s));
//		
//	}
//	
	
	/**
	 * Returns column info object for a given index.
	 * @param column index of the column
	 * @return column meta info object
	 */
	public Column getColumnInfo(int column){
		return columns.get(column);
	}
	
	/**
	 * Returns a card for a given row.
	 * @param row row 
	 * @return card 
	 */
	public Card getCardAt(int row){
		return pricingSettings.getCards().get(row);
	}

	/**
	 * Determines all columns width based on the width of the widest cell in each column.
	 * This way the width of the column is automatically adjusted when
	 * a cell with a wider test is inserted.
	 */
	
	/*
	private void findMaxColumnsWidth(){
		
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
	*/
}
