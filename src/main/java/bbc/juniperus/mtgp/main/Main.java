package bbc.juniperus.mtgp.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;

import bbc.juniperus.mtgp.deckpricing.DeckEvalException;
import bbc.juniperus.mtgp.domain.Deck;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	
	public Deck loadDeck(String path) throws DeckEvalException{
		
		File f = new File(path);
		
		if (!f.exists())
			throw new DeckEvalException("The file does not exits.");

		Deck deck = new Deck();
		
		try {
			deck.readFromFile(f);
		} catch (IOException e) {
			throw new DeckEvalException("Error when reading from the file.\n" + e.getMessage());
		} catch (ParseException e) {
			throw new DeckEvalException("Error when parsing the file.\n" + e.getMessage());
		}
		
		return deck;
	}
	
	
public static void serializeDeck(Deck deck,String path){
		
		try{
			OutputStream fileOs = new FileOutputStream(path);
			OutputStream bufferOs = new BufferedOutputStream(fileOs);
			ObjectOutput objectOs = new ObjectOutputStream(bufferOs);
		      
		      try{
		    	  objectOs.writeObject(deck);
		      }finally{
		    	  objectOs.close();
		      }

		}catch(IOException e){
		      System.out.println("Saving deck failed: " + e.getMessage());
		}

	}
	
	public static Deck deserializeDeck(String path){
		
		Deck result = null;
		try{
		      InputStream fileIs = new FileInputStream(path);
		      InputStream bufferIs = new BufferedInputStream(fileIs);
		      ObjectInput objectIs = new ObjectInputStream(bufferIs);
		      try{
		    	  result = (Deck) objectIs.readObject();
		      }
		      finally{
		    	  objectIs.close();
		      }
	    }catch(ClassNotFoundException e){
	    	System.out.println("Loading deck failed: " + e.getMessage());
	    }catch(IOException e){
	    	System.out.println("Loading deck failed: " + e.getMessage());
	    }catch(ClassCastException e){
	    	System.out.println("Loading deck failed: " + e.getMessage());
	    }
		
		return result;
	}
	
	
	
}
