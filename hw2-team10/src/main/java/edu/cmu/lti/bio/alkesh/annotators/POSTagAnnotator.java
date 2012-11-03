package edu.cmu.lti.bio.alkesh.annotators;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;

import org.apache.uima.resource.ResourceInitializationException;
import java.util.ArrayList;

import edu.cmu.lti.bio.alkesh.tools.PosTagNamedEntityRecognizer;
import edu.cmu.lti.bio.alkesh.types.GeneTag;
import edu.cmu.lti.bio.alkesh.types.GeneTagList;
import edu.cmu.lti.bio.alkesh.types.Sentence;


/**
 * @author alkesh
 * 
 * POSTagAnnotator is responsible for extracting the possible
 * Named Entities using Stanford-CoreNLP tool
 */

public class POSTagAnnotator extends JCasAnnotator_ImplBase {

	private PosTagNamedEntityRecognizer posTagger;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		posTagger = new PosTagNamedEntityRecognizer();
	}
	
	/**
	 * Annotates the text passed in jCas with possible Gene names from the text 
	 * 
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		FSIterator it = jCas.getAnnotationIndex(Sentence.type).iterator();
		Sentence sentence = null;
		if (it.hasNext()) {
			sentence = (Sentence) it.next();
		}

		String id = sentence.getId();
		String sentenceText = sentence.getText();

		Map<Integer, Integer> geneSpanMap = null;

		try {
			geneSpanMap = posTagger.getGeneSpans(sentenceText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (geneSpanMap != null) {
			Iterator<Integer> spanIt = geneSpanMap.keySet().iterator();
			ArrayList<GeneTag> geneList = new ArrayList<GeneTag>();

			while (spanIt.hasNext()) {
				int begin = spanIt.next();
				int end = geneSpanMap.get(begin);
				GeneTag geneTag = new GeneTag(jCas);
				// System.out.println(sentenceText.substring(begin,end));
				String geneName=sentenceText.substring(begin, end);
				geneTag.setGeneName(geneName);
				geneTag.setStart(begin);
				geneTag.setEnd(end);
				geneTag.setScore(0.0);
				//System.out.println("Annotated: "+geneTag.getGeneName()+"\t"+begin+"\t"+end);
				geneTag.addToIndexes();
				geneList.add(geneTag);
			}
			

			FSList fsList = createGeneTagList(jCas, geneList);

			GeneTagList annotation = new GeneTagList(jCas);
			annotation.setId(id);
			annotation.setGeneList(fsList);
			annotation.setText(sentenceText);
			annotation.addToIndexes();
		}
	}

	/**
	 * Creates FeatureStructure List from GeneTagList
	 * @param aJCas
	 * @param aCollection
	 * @return FSList
	 */
	
	public FSList createGeneTagList(JCas aJCas, Collection<GeneTag> aCollection) {
		if (aCollection.size() == 0) {
			return new EmptyFSList(aJCas);
		}

		NonEmptyFSList head = new NonEmptyFSList(aJCas);
		NonEmptyFSList list = head;
		Iterator<GeneTag> i = aCollection.iterator();
		while (i.hasNext()) {
			head.setHead(i.next());
			if (i.hasNext()) {
				head.setTail(new NonEmptyFSList(aJCas));
				head = (NonEmptyFSList) head.getTail();
			} else {
				head.setTail(new EmptyFSList(aJCas));
			}
		}

		return list;
	}

}
