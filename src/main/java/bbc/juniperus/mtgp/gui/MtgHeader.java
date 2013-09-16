package bbc.juniperus.mtgp.gui;

public class MtgHeader {
	
	private String source;
	private String name;
	
	public MtgHeader(String source, String name){
		this.source = source;
		this.name = name;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
