package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.SimplePassageExtractor;

/**
 * This class returned a list of passage candidates based on bm25 scoring algirthom
 * The candidate passage windows are all spans between two keywords
 * @author Yifei
 *
 */
public class Bm25BioPassageExtractor extends SimplePassageExtractor {
  
  @Override
  protected List<PassageCandidate> extractPassages(String question, List<Keyterm> keyterms,
          List<RetrievalResult> documents) {
    Map<String,Double> KeyIdf = new HashMap<String,Double>();
    List<String> keys = Lists.transform(keyterms, new Function<Keyterm, String>() {
      public String apply(Keyterm keyterm) {
        return keyterm.getText();
      }
    });
    for ( String keyterm : keys ) {
      KeyIdf.put(keyterm, (double) 0);
    }
    for (RetrievalResult document : documents) {
      String id = document.getDocID();
      String htmlText = null;
      try {
        htmlText = wrapper.getDocText(id);
      } catch (SolrServerException e) {
        e.printStackTrace();
      }
      String text = Jsoup.parse(htmlText).text().replaceAll("([\177-\377\0-\32]*)", "")/* .trim() */;
      text = text.substring(0, Math.min(5000, text.length()));
      for ( String keyterm : keys ) {
        Pattern p = Pattern.compile( keyterm );
        Matcher m = p.matcher( text );
        if ( m.find() ) {
          double tmp = KeyIdf.get(keyterm);
          tmp++;
          KeyIdf.put(keyterm, tmp);
        }
      }
    }
    for ( String keyterm : keys ) {
      double tmp = KeyIdf.get(keyterm);
      tmp = Math.log((documents.size()+1)/(tmp+0.5));
      KeyIdf.put(keyterm, (double) tmp);
    }
    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    for (RetrievalResult document : documents) {
      System.out.println("RetrievalResult: " + document.toString());
      String id = document.getDocID();
      try {
        String htmlText = wrapper.getDocText(id);

        String text = Jsoup.parse(htmlText).text().replaceAll("([\177-\377\0-\32]*)", "");
        text = text.substring(0, Math.min(5000, text.length()));
        System.out.println(text);

        Bm25PassageCandidateFinder finder = new Bm25PassageCandidateFinder(id, text,
                new KeytermWindowScorerSum());
        List<String> keytermStrings = Lists.transform(keyterms, new Function<Keyterm, String>() {
          public String apply(Keyterm keyterm) {
            return keyterm.getText();
          }
        });
        List<PassageCandidate> passageSpans = finder.extractPassages(keytermStrings
                .toArray(new String[0]),KeyIdf);
        for (PassageCandidate passageSpan : passageSpans)
          result.add(passageSpan);
      } catch (SolrServerException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

}
