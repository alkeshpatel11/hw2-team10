package edu.cmu.lti.bio.alkesh.customtypes;

public class SentenceInfo {
	
	String id;
	String text;
	
	public SentenceInfo(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	

}
