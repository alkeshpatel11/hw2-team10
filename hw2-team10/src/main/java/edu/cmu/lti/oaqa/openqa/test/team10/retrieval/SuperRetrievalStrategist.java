package edu.cmu.lti.oaqa.openqa.test.team10.retrieval;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.cse.basephase.retrieval.AbstractRetrievalStrategist;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.test.team10.keyterm.SynonymExtractor;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * 
 * @author Zeyuan Li <zeyuanl@cs.cmu.edu>
 * 
 */
public class SuperRetrievalStrategist extends AbstractRetrievalStrategist {

	protected Integer hitListSize;

	protected SolrWrapper wrapper;

	private StanfordCoreNLP pipeline;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			this.hitListSize = (Integer) aContext
					.getConfigParameterValue("hit-list-size");
		} catch (ClassCastException e) { // all cross-opts are strings?
			this.hitListSize = Integer.parseInt((String) aContext
					.getConfigParameterValue("hit-list-size"));
		}
		String serverUrl = (String) aContext.getConfigParameterValue("server");
		Integer serverPort = (Integer) aContext.getConfigParameterValue("port");
		Boolean embedded = (Boolean) aContext
				.getConfigParameterValue("embedded");
		String core = (String) aContext.getConfigParameterValue("core");

		// stanford coreNLP
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos");
		pipeline = new StanfordCoreNLP(props);

		try {
			this.wrapper = new SolrWrapper(serverUrl, serverPort, embedded,
					core);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	protected List<RetrievalResult> retrieveDocuments(String questionText,
			List<Keyterm> keyterms) {
		// use stemmed word to run the query
		boolean dostem = false;

		List<String> querys = new ArrayList<String>();
		try {
			querys = formulateQuery(questionText, keyterms, dostem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retrieveDocuments(querys);
	};

	/**
	 * formulateQuery using graduate relaxation: RS formulates an ordered
	 * sequence of queries, where the first query is the narrowest or most
	 * specific query, and each successive query is broader / less specific than
	 * the previous one
	 * 
	 * @throws Exception
	 * 
	 * */
	protected List<String> formulateQuery(String questionText,
			List<Keyterm> keyterms, boolean dostem) throws Exception {
		Annotation document = new Annotation(questionText);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> usefulwords = new LinkedList<String>();
		PorterStemmer stemmer = new PorterStemmer();
		SynonymExtractor synextrator = new SynonymExtractor();

		// include NN, VB and ADJ
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String pos = token.get(PartOfSpeechAnnotation.class);
				if (pos.contains("NN") || pos.contains("VB")
						|| pos.contains("JJ"))
					usefulwords.add(dostem ? stemmer.stem(token.value())
							: token.value());
			}
		}

		List<String> querys = new LinkedList<String>();

		// for keyword, include both stemmed word and original word
		StringBuffer result = new StringBuffer();
		if (dostem) {
			for (int i = 0; i < keyterms.size(); i++) {
				String curkeyterm = keyterms.get(i).getText();
				if (curkeyterm.split(" ").length > 1)
					curkeyterm = "\"" + curkeyterm + "\"";

				// append synonym in query string
				List<GeneCount> syns = synextrator.getSynonyms(keyterms.get(i)
						.getText());
				result.append("(" + stemmer.stem(curkeyterm));
				for (int j = 0; j < syns.size(); j++) {
					String cursyn = syns.get(j).getGeneName();
					if (cursyn.matches(".*?\\s.*+"))
						cursyn = "\"" + cursyn + "\"";

					result.append(" OR " + cursyn);
				}
				result.append(")"); // TODO: boost by keyterm weight

				if (i < keyterms.size() - 1) {
					result.append(" AND ");
				}
			}
		} else {
			for (int i = 0; i < keyterms.size(); i++) {
				String curkeyterm = keyterms.get(i).getText();
				if (curkeyterm.length() > 1)
					curkeyterm = "\"" + curkeyterm + "\"";

				// append synonym in query string
				List<GeneCount> syns = synextrator.getSynonyms(keyterms.get(i)
						.getText());
				result.append("(" + curkeyterm);
				for (int j = 0; j < syns.size(); j++) {
					String cursyn = syns.get(j).getGeneName();
					if (cursyn.matches(".*?\\s.*+"))
						cursyn = "\"" + cursyn + "\"";

					result.append(" OR " + cursyn);
				}
				result.append(")"); // TODO: boost by keyterm weight

				if (i < keyterms.size() - 1)
					result.append(" AND ");
			}
		}
		// make sure key term is always included and boosted
		String qmain = "(" + result.toString() + ")^5 ";
		// querys.add(qmain);

		for (int n = 0; n < usefulwords.size(); n++) {
			result = new StringBuffer();
			result.append(qmain);
			for (int i = n; i < usefulwords.size(); i++) {
				result.append(" AND " + usefulwords.get(i));
			}

			System.out.println(" QUERY: " + result.toString());
			querys.add(result.toString());
		}

		return querys;
	}

	protected List<RetrievalResult> retrieveDocuments(List<String> querys) {
		List<RetrievalResult> result = new ArrayList<RetrievalResult>();
		List<String> idlist = new ArrayList<String>();

		try {
			// ensure returned result lists size <= hitListSize
			for (int i = 0; i < querys.size(); i++) {
				String query = querys.get(i);
				SolrDocumentList docs = wrapper.runQuery(query, hitListSize);
				for (SolrDocument doc : docs) {
					if (!idlist.contains((String) doc.getFieldValue("id"))
							&& result.size() < hitListSize) {
						idlist.add((String) doc.getFieldValue("id"));
						RetrievalResult r = new RetrievalResult(
								(String) doc.getFieldValue("id"),
								(Float) doc.getFieldValue("score"), query);
						result.add(r);
						System.out.println(doc.getFieldValue("id"));
					} else if (result.size() >= hitListSize)
						break;
				}

				if (result.size() >= hitListSize)
					break;
			}

		} catch (Exception e) {
			System.err.println("Error retrieving documents from Solr: " + e);
		}
		return result;
	}

	protected List<RetrievalResult> retrieveDocuments(String query) {
		List<RetrievalResult> result = new ArrayList<RetrievalResult>();
		try {
			SolrDocumentList docs = wrapper.runQuery(query, hitListSize);
			for (SolrDocument doc : docs) {
				RetrievalResult r = new RetrievalResult(
						(String) doc.getFieldValue("id"),
						(Float) doc.getFieldValue("score"), query);
				result.add(r);
				System.out.println(doc.getFieldValue("id"));
			}
		} catch (Exception e) {
			System.err.println("Error retrieving documents from Solr: " + e);
		}
		return result;
	}

	@Override
	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		wrapper.close();
	}
}
