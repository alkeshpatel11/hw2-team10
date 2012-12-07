package edu.cmu.lti.oaqa.openqa.test.team10.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.bio.retrieval.query.strategy.QueryStrategy;
import edu.cmu.lti.oaqa.bio.retrieval.query.structure.QueryComponent;
import edu.cmu.lti.oaqa.bio.retrieval.query.structure.QueryComponentContainer;
import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.cse.basephase.retrieval.AbstractRetrievalStrategist;
import edu.cmu.lti.oaqa.framework.UimaContextHelper;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;

/**
 * this strategy integrated synonyms, lexical variants, etc.
 * 
 * @author alkeshku <alkeshku@andrew.cmu.edu>
 */
public class EnhancedSolrRetrievalStrategist extends
		AbstractRetrievalStrategist {

	private Integer hitListSize;

	
	private String backupQuery;

	private boolean useUMLS;

	private boolean useENTREZ;

	private boolean useMESH;

	private boolean useUMLSAcronym;

	private boolean useENTREZAcronym;

	private boolean useMESHAcronym;

	private boolean useLexicalVariants;

	private boolean usePosTagger;

	private SolrWrapper solrWrapper;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		// Gets values from the yaml files
		this.hitListSize = (Integer) aContext
				.getConfigParameterValue("hit-list-size");
		this.useENTREZ = UimaContextHelper.getConfigParameterBooleanValue(
				aContext, "ENTREZ", false);
		this.useMESH = UimaContextHelper.getConfigParameterBooleanValue(
				aContext, "MESH", false);
		this.useUMLS = UimaContextHelper.getConfigParameterBooleanValue(
				aContext, "UMLS", false);
		this.useENTREZAcronym = UimaContextHelper
				.getConfigParameterBooleanValue(aContext, "ENTREZ-Acronym",
						false);
		this.useMESHAcronym = UimaContextHelper.getConfigParameterBooleanValue(
				aContext, "MESH-Acronym", false);
		this.useUMLSAcronym = UimaContextHelper.getConfigParameterBooleanValue(
				aContext, "UMLS-Acronym", false);
		this.useLexicalVariants = UimaContextHelper
				.getConfigParameterBooleanValue(aContext, "LexicalVariants",
						false);
		this.usePosTagger = UimaContextHelper.getConfigParameterBooleanValue(
				aContext, "PosTagger", false);

		String SERVER_URL = UimaContextHelper.getConfigParameterStringValue(
				aContext, "server",
				"http://peace.isri.cs.cmu.edu:9080/solr/genomics-simple/");
		int SERVER_PORT = UimaContextHelper.getConfigParameterIntValue(
				aContext, "port", 9080);
		try {
			solrWrapper = new SolrWrapper(SERVER_URL, SERVER_PORT, null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// @Override
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

	@Override
	protected List<RetrievalResult> retrieveDocuments(String question,
			List<Keyterm> keyterms) {
		ArrayList<RetrievalResult> result = new ArrayList<RetrievalResult>();

		try {

			String newquestion="";
			for(int i=0;i<keyterms.size();i++){
				newquestion+=keyterms.get(i).getText()+" ";
			}
			
			// set retrieval rules for Solr
			SolrDocumentList docList = solrWrapper.runQuery(newquestion,
					hitListSize);

			String[] docnos = new String[hitListSize];
			String[] docnos2 = new String[hitListSize];

			for (int i = 0; i < docList.size(); i++) {
				SolrDocument doc = docList.get(i);
				String docid = doc.getFieldValue("id").toString();
				float score = (Float) doc.getFieldValue("score");
				docnos[i] = docid;
				RetrievalResult r = new RetrievalResult(docnos[i], score,
						question);// Math.exp(sers[i].score)
				result.add(r);
			}

			/*
			 * If there are not enough documents retrieved from boolean complex
			 * query, use general complex query to guarantee enough documents
			 */
			if (docnos.length < hitListSize) {
				SolrDocumentList list = solrWrapper.runQuery(backupQuery,
						hitListSize - docnos.length);

				for (int j = 0; j < docnos2.length; j++) {
					SolrDocument doc = list.get(j);
					String docid = doc.getFieldValue("id").toString();
					float score = (Float) doc.getFieldValue("score");

					RetrievalResult r = new RetrievalResult(docid, score,
							backupQuery);
					result.add(r);
				}
			}

		} catch (Exception e) {
			System.err.println("Error retrieving documents from Solr: " + e);
		}
		return result;

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

		//System.out.println("#### Query: " + query);
		return query;
	}

}