package edu.cmu.lti.bio.alkesh.comparators;

import java.util.Comparator;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;

/**
 * 
 * @author alkesh
 * It arranges the Gene Names in ascending order of GeneNames
 */
public class CompGeneName implements Comparator<GeneCount>{

	public int compare(GeneCount o1,GeneCount o2){
		
		String s1=o1.getGeneName();
		String s2=o2.getGeneName();
		
		return s1.compareToIgnoreCase(s2);
		
	}
	
}
