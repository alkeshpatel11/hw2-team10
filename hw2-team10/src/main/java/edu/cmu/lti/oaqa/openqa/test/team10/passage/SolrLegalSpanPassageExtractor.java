package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.bio.retrieval.query.strategy.QueryStrategy;
import edu.cmu.lti.oaqa.bio.retrieval.query.structure.QueryComponent;
import edu.cmu.lti.oaqa.bio.retrieval.query.structure.QueryComponentContainer;
import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;

public class SolrLegalSpanPassageExtractor extends SimpleBioPassageExtractor {

	private String backupQuery;
	private boolean useUMLS;

	private boolean useENTREZ;

	private boolean useMESH;

	private boolean useUMLSAcronym;

	private boolean useENTREZAcronym;

	private boolean useMESHAcronym;

	private boolean useLexicalVariants;
	private boolean usePosTagger;

	private int passageListSize;
	SolrWrapper solrWrapper;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			this.passageListSize = (Integer) aContext
					.getConfigParameterValue("num-passage");
		} catch (ClassCastException e) { // all cross-opts are strings?
			this.passageListSize = Integer.parseInt((String) aContext
					.getConfigParameterValue("num-passage"));
		}
		String serverUrl = (String) aContext.getConfigParameterValue("server");
		Integer serverPort = (Integer) aContext.getConfigParameterValue("port");
		Boolean embedded = (Boolean) aContext
				.getConfigParameterValue("embedded");
		String core = (String) aContext.getConfigParameterValue("core");
		try {
			this.solrWrapper = new SolrWrapper(serverUrl, serverPort, embedded,
					core);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	protected List<PassageCandidate> extractPassages(String question,
			List<Keyterm> keyterms, List<RetrievalResult> documents) {
		List<PassageCandidate> result = new ArrayList<PassageCandidate>();
		String newquestion = "";
		for (int i = 0; i < keyterms.size(); i++) {
			newquestion += keyterms.get(i).getText() + " ";
		}
		// String newquestion=this.formulateQuery(keyterms);
		for (RetrievalResult document : documents) {
			System.out.println("RetrievalResult: " + document.toString());
			String id = document.getDocID();
			try {
				// String htmlText = wrapper.getDocText(id);

				// HashMap<String,String>paramMap=new HashMap<String,String>();
				// paramMap.put("q", question);
				// paramMap.put("fq", "docid:"+id);
				// SolrParams params=new MapSolrParams(paramMap);
				SolrQuery query = new SolrQuery();
				query.set("q", newquestion);// question
				query.setFilterQueries("docid:" + id);
				query.setFields("score", "begin", "end");

				SolrDocumentList passageList = solrWrapper.runQuery(query,
						passageListSize);
				for (int i = 0; i < passageList.size(); i++) {
					SolrDocument doc = passageList.get(i);
					Integer startStr = (Integer) doc.getFieldValue("begin");
					Integer endStr = (Integer) doc.getFieldValue("end");
					Float scoreStr = (Float) doc.getFieldValue("score");
					int start = 0;
					int end = 0;
					float score = 0.0f;

					if (startStr != null) {
						start = startStr.intValue();
					}
					if (endStr != null) {
						end = endStr.intValue();
					}
					if (scoreStr != null) {
						score = scoreStr.floatValue();
					}
					PassageCandidate passageCandidate = new PassageCandidate(
							id, start, end, score, question);
					result.add(passageCandidate);
				}
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (AnalysisEngineProcessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	protected String formulateQuery(List<Keyterm> keyterms) {

		// QueryGenerator qs = new QueryGenerator(keyterms);
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();

		map.put("umls", useUMLS);
		map.put("entrez", useENTREZ);
		map.put("mesh", useMESH);
		map.put("lexical_variants", useLexicalVariants);
		map.put("postagger", usePosTagger);
		map.put("acronym_umls", useUMLSAcronym);
		map.put("acronym_entrez", useENTREZAcronym);
		map.put("acronym_mesh", useMESHAcronym);

		this.backupQuery = generateSolrQuery(false, map, "", keyterms);

		String s2 = generateSolrQuery(true, map, "", keyterms);
		System.out.println("Query~~~:" + s2);

		return s2;
	}

	/**
	 * Generates the Solr query, considering boolean-filter, resource selection
	 * and which field to search.
	 * 
	 * @param filter
	 *            Whether to use the boolean-filter in Solr or not
	 * @param map
	 *            The map contains all the resource selections
	 * @param field
	 *            The field to search in Solr. Possible options are
	 *            "[legalspan]", "[sentence], etc.
	 * @return formed Solr query
	 */
	public String generateSolrQuery(boolean filter,
			HashMap<String, Boolean> map, String field, List<Keyterm> keyTerms) {

		QueryStrategy refiner = new QueryStrategy(keyTerms);
		HashMap<String, Boolean> resourceFilter = new HashMap<String, Boolean>();
		if (map.isEmpty()) {
			resourceFilter = getDefaultResourceFilter();
		} else {
			resourceFilter.putAll(map);
		}

		refiner.hasUMLS(resourceFilter.get("umls"));
		refiner.hasEntrez(resourceFilter.get("entrez"));
		refiner.hasMESH(resourceFilter.get("mesh"));
		refiner.hasLexicalVariants(resourceFilter.get("lexical_variants"));
		refiner.hasPOSTagger(resourceFilter.get("postagger"));
		refiner.hasUMLSAcronym(resourceFilter.get("acronym_umls"));
		refiner.hasEntrezAcronym(resourceFilter.get("acronym_entrez"));
		refiner.hasMESHAcronym(resourceFilter.get("acronym_mesh"));

		QueryComponentContainer qc = refiner.getAllQueryComponents();

		String query = ""; // general query

		for (QueryComponent q : qc.getQueryComponent()) {

			String keyterm = "";

			// wraps the keyterm if it is a phrase. Using "2" here
			// is based on experiment.
			float weight = Float.parseFloat(q.getWeight());
			keyterm = q.getKeyterm().getText().contains(" ") ? "\""
					+ q.getKeyterm().getText() + "\" ^" + (2.0 * weight) : q
					.getKeyterm().getText();

			// wraps the keyterm and synonyms
			// String dismaxOR="( ";
			String tempMain = "";

			for (int i = 0; i < q.getSynonyms().size(); i++) {
				String synonym = q.getSynonyms().get(i).trim();
				if (synonym.isEmpty()) {
					continue;
				}
				synonym = synonym.contains(" ") ? "\"" + synonym + "\" ^ "
						+ String.valueOf((2.0 * weight)) : synonym;
				tempMain += synonym + " | ";
			}
			tempMain = tempMain.trim().replaceAll("[|]$", "").trim();
			if (!tempMain.equals("")) {
				query += "( " + keyterm + " ( " + tempMain + " ))";
			} else {
				query += "( " + keyterm + " ) ";
			}

		}

		// System.out.println("#### Query: " + query);
		return query;
	}

	private HashMap<String, Boolean> getDefaultResourceFilter() {
		HashMap<String, Boolean> resourceFilter = new HashMap<String, Boolean>();
		resourceFilter.put("umls", false);
		resourceFilter.put("entrez", true);
		resourceFilter.put("mesh", true);
		resourceFilter.put("lexical_variants", true);
		resourceFilter.put("postagger", true);
		resourceFilter.put("acronym_umls", true);
		resourceFilter.put("acronym_entrez", false);
		resourceFilter.put("acronym_mesh", false);
		return resourceFilter;
	}

}
