package bbc.juniperus.mtgp.utils;

import java.util.Arrays;

public class Stack {
	
	
	
	public static void printStack(){
		StackTraceElement[] els = Thread.currentThread().getStackTrace();
		
		for(int i = els.length -1 ; i > 1;i--)
			System.out.println(els[i]);
		
	}
	
	public static void main(String[] args){
		printStack();
	}
}
