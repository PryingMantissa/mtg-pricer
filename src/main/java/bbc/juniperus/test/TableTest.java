package bbc.juniperus.test;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import bbc.juniperus.mtgp.cardsearch.SearcherFactory;
import bbc.juniperus.mtgp.utils.Stack;

public class TableTest {
	
	private JFrame window;
	private JTable table;

	
	public TableTest(){
		setTable();
		setupGui();
		window.setVisible(true);
	}
	
	

	private void setupGui(){
		
		window = new JFrame();
		//tabPane = new JTabbedPane();
		window.setTitle("Test");
		window.setSize(600, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.add(table);
		table.setBackground(Color.yellow);
	}
	
	private void setTable(){
		table = new JTable(new MyTableModel());
		//table.setColumnModel(new MyTableColumnModel());
	}
	
	
	public static void main (String[] args){
		new TableTest();
	}
	
	
	private class MyTableModel extends DefaultTableModel{
		
		@Override
		public Object getValueAt(int x, int y){
			return "kokot";
		}
		
		
		@Override
		public int getColumnCount(){
			return 5;
		}
		
		@Override
		public int getRowCount(){
			return 5;
		}
		
	}
	
	

	
	private class MyTableColumnModel extends DefaultTableColumnModel{
		
		@Override
		public int getColumnCount(){
			return 2;
		}
		
	}
	
	private class MyHeaderRenderer extends DefaultTableCellRenderer{
		@Override
		public Component getTableCellRendererComponent(JTable arg0, Object arg1,
				boolean arg2, boolean arg3, int arg4, int arg5) {
			return new JLabel(arg1.toString());
		}
	}
	
	private class MyCellRenderer extends DefaultTableCellRenderer{
		
		@Override
		public Component getTableCellRendererComponent(JTable arg0, Object arg1,
				boolean arg2, boolean arg3, int arg4, int arg5) {
			
			return new JLabel(arg1.toString());
		}
	}
}
