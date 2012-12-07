package edu.cmu.lti.oaqa.openqa.test.team10.keyterm;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.bio.alkesh.genetrainer.NGramLuceneWrapper;
import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

/**
 * 
 * @author alkesh
 * 
 */
public class SimilarNGramExtractor extends AbstractKeytermExtractor {

	int nSimilarGrams = 10;
	float filterThreshold = 0.30f;
	NGramLuceneWrapper nGramLuceneWrapper;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		System.out.println("Reached here");
		try {
			nSimilarGrams = (Integer) aContext.getConfigParameterValue(
					"ngram-size", "10");
			filterThreshold = (Float) aContext.getConfigParameterValue(
					"ngram-score-threshold", "0.30f");

			nGramLuceneWrapper = new NGramLuceneWrapper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<GeneCount> getSynonyms(String term) throws Exception {

		// main.createIndex();

		ArrayList<GeneCount> geneList = nGramLuceneWrapper.searchIndex(term,
				nSimilarGrams);
		ArrayList<GeneCount> selectedList = new ArrayList<GeneCount>();
		if (geneList.size() > 0) {

			double thr = geneList.get(0).getCount() * filterThreshold;
			for (int i = 0; i < geneList.size(); i++) {
				// System.out.println(geneList.get(i).getGeneName()+"\t"+geneList.get(i).getCount());
				if (geneList.get(i).getCount() > thr) {
					selectedList.add(geneList.get(i));
				} else {
					break;
				}
			}
		}
		return selectedList;
	}

	@Override
	protected List<Keyterm> getKeyterms(String term) {
		List<Keyterm> keyterms = new ArrayList<Keyterm>();
		ArrayList<GeneCount> similarNgrams = new ArrayList<GeneCount>();
		try {
			getSynonyms(term);
		} catch (Exception e) {
			e.printStackTrace();
			return keyterms;
		}

		for (int i = 0; i < similarNgrams.size(); i++) {
			keyterms.add(new Keyterm(similarNgrams.get(i).getGeneName()));
		}

		return keyterms;

	}

	public static void main(String args[]) {
		try {
			SimilarNGramExtractor extractor = new SimilarNGramExtractor();
			ArrayList<GeneCount> list = extractor
					.getSynonyms("beta-amyloid precursor");
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).getGeneName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
