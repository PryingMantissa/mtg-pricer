package bbc.juniperus.mtgp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import bbc.juniperus.mtgp.data.DataStorage;

public class Utils {
	
	
	public static void saveDataToBin(String path, DataStorage data) throws IOException{
		
		OutputStream fileOs = new FileOutputStream(path);
		OutputStream bufferOs = new BufferedOutputStream(fileOs);
		ObjectOutput objectOs = new ObjectOutputStream(bufferOs);
		objectOs.writeObject(data);
		if (objectOs != null)
				objectOs.close(); 
	}
	
	public static DataStorage loadDataFromBin(String path) throws IOException, ClassNotFoundException{
		DataStorage result = null;
		InputStream fileIs = new FileInputStream(path);
		InputStream bufferIs = new BufferedInputStream(fileIs);
		ObjectInput objectIs = new ObjectInputStream(bufferIs);
		result = (DataStorage) objectIs.readObject();
		
		if (objectIs != null)
			objectIs.close();
		return result;
	}
}
