package bbc.juniperus.mtgp.tablemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import bbc.juniperus.mtgp.cardsearch.CardFinder;
import bbc.juniperus.mtgp.cardsearch.CardSearchResults;
import bbc.juniperus.mtgp.domain.Card;
import bbc.juniperus.mtgp.domain.CardResult;
import bbc.juniperus.mtgp.domain.PricingSettings;
import bbc.juniperus.mtgp.gui.Controller;
import bbc.juniperus.mtgp.gui.Controller.Phase;

/**
 * Implementation of {@link TableModel} which serves as a table model for GUI app table.
 * It is backed by {@link DataStorage}.
 *
 */
@SuppressWarnings("serial")
public class MtgPricerTableModel extends AbstractTableModel {

	public enum MtgPricerColumn {
		NAME("Name"), QUANTITY("Quantity"), RESULT("CardFinder");
	
		private final String headerText;
		
		MtgPricerColumn(String name){
			this.headerText = name;
		}
		
		public String getName(){
			return headerText;
		}
		
	}
	
	private PricingSettings pricingSettings;
	private Phase currentPhase;
	private Controller controller;
	private List<CardFinder> cardFinders;
	private Map<CardFinder,CardSearchResults> resultsContainer;
	
	/**
	 * Constructs table model around {@link DataStorage}.
	 * @param data central data storage.
	 */
	public MtgPricerTableModel(Controller controller){
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
		
		Card card  = getCardAt(row);
		
		if (column == MtgPricerColumn.NAME.ordinal())
			return new Cell(card.getName(), Cell.Type.STRING); 
		else if (column == MtgPricerColumn.QUANTITY.ordinal())
			return new Cell(pricingSettings.getQuantity(card) + "", Cell.Type.INTEGER);
		else
			if (currentPhase != Phase.SEARCHING && currentPhase != Phase.SEARCHING)
				throw new AssertionError();
			else{
				CardFinder cardFinder = cardFinders.get(column - 2);
				CardResult result = resultsContainer.get(cardFinder).getCardResult(card);
				
				String val;
				if (result == null) //This card is not processed yet so show just empty string.
					return Cell.NOT_PROCESSED_CELL;
				else if (result == CardResult.NULL_CARD_RESULT) //The card was not found.
					return Cell.NOT_FOUND_CELL;
				else{
					val = result.getPrice() + " " + result.getCurrency().getCurrencyCode();
					return new Cell(val,Cell.Type.PRICE);
				}
			}
	}


	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int index){

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
		MtgPricerColumn column = getColumnType(columnIndex);
		
		if (column == MtgPricerColumn.RESULT){
			CardFinder cardFinder = cardFinders.get(columnIndex - 2);
			return cardFinder.getName();
		}else
			return column.getName();
		
	}
	
	public MtgPricerColumn getColumnType(int columnIndex){
		if (columnIndex < 2)
			return MtgPricerColumn.values()[columnIndex];
		else
			return MtgPricerColumn.RESULT;
	}
	
	
	/**
	 * Refer to {@link TableModel}. The value taken into consideration
	 * is the result of {@link Object#toString()} invoked on <b>value</b> object.
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){

		
		Card card = getCardAt(rowIndex);
		
		if (columnIndex == MtgPricerColumn.NAME.ordinal()){
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
	
}
