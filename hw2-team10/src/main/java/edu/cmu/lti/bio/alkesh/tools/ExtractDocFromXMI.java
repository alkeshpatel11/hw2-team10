package edu.cmu.lti.bio.alkesh.tools;

/*
 *  Copyright 2012 alkeshku.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.jsoup.Jsoup;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;

/**
 * 
 * @author alkeshku
 */
public class ExtractDocFromXMI {

	String XMI_REPOSITORY = "C:/Users/alkesh/Downloads/Trec06_annotated_xmi/";
	String TYPE_DESC_XML = "src/main/resources/edu/cmu/lti/oaqa/bio/model/bioTypes.xml";
	String SOLR_SERVER_URL = "http://peace.isri.cs.cmu.edu:9080/solr/genomics-simple/";
	// String SOLR_SERVER_URL = "http://localhost:8983/solr/genomics-simple1/";
	SolrWrapper solrWrapper = null;

	public ExtractDocFromXMI(String configFile) {
		try {
			// ResourceBundle res = ResourceBundle.getBundle(configFile);
			// setConfig(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setConfig(ResourceBundle res) {
		this.XMI_REPOSITORY = res.getString("xmi.repository");
		this.TYPE_DESC_XML = res.getString("uima.type.description");
		this.SOLR_SERVER_URL = res.getString("solr.server.url");
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

	public static void main(String args[]) {

		ExtractDocFromXMI main = null;
		try {

			if (args.length == 0) {
				args = new String[1];
				args[0] = new String("solrindexprocess");

			}
			main = new ExtractDocFromXMI(args[0]);

			File files[] = new File(main.XMI_REPOSITORY).listFiles();

			main.solrWrapper = new SolrWrapper(main.SOLR_SERVER_URL, null,
					null, null);
			XMLInputSource input = new XMLInputSource(main.TYPE_DESC_XML);
			TypeSystemDescription typeDesc = UIMAFramework.getXMLParser()
					.parseTypeSystemDescription(input);

			CAS cas = CasCreationUtils.createCas(typeDesc, null,
					new FsIndexDescription[0]);
			//HashSet<String>hshSet=new HashSet<String>();
			//boolean flag=false;
			for (int i = 0; i < files.length; i++) {
//13500
				String currentFile = files[i].getAbsolutePath();
				if(i<40075){
					//flag=true;
					continue;
				}
				
				FileInputStream inputStream = new FileInputStream(currentFile);
				try {
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

				String fileName = files[i].getName();
				String id = fileName.replace(".xmi", "").trim();
				// int start=cas.getSofa().
				String htmlText = cas.getDocumentText();
				htmlText = htmlText.replaceAll("[\r]", " ");
				// System.out.println(htmlText);
				// System.out.println(htmlText.length());
				// System.out.println(main.solrWrapper.getDocText(id).length());

				// String docText = Jsoup.parse(htmlText).text();
				// System.out.println(docText);
				Date now = new Date();
				HashMap<String, Object> hshMap = new HashMap<String, Object>();
				hshMap.put("id", id);
				// hshMap.put("htmltext", htmlText);
				hshMap.put("text", htmlText);
				hshMap.put("timestamp", now);
				SolrInputDocument solrDoc = main.makeSolrDocument(hshMap);
				String docXML = ClientUtils.toXML(solrDoc);
				main.indexDocument(docXML);

				// System.out.println(docText);
				if (i % 500 == 0) {
					//main.solrWrapper.getServer().commit();
					// Thread.sleep(1000);
				}
				// HashMap<String,String>param=new HashMap<String,String>();
				// param.put("q", "id:"+id);
				// param.put("fl","text");
				// SolrParams params=new MapSolrParams(param);
				// System.out.println(((String)main.solrWrapper.getServer().query(params).getResults().get(0).getFieldValue("text")).length());
				System.out.println(i + ". indexed with docno: " + id);
			}
			//main.solrWrapper.getServer().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (main.solrWrapper != null) {
				main.solrWrapper.close();
			}
		}
	}

		public void indexDocument(String docXML) {

		String xml = "<add>" + docXML + "</add>";
		// System.out.println(xml);
		DirectXmlRequest xmlreq = new DirectXmlRequest("/update", xml);
		try {
			//solrWrapper.getServer().request(xmlreq);
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
