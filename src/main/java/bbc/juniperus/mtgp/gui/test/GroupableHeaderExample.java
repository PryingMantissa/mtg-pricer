package bbc.juniperus.mtgp.gui.test;

/* (swing1.1beta3)
*
* |-----------------------------------------------------|
* |        |       Name      |         Language         |
* |        |-----------------|--------------------------|
* |  SNo.  |        |        |        |      Others     |
* |        |   1    |    2   | Native |-----------------|
* |        |        |        |        |   2    |   3    |  
* |-----------------------------------------------------|
* |        |        |        |        |        |        |
*
*/
//package jp.gr.java_conf.tame.swing.examples;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
* @version 1.0 11/09/98
*/
public class GroupableHeaderExample extends JFrame {


	private static final long serialVersionUID = 1L;

	public GroupableHeaderExample() {
		super( "Groupable Header Example" );

		DefaultTableModel dm = new DefaultTableModel();
		dm.setDataVector(new Object[][]{
							{"119","foo","bar","ja","ko","zh"},
							{"911","bar","foo","en","fr","pt"}},
	    new Object[]{"SNo.","kuku","2","Native","2","3"});

		
		JTable table = new JTable(dm) {
			
			private static final long serialVersionUID = 1L;

			protected JTableHeader createDefaultTableHeader() {
				return new GroupableTableHeader(columnModel);
			}
		};
			
			
	   TableColumnModel cm = table.getColumnModel();
	   ColumnGroup gName = new ColumnGroup("Name");
	   gName.add(cm.getColumn(1));
	   gName.add(cm.getColumn(2));
	   
	   ColumnGroup gLang = new ColumnGroup("Language");
	   gLang.add(cm.getColumn(3));
	   
	   ColumnGroup gOther = new ColumnGroup("Others");
	   
	   gOther.add(cm.getColumn(4));
	   gOther.add(cm.getColumn(5));
	   gLang.add(gOther);
	   
	   GroupableTableHeader header = (GroupableTableHeader) table.getTableHeader();
	   
	   header.addColumnGroup(gName);
	   header.addColumnGroup(gLang);
	   
	   JScrollPane scroll = new JScrollPane(table);
	   add(scroll);
	   setSize(400, 120);   
 }

	
	
	public static void main(String[] args) {
		
		GroupableHeaderExample frame = new GroupableHeaderExample();
		
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e ) {
				System.exit(0);
			}
		});
		
		frame.setVisible(true);
 }
 
 
 
}
