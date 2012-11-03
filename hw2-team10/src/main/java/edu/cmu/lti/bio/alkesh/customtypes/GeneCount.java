package edu.cmu.lti.bio.alkesh.customtypes;
/**
 * 
 * @author alkesh
 * Holds Gene Names and corresponding statistical score
 */
public class GeneCount {
	String geneName;
	double count;
	
	public String getGeneName() {
		return geneName;
	}
	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public GeneCount(String geneName, double count) {
		super();
		this.geneName = geneName;
		this.count = count;
	}
	
}
