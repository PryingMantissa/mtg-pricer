package bbc.juniperus.mtgp.tablemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardSearchResults;
import bbc.juniperus.mtgp.data.DataStorage;
import bbc.juniperus.mtgp.data.PricingSettings;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.gui.Controller;
import bbc.juniperus.mtgp.gui.Controller.Phase;

/**
 * Implementation of {@link TableModel} which serves as a table model for GUI app table.
 * It is backed by {@link DataStorage}.
 *
 */
@SuppressWarnings("serial")
public class PricerTableModel extends AbstractTableModel {

	private enum Column {
		NAME("Name"), QUANTITY("Quantity");
	
		private final String name;
		
		Column(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
	}
	
	private List<Column> columns = new ArrayList<Column>();
	private PricingSettings pricingSettings;
	private Phase currentPhase;
	private Controller controller;
	private List<CardFinder> cardFinders;
	private Map<CardFinder,CardSearchResults> resultsContainer;
	
	/**
	 * Constructs table model around {@link DataStorage}.
	 * @param data central data storage.
	 */
	public PricerTableModel(Controller controller){
		//data.addDataChangeListener(this);
		//Default columns.
		this.controller = controller;
	}
	
	public void newPricing(PricingSettings settings){
		pricingSettings = settings;
		currentPhase = Phase.SETTING;
	}
	
	public void startPresentingResults(Collection<CardSearchResults> searchResults){
		resultsContainer = new HashMap<>();
		
		for (CardSearchResults res : searchResults) //Store it in inernal hash map for faster acces
			resultsContainer.put(res.getFinder(),res);
		currentPhase = Phase.SEARCHING;
		cardFinders = new ArrayList<>(resultsContainer.keySet());
		fireTableStructureChanged();
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
			if (currentPhase != Phase.SEARCHING && currentPhase != Phase.SEARCHING)
				throw new AssertionError();
			else{
				CardFinder cardFinder = cardFinders.get(column - 2);
				CardResult result = resultsContainer.get(cardFinder).getCardResult(card);
				
				String val;
				if (result == null) //not yet found
					val = "x";
				else if (result == CardResult.NULL_CARD_RESULT)
					val = "N/A";
				else
					val = result.getPrice() + " KKT";
				return new Cell(val,Cell.Type.STRING);
			}
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
		if (currentPhase == null) //When gui objects are constructed but the first phase has not started 
			return 0;
		return pricingSettings.getCards().size();
	}
	

	@Override
	public int getColumnCount(){
		if (currentPhase == null) //When gui objects are constructed but the first phase has not started 
			return 0;
		
		if (currentPhase == Phase.SETTING)
			return 2;
		else if (currentPhase == Phase.SEARCHING ||
				currentPhase == Phase.PRICING_FINISHED){
			System.out.println("returning " + (2 + cardFinders.size()));
			return 2 + cardFinders.size();
		}
		else;
			throw new AssertionError();
	}
	

	
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		return (currentPhase == Phase.SETTING); //Can edit either card name or quantity when in settings phase
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < Column.values().length)
			return Column.values()[columnIndex].getName();
		else{
			CardFinder cardFinder = cardFinders.get(columnIndex - 2);
			return cardFinder.getName();
		}
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
	 * Returns a card for a given row.
	 * @param row row 
	 * @return card 
	 */
	public Card getCardAt(int row){
		return pricingSettings.getCards().get(row);
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
