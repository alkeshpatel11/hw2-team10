package edu.cmu.lti.bio.alkesh.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
//import org.apache.uima.cas.Feature;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.jsoup.Jsoup;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;

public class LegalSpanIndexing {

	String XMI_REPOSITORY = "C:/Users/alkesh/Downloads/Trec06_annotated_xmi/";
	String TYPE_DESC_XML = "src/main/resources/edu/cmu/lti/oaqa/bio/model/bioTypes.xml";
	//String SOLR_SERVER_URL = "http://localhost:8983/solr/genomics-legalspan/";
	String SOLR_SERVER_URL = "http://peace.isri.cs.cmu.edu:9080/solr/genomics-legalspan/";

	SolrWrapper solrWrapper = null;
	public LegalSpanIndexing() {
		try {
			// ResourceBundle res = ResourceBundle.getBundle(configFile);
			// setConfig(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * private void setConfig(ResourceBundle res) { this.XMI_REPOSITORY =
	 * res.getString("xmi.repository"); this.TYPE_DESC_XML =
	 * res.getString("uima.type.description"); this.SOLR_SERVER_URL =
	 * res.getString("solr.server.url"); }
	 */

	public boolean isIndexed(String id, SolrWrapper solrWrapper)
			throws Exception {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("q", "docid:" + id);
		map.put("rows", "1");
		SolrParams params = new MapSolrParams(map);
		// SolrQuery query = new SolrQuery(params);
		QueryResponse resp = solrWrapper.getServer().query(params);
		if (resp.getResults().size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public File[] getDocumentIdsFromFile() throws Exception {
		String filePath = "src/main/resources/gs/trecgen06.passage";
		BufferedReader bfr = new BufferedReader(new FileReader(filePath));
		String str;
		ArrayList<File> docIdList = new ArrayList<File>();
		while ((str = bfr.readLine()) != null) {
			String rec[] = str.trim().split("[\\t]");

			if (rec.length > 2) {
				docIdList.add(new File(
						"C:/Users/alkesh/Downloads/Trec06_annotated_xmi/"
								+ rec[1] + ".xmi"));
			}
		}
		bfr.close();
		bfr = null;

		return docIdList.toArray(new File[0]);
	}

	public void distributeFiles(String sourceDirectory) throws Exception {
		String baseDir = "C:/Users/alkesh/Downloads/ParallelIndexing/";
		File files[] = new File(sourceDirectory).listFiles();
		int folder = 0;
		File newFile = new File(baseDir + folder);
		if (!newFile.exists()) {
			newFile.mkdirs();
		}
		String dir = newFile.getAbsolutePath();
		for (int i = files.length - 1; i >= 0; i--) {
			String file = dir + "\\" + files[i].getName();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			bfw.write(files[i].getAbsolutePath());
			bfw.close();
			bfw = null;

			if ((i + 1) % 2048 == 0) {
				folder++;
				newFile = new File(baseDir + folder);
				if (!newFile.exists()) {
					newFile.mkdirs();
				}
				dir = newFile.getAbsolutePath();
			}
		}

	}

	public static void main1(String args[]) {
		try {
			LegalSpanIndexing main = new LegalSpanIndexing();
			main.distributeFiles("C:/Users/alkesh/Downloads/Trec06_annotated_xmi/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		

		try {

			LegalSpanIndexing main = new LegalSpanIndexing();

			// File files[] = new File(main.XMI_REPOSITORY).listFiles();
			// main.getDocumentIdsFromFile();//
			main.solrWrapper = new SolrWrapper(main.SOLR_SERVER_URL, null, null,
					null);
			XMLInputSource input = new XMLInputSource(main.TYPE_DESC_XML);
			TypeSystemDescription typeDesc = UIMAFramework.getXMLParser()
					.parseTypeSystemDescription(input);

			CAS cas = CasCreationUtils.createCas(typeDesc, null,
					new FsIndexDescription[0]);
			// boolean cont = false;
			File queue[] = new File(
					"C:/Users/alkesh/Downloads/ParallelIndexing/").listFiles();
			for(int i=0;i<queue.length;i++){
				main.bulkIndexing(queue[i].listFiles(), cas);
			}
			/////Multithread implementation
			/*IndexingThread indexingThread[] = new IndexingThread[queue.length];
			for (int i = 0; i < indexingThread.length; i++) {
				indexingThread[i] = new IndexingThread(queue[i], cas,
						main.solrWrapper.getServer());
				indexingThread[i].start();
			}

			while (true) {
				boolean stop = true;
				for (int i = 0; i < indexingThread.length; i++) {
					if (indexingThread[i].isAlive()) {
						stop = false;
					}
				}
				for (int i = 0; i < indexingThread.length; i++) {
					System.out.println(indexingThread[i].getProcessed());
				}

				if (stop) {
					break;
				}
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//if (main.solrWrapper != null) {
				//main.solrWrapper.close();
			//}
		}

	}

	public void bulkIndexing(File files[], CAS cas) throws Exception {
		for (int i = 0; i < files.length; i++) {

			String fileName = files[i].getName();
			File xmiFile = new File(this.XMI_REPOSITORY + fileName);// files[i].getAbsolutePath();

			String id = fileName.replace(".xmi", "").trim();

			/*
			 * if (main.isIndexed(id, solrWrapper)) { continue; }
			 */

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

					// IndexingThread indexingThread = new IndexingThread(id,
					// hshMap, solrWrapper.getServer());
					// indexingThread.start();
					SolrInputDocument solrDoc = makeSolrDocument(hshMap);
					String docXML = ClientUtils.toXML(solrDoc);
					indexDocument(docXML);

				}

				files[i].delete();
			}
			System.out.println(count + " Legalspan added for " + id);
			if (i % 100 == 0) {
				solrWrapper.getServer().commit();
			}
			// System.out.println(i + " indexed with docno: " + id);
		}

	}

	public void indexDocument(String docXML) {

		String xml = "<add>" + docXML + "</add>";
		// System.out.println(xml);
		DirectXmlRequest xmlreq = new DirectXmlRequest("/update", xml);
		try {
			solrWrapper.getServer().request(xmlreq);
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
