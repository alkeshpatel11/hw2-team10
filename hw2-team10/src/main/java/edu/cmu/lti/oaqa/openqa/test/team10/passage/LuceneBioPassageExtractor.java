package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.HTMLStripCharFilterFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.Jsoup;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorerSum;
import edu.cmu.lti.oaqa.openqa.hello.passage.PassageCandidateFinder;
import edu.cmu.lti.oaqa.openqa.hello.passage.SimplePassageExtractor;

public class LuceneBioPassageExtractor extends SimplePassageExtractor {

	Set<String>stopWords=new HashSet<String>();
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		String stopFile = (String) aContext
				.getConfigParameterValue("stopword-file");//data/stopwords.txt
		try{
			loadStopWords(stopFile);
		}catch(Exception e){
			throw new ResourceInitializationException();
		}
		
	}

	private void loadStopWords(String stopFile) throws Exception {
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new FileReader(stopFile));
			String text="";
			char chars[]=new char[2048];
			while((bfr.read(chars))!=-1){
				text+=new String(chars);
				chars=null;
				chars=new char[2048];
			}
			bfr.close();
			bfr=null;
			text=text.trim();
			
			String words[]=text.split("[\n]");
			for(int i=0;i<words.length;i++){
				stopWords.add(words[i]);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceInitializationException();
		} finally {
			if(bfr!=null){
				bfr.close();
				bfr=null;
			}
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
				String htmlText = wrapper.getDocText(id);

				// cleaning HTML text
				// String text = Jsoup.parse(htmlText).text()
				// .replaceAll("([\177-\377\0-\32]*)", "")/* .trim() */;
				// for now, making sure the text isn't too long
				// text = text.substring(0, Math.min(5000, text.length()));
				String text = htmlText;
				// System.out.println(text);

				LuceneBioPassageCandidateFinder finder = new LuceneBioPassageCandidateFinder(
						id, text);
				List<String> keytermStrings = Lists.transform(keyterms,
						new Function<Keyterm, String>() {
							public String apply(Keyterm keyterm) {
								return keyterm.getText();
							}
						});
				List<PassageCandidate> passageSpans = finder.extractPassages(
						question, keytermStrings,stopWords);
				for (PassageCandidate passageSpan : passageSpans)
					result.add(passageSpan);

			} catch (SolrServerException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
