package edu.cmu.lti.bio.alkesh.comparators;

import java.util.Comparator;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
/**
 * 
 * @author alkesh
 * It arranges the Gene Names in descending order of their weights
 */
public class CompGeneCount implements Comparator<GeneCount> {

	@Override
	public int compare(GeneCount o1, GeneCount o2) {

		double count1 = o1.getCount();
		double count2 = o2.getCount();
		//System.out.println(count1+"\t"+count2);
		
		if (count1 > count2) {
			return -1;
		} else if (count1 == count2) {
			return 0;
		}else{
			return 1;
		}

	}

}
