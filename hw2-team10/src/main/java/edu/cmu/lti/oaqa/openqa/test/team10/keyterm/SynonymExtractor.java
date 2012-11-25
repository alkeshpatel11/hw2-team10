package edu.cmu.lti.oaqa.openqa.test.team10.keyterm;

import java.util.ArrayList;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.bio.alkesh.genetrainer.NGramLuceneWrapper;

public class SynonymExtractor {

	public ArrayList<GeneCount> getSynonyms(String term) throws Exception {

		NGramLuceneWrapper main = new NGramLuceneWrapper();
		// main.createIndex();
		ArrayList<GeneCount> geneList = main.searchIndex(term, 100);
		ArrayList<GeneCount> selectedList = new ArrayList<GeneCount>();
		if (geneList.size() > 0) {
			
			double thr=geneList.get(0).getCount()*0.30;
			for (int i = 0; i < geneList.size(); i++) {
				// System.out.println(geneList.get(i).getGeneName()+"\t"+geneList.get(i).getCount());
				if (geneList.get(i).getCount() > thr) {
					selectedList.add(geneList.get(i));
				}
				if (i > 10) {
					break;
				}
			}
		}
		return selectedList;
	}
	
	public static void main(String args[]){
		try{
			SynonymExtractor extractor=new SynonymExtractor();
			ArrayList<GeneCount>list=extractor.getSynonyms("beta-amyloid precursor");
			for(int i=0;i<list.size();i++){
				System.out.println(list.get(i).getGeneName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
