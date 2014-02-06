package bbc.juniperus.mtgp.domain;

import java.io.Serializable;

public class Source implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	String name;
	public Source(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return "source@" +name;
	}
	
	
	public String getName(){
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Source &&
				name.equals(((Source) obj).name))
			return true;
		return false;
	}
	
	public int hashCode(){
		return name.hashCode();
	}
	
	public static void main(String[] args){
		
		Source s,s2;
		
		s = new Source("pica");
		s2 = new Source("pica2");
		

	}
}