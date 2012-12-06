package edu.cmu.lti.bio.alkesh.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import java.util.Iterator;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.util.Iterator;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;

public class LanguageModeling {

	String XMI_REPOSITORY = "C:/Users/alkesh/Downloads/Trec06_annotated_xmi/";
	String TYPE_DESC_XML = "src/main/resources/edu/cmu/lti/oaqa/bio/model/bioTypes.xml";
	String SOLR_SERVER_URL = "http://localhost:8983/solr/genomics-legalspan/";

	SolrWrapper solrWrapper = null;

	public LanguageModeling() {
		try {
			solrWrapper = new SolrWrapper(SOLR_SERVER_URL, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		try {
			LanguageModeling main = new LanguageModeling();
			//main.findSimilarity("What is the role of PrnP in mad cow disease",
					//"");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// if (solrWrapper != null) { solrWrapper.close(); }

		}

	}

	
	
	public void indexPassages() throws Exception {

		File files[] = new File(XMI_REPOSITORY).listFiles();

		//IndexingUtils indexUtils=new IndexingUtils(solrWrapper.getServer());
		XMLInputSource input = new XMLInputSource(TYPE_DESC_XML);
		TypeSystemDescription typeDesc = UIMAFramework.getXMLParser()
				.parseTypeSystemDescription(input);

		CAS cas = CasCreationUtils.createCas(typeDesc, null,
				new FsIndexDescription[0]);

		for (int i = 0; i < files.length; i++) {

			if (i > 1) {
				break;
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
			String htmlText = cas.getDocumentText();

			Elements paraElements = Jsoup.parse(htmlText, "").select("P");
			ArrayList<String> paragraphs = new ArrayList<String>();
			for (int k = 0; k < paraElements.size(); k++) {
				String paragraph = paraElements.get(k).text();
				// System.out.println(paragraph);
				// System.out.println("===================================");
				paragraphs.add(paragraph);

			}

			String docText = Jsoup.parse(htmlText).text();
			// System.out.println("Total text: "+docText.length());
			// System.out.println("Para text: "+docText.length());
			// System.out.println(docText);
			Date now = new Date();
			HashMap<String, Object> hshMap = new HashMap<String, Object>();
			hshMap.put("id", id);
			// hshMap.put("html text", htmlText);
			hshMap.put("text", docText);
			hshMap.put("paragraph", paragraphs);
			hshMap.put("timestamp", now);
			//SolrInputDocument solrDoc = indexUtils.makeSolrDocument(hshMap);
			//String docXML = ClientUtils.toXML(solrDoc);
			//indexUtils.indexDocument(docXML);
			// System.out.println(docText);
			// if (i % 50 == 0) {
			//solrWrapper.getServer().commit();
			// Thread.sleep(1000);
			// }

			System.out.println(i + ". indexed with docno: " + id);

		}

	}
/*
	public double findSimilarity1(String q, String p) throws Exception {

		double similarity = 0.0;
		HashMap<String, String> hshMap = new HashMap<String, String>();
		hshMap.put("q", "id:10085337");
		//qt=tvrh&tv.tf=true&tv.fl=contents&tv.all=true
		hshMap.put("qt", "tvrh");
		hshMap.put("tv.tf", "true");
		hshMap.put("tv.fl", "paragraph,text");
		hshMap.put("tv.all", "true");
		hshMap.put("fl", "paragraph,text");
		SolrParams solrParams = new MapSolrParams(hshMap);
		QueryResponse queryResponse = solrWrapper.getServer().query(solrParams);
		TermsResponse termsResponse = queryResponse.getTermsResponse();
		List<Term> terms = termsResponse.getTerms("text");
		for (int i = 0; i < terms.size(); i++) {
			System.out.println(terms.get(i).getTerm() + "\t"
					+ terms.get(i).getFrequency());
		}


		return similarity;
	}

	
	public double findSimilarity(String q, String p) throws Exception {

		double similarity = 0.0;
		HashMap<String, String> hshMap = new HashMap<String, String>();
		hshMap.put("q", "id:10085337");
		//qt=tvrh&tv.tf=true&tv.fl=contents&tv.all=true
		hshMap.put("qt", "tvrh");
		hshMap.put("tv.tf", "true");
		hshMap.put("tv.fl", "paragraph,text");
		hshMap.put("tv.all", "true");
		hshMap.put("fl", "paragraph,text");
		SolrParams solrParams = new MapSolrParams(hshMap);
		QueryResponse queryResponse = solrWrapper.getServer().query(solrParams);
		TermsResponse termsResponse = queryResponse.getTermsResponse();
		List<Term> terms = termsResponse.getTerms("text");
		for (int i = 0; i < terms.size(); i++) {
			System.out.println(terms.get(i).getTerm() + "\t"
					+ terms.get(i).getFrequency());
		}


		return similarity;
	}
*/
}
