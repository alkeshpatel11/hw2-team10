package edu.cmu.lti.bio.alkesh.tools;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.jsoup.Jsoup;

public class IndexingThread extends Thread {

	CAS cas;
	String threadName;
	SolrServer solrServer;
	File files[];
	int processed=0;
	String XMI_REPOSITORY = "C:/Users/alkesh/Downloads/Trec06_annotated_xmi/";

	public IndexingThread(File dir, CAS cas,SolrServer server) {
		files=dir.listFiles();
		threadName=dir+" processing thread";
		this.cas=cas;
		this.solrServer = server;
	}
	
	public int getProcessed(){
		return processed;
	}
	public String getThreadName(){
		return threadName;
	}

	public void run() {

		try {
			// String htmlText = cas.getDocumentText();
			this.bulkIndexing();
			
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
	
	public void bulkIndexing() throws Exception{
		for (int i = 0; i <files.length; i++) {

			String fileName = files[i].getName();
			File xmiFile = new File(this.XMI_REPOSITORY+fileName);//files[i].getAbsolutePath();
			
			String id = fileName.replace(".xmi", "").trim();

			/*if (main.isIndexed(id, solrWrapper)) {
				continue;
			}*/

			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(xmiFile);

				XmiCasDeserializer.deserialize(inputStream, cas, true);
			} catch (Exception e) {
				e.printStackTrace();
				if (inputStream != null) {
					inputStream.close();
				}
				continue;
				// throw new CollectionException(e);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}

			}

			Iterator<AnnotationFS> it = cas.getAnnotationIndex().iterator();
			// ArrayList<String> paraList = new ArrayList<String>();
			int count = 0;
			while (it.hasNext()) {
				AnnotationFS val = it.next();

				if (val.getType().getName()
						.equals("edu.cmu.lti.bio.trec.LegalSpan")) {
					int start = val.getBegin();
					int end = val.getEnd();
					// System.out.println(start + "\t" + end);

					// String paragraph = Jsoup.parse(val.getCoveredText())
					// .text();

					String paragraph = val.getCoveredText();
					Date now = new Date();
					HashMap<String, Object> hshMap = new HashMap<String, Object>();
					hshMap.put("id", id + "_" + (count++));
					hshMap.put("docid", id);
					hshMap.put("text", paragraph);
					hshMap.put("begin", start);
					hshMap.put("end", end);
					// hshMap.put("paragraph", paragraph);
					hshMap.put("timestamp", now);

					//IndexingThread indexingThread = new IndexingThread(id,
						//	hshMap, solrWrapper.getServer());
					//indexingThread.start();
					SolrInputDocument solrDoc = makeSolrDocument(hshMap);
					String docXML = ClientUtils.toXML(solrDoc);
					indexDocument(docXML);

				}
				
				files[i].delete();
				this.processed++;
			}
			System.out.println(count + " Legalspan added for " + id);
			if (i % 100 == 0) {
				solrServer.commit();
			}
			// System.out.println(i + " indexed with docno: " + id);
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
