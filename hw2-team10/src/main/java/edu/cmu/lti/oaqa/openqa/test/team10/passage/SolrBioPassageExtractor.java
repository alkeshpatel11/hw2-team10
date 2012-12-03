package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;

public class SolrBioPassageExtractor extends SimpleBioPassageExtractor {

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
				query.set("q", question);
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
}
