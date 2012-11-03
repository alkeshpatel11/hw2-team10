package edu.cmu.lti.bio.alkesh.annotators;

import java.util.ArrayList;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.bio.alkesh.genetrainer.NGramLuceneWrapper;
import edu.cmu.lti.bio.alkesh.types.GeneTag;
import edu.cmu.lti.bio.alkesh.types.GeneTagList;

/**
 * @author alkesh
 * 
 * GeneTagAnnotator filter out some of the less likely GeneNames 
 * extracted by previous annotator. It refers indexed GeneDataBase for
 * finding relevant match with score 
 */
public class GeneTagAnnotator extends JCasAnnotator_ImplBase {

	NGramLuceneWrapper searcher = new NGramLuceneWrapper();
	int N = 100;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		FSIterator it = jCas.getAnnotationIndex(GeneTagList.type).iterator();
		GeneTagList geneTagList = null;
		if (it.hasNext()) {
			geneTagList = (GeneTagList) it.next();
		}
		String id = geneTagList.getId();
		String sentenceText = geneTagList.getText();
		// System.out.println("Sentence Id: " + geneTagList.getId());

		int i = 0;
		FSList fsList = geneTagList.getGeneList();
		while (true) {

			GeneTag geneTag = null;
			try {
				geneTag = (GeneTag) fsList.getNthElement(i);
			} catch (Exception e) {
				break;
			}

			// int start=geneTag.getStart();
			// int end=geneTag.getEnd();
			String geneName = geneTag.getGeneName();
			ArrayList<GeneCount> results = new ArrayList<GeneCount>();

			try {

				results = searcher.searchIndex(geneName, N);
			} catch (Exception e) {
				System.out.println("Searching failed for " + geneName);
				e.printStackTrace();
			}
			if (results.size() > 0) {
				geneTag.setScore(results.get(0).getCount());
				// geneTag.setGeneName(results.get(0).getGeneName());
			}

			i++;
		}

		// System.out.println("Annotated: "+sentenceText);
		GeneTagList annotation = new GeneTagList(jCas);
		annotation.setId(id);
		annotation.setGeneList(fsList);
		annotation.setText(sentenceText);
		annotation.addToIndexes();

	}

	
}
