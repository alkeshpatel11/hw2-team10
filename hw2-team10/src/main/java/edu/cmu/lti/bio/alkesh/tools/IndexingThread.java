package edu.cmu.lti.bio.alkesh.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.jsoup.Jsoup;

public class IndexingThread extends Thread {

	String docid;
	HashMap<String, Object> indexMap;
	SolrServer solrServer;

	public IndexingThread(String id, HashMap<String, Object> idxMap,
			SolrServer server) {
		this.docid = id;
		this.indexMap = idxMap;
		this.solrServer = server;
	}

	public void run() {

		try {
			// String htmlText = cas.getDocumentText();

			SolrInputDocument solrDoc = makeSolrDocument(indexMap);
			String docXML = ClientUtils.toXML(solrDoc);
			indexDocument(docXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void indexDocument(String docXML) {

		String xml = "<add>" + docXML + "</add>";
		// System.out.println(xml);
		DirectXmlRequest xmlreq = new DirectXmlRequest("/update", xml);
		try {
			solrServer.request(xmlreq);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public SolrInputDocument makeSolrDocument(HashMap<String, Object> hshMap)
			throws Exception {

		SolrInputDocument doc = new SolrInputDocument();

		Iterator<String> keys = hshMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = hshMap.get(key);

			SolrInputField field = new SolrInputField(key);
			try {
				doc.addField(field.getName(), value, 1.0f);

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

		return doc;

	}

}
