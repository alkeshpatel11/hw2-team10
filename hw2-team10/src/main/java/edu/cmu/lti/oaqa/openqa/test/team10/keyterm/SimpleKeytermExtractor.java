/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.cmu.lti.oaqa.openqa.test.team10.keyterm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.alkesh.annotators.POSTagAnnotator;
import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.bio.alkesh.genetrainer.NGramLuceneWrapper;
import edu.cmu.lti.bio.alkesh.tools.PosTagNamedEntityRecognizer;
import edu.cmu.lti.bio.alkesh.types.GeneTag;
import edu.cmu.lti.bio.alkesh.types.GeneTagList;
import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

/**
 * 
 * @author Zi Yang <ziy@cs.cmu.edu>
 * 
 */
public class SimpleKeytermExtractor extends AbstractKeytermExtractor {

	private PosTagNamedEntityRecognizer posTagger;
	NGramLuceneWrapper searcher;
	int MAX_RESULTS = 50;
	int MAX_KEYTERMS=0;
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		posTagger = new PosTagNamedEntityRecognizer();
		searcher = new NGramLuceneWrapper();
	}

	@Override
	protected List<Keyterm> getKeyterms(String question) {

		question = question.replace('?', ' ');
		question = question.replace('(', ' ');
		question = question.replace('[', ' ');
		question = question.replace(')', ' ');
		question = question.replace(']', ' ');
		question = question.replace('/', ' ');
		question = question.replace('\'', ' ');

		Map<Integer, Integer> geneSpanMap = null;

		try {
			geneSpanMap = posTagger.getGeneSpans(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<GeneCount> geneList = new ArrayList<GeneCount>();
		if (geneSpanMap != null) {
			Iterator<Integer> spanIt = geneSpanMap.keySet().iterator();

			while (spanIt.hasNext()) {
				int begin = spanIt.next();
				int end = geneSpanMap.get(begin);
				// System.out.println(sentenceText.substring(begin,end));
				String geneName = question.substring(begin, end);
				// System.out.println("Annotated: "+geneTag.getGeneName()+"\t"+begin+"\t"+end);

				ArrayList<GeneCount> relatedGenes = new ArrayList<GeneCount>();
				/*try {
					relatedGenes = searcher.searchIndex(geneName, MAX_RESULTS);
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				if (relatedGenes.size() > 0) {
					relatedGenes = findBestRelatedGenes(relatedGenes);
				}
				if (relatedGenes.size() > 0) {
					geneList.addAll(relatedGenes);
				} else {
					geneList.add(new GeneCount(geneName, 1.0));
				}
			}

		}

		ArrayList<Keyterm> keyterms = new ArrayList<Keyterm>();
		/*
		 * String[] questionTokens = question.split("\\s+"); List<Keyterm>
		 * keyterms = new ArrayList<Keyterm>(); for (int i = 0; i <
		 * questionTokens.length; i++) { keyterms.add(new
		 * Keyterm(questionTokens[i])); }
		 */
		for (int i = 0; i < geneList.size(); i++) {
			keyterms.add(new Keyterm(removeEscapeChars(geneList.get(i).getGeneName())));
			System.out.println("$$$$ "+geneList.get(i).getGeneName());
		}

		return keyterms;
	}

	private ArrayList<GeneCount> findBestRelatedGenes(ArrayList<GeneCount> genes) {

		
		ArrayList<GeneCount> bestGenes = new ArrayList<GeneCount>();
		double threshold = 0.3 * genes.get(0).getCount();
		for (int i = 0; i < genes.size(); i++) {
			if (genes.get(i).getCount() >= threshold) {
				bestGenes.add(genes.get(i));
			}
			if(i>MAX_KEYTERMS){
				break;
			}
		}

		return bestGenes;
	}
	
	private String removeEscapeChars(String keyterms){
		
		keyterms = keyterms.replace('(', ' ');
		keyterms = keyterms.replace('[', ' ');
		keyterms = keyterms.replace(')', ' ');
		keyterms = keyterms.replace(']', ' ');
		keyterms = keyterms.replace('/', ' ');
		keyterms = keyterms.replace('\'', ' ');

		return keyterms;
	}
}
