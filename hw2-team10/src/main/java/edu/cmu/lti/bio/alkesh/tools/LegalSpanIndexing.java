package edu.cmu.lti.bio.alkesh.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
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
	String SOLR_SERVER_URL = "http://localhost:8983/solr/genomics-legalspan/";

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
	public static void main(String args[]) {
		SolrWrapper solrWrapper = null;

		try {

			LegalSpanIndexing main = new LegalSpanIndexing();

			File files[] = new File(main.XMI_REPOSITORY).listFiles();

			solrWrapper = new SolrWrapper(main.SOLR_SERVER_URL, null, null,
					null);
			XMLInputSource input = new XMLInputSource(main.TYPE_DESC_XML);
			TypeSystemDescription typeDesc = UIMAFramework.getXMLParser()
					.parseTypeSystemDescription(input);

			CAS cas = CasCreationUtils.createCas(typeDesc, null,
					new FsIndexDescription[0]);
			boolean cont=false;
			for (int i = 0; i < files.length; i++) {

				if (cont==false && files[i].getName().equals("12411320.xmi")) {
					cont=true;
					
				}
				if(!cont){
					continue;
				}

				String currentFile = files[i].getAbsolutePath();

				FileInputStream inputStream = new FileInputStream(currentFile);
				try {
					XmiCasDeserializer.deserialize(inputStream, cas, true);
				} catch (Exception e) {
					throw new CollectionException(e);
				} finally {
					inputStream.close();
				}

				String fileName = files[i].getName();
				String id = fileName.replace(".xmi", "").trim();
				Iterator<AnnotationFS> it = cas.getAnnotationIndex().iterator();
				// ArrayList<String> paraList = new ArrayList<String>();
				int count = 0;
				while (it.hasNext()) {
					AnnotationFS val = it.next();

					if (val.getType().getName()
							.equals("edu.cmu.lti.bio.trec.LegalSpan")) {
						// int start = val.getBegin();
						// int end = val.getEnd();
						// System.out.println(start + "\t" + end);

						String paragraph = Jsoup.parse(val.getCoveredText())
								.text();

						Date now = new Date();
						HashMap<String, Object> hshMap = new HashMap<String, Object>();
						hshMap.put("id", id + "_" + (count++));
						hshMap.put("docid", id);
						hshMap.put("text", paragraph);
						// hshMap.put("paragraph", paragraph);
						hshMap.put("timestamp", now);

						IndexingThread indexingThread = new IndexingThread(id,
								hshMap, solrWrapper.getServer());
						indexingThread.start();
					}
				}
				System.out.println(count+" Legalspan added for "+id);
				solrWrapper.getServer().commit();
				// System.out.println(i + " indexed with docno: " + id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (solrWrapper != null) {
				solrWrapper.close();
			}
		}

	}

}
