package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.util.Version;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorer;
import edu.cmu.lti.oaqa.openqa.hello.passage.PassageCandidateFinder;
import edu.cmu.lti.oaqa.openqa.test.team10.passage.PassageCandidateFinder.PassageSpan;

public class LuceneBioPassageCandidateFinder {

	private String text;
	private String docId;

	private int textSize; // values for the entire text
	private double totalMatches;
	private double totalKeyterms;

	// private KeytermWindowScorer scorer;

	public LuceneBioPassageCandidateFinder(String docId, String text) {// ,
		// KeytermWindowScorer scorer) {

		this.text = text;
		this.docId = docId;
		this.textSize = text.length();
		// this.scorer = scorer;
	}

	@SuppressWarnings("deprecation")
	public List<PassageCandidate> extractPassages(String question,List<String>keyterms,Set<String> stopWords) {// ,
		// Map<String, Double> idf) {
		// List<List<PassageSpan>> matchingSpans = new
		// ArrayList<List<PassageSpan>>();
		// List<PassageSpan> matchedSpans = new ArrayList<PassageSpan>();
		List<PassageCandidate> result = new ArrayList<PassageCandidate>();

		try {
			List<String> keys = new ArrayList<String>();
			// Find all keyterm matches.
			BooleanQuery totalQuery=new BooleanQuery();
			//String keyterms[]=question.split("[\\W]");
			for(int k=0;k<keyterms.size();k++){
				if(stopWords.contains(keyterms.get(k).toLowerCase())){
					continue;
				}
				TermQuery query = new TermQuery(new Term("text", keyterms.get(k)));
				System.out.println(keyterms.get(k));
				totalQuery.add(query,BooleanClause.Occur.SHOULD);
			}
			// HTMLStripCharFilterFactory htmlFilter=new
			// HTMLStripCharFilterFactory();
			//Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_36);
			//TokenStream tokenStream = analyzer.tokenStream("text",new StringReader(text));	
			TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_36,new StringReader(text));
			tokenStream=new LowerCaseFilter(Version.LUCENE_36,tokenStream);
			tokenStream=new StopFilter(Version.LUCENE_36,tokenStream,stopWords,true);
			QueryScorer scorer = new QueryScorer(totalQuery, "text");
			Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
			Highlighter highlighter = new Highlighter(scorer);
			highlighter.setTextFragmenter(fragmenter);
			TextFragment textFrags[] = highlighter.getBestTextFragments(tokenStream, text,
					true, 10);// estFragment(tokenStream,"text",
								// question,10);

			// ///////////////////////////

			for (int j = 0; j < textFrags.length; j++) {
				TextFragment textFrag = textFrags[j];
				System.out.println("########################################################");
				System.out.println(textFrag.getScore()+"\t"+textFrag.getTextStartPos()+"\t"+textFrag.getTextEndPos()+"\t"+text.substring(textFrag.getTextStartPos(), textFrag.getTextEndPos()));
				PassageCandidate passageCandidate = new PassageCandidate(docId,
						textFrag.getTextStartPos(), textFrag.getTextEndPos(),
						textFrag.getScore(), question);
				result.add(passageCandidate);

			}

		} catch (Exception e) {
			e.printStackTrace();
			// continue;
		}

		// Sort the result in order of decreasing score.
		// Collections.sort ( result , new PassageCandidateComparator() );
		return result;

	}

	private class PassageCandidateComparator implements Comparator {
		// Ranks by score, decreasing.
		public int compare(Object o1, Object o2) {
			PassageCandidate s1 = (PassageCandidate) o1;
			PassageCandidate s2 = (PassageCandidate) o2;
			if (s1.getProbability() < s2.getProbability()) {
				return 1;
			} else if (s1.getProbability() > s2.getProbability()) {
				return -1;
			}
			return 0;
		}
	}

	class PassageSpan {
		private int begin, end;

		public PassageSpan(int begin, int end) {
			this.begin = begin;
			this.end = end;
		}

		public boolean containedIn(int begin, int end) {
			if (begin <= this.begin && end >= this.end) {
				return true;
			} else {
				return false;
			}
		}
	}

	/*
	public static void main(String[] args) {
		LuceneBioPassageCandidateFinder passageFinder1 = new LuceneBioPassageCandidateFinder("1",
				"The quick brown fox jumped over the quick brown fox.");
		LuceneBioPassageCandidateFinder passageFinder2 = new LuceneBioPassageCandidateFinder("1",
				"The quick brown fox jumped over the quick brown fox.");
		String[] keyterms = { "quick", "jumped" };
		List<PassageCandidate> windows1 = passageFinder1
				.extractPassages("quick jumped");
		System.out.println("Windows (product scoring): " + windows1);
		List<PassageCandidate> windows2 = passageFinder2
				.extractPassages("quick");
		System.out.println("Windows (sum scoring): " + windows2);
	}
*/
}
