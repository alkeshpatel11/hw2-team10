package edu.cmu.lti.bio.alkesh.tools;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

public class IndexingUtils {

	SolrServer solrServer;
	public IndexingUtils(SolrServer server){
		solrServer=server;
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
