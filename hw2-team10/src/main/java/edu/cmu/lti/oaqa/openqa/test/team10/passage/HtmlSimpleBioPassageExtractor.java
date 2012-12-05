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
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorerSum;
import edu.cmu.lti.oaqa.openqa.hello.passage.SimplePassageExtractor;
import edu.cmu.lti.oaqa.openqa.hello.passage.PassageCandidateFinder;
//import edu.cmu.lti.oaqa.openqa.test.team10.passage.PassageCandidateFinder.PassageSpan;
/**
 * This class cut the html doc into possible paragraph chunks using a naive method.
 * And do the passage retrieval separately.
 * @author Yifei
 *
 */
public class HtmlSimpleBioPassageExtractor extends SimplePassageExtractor {

  @Override
  protected List<PassageCandidate> extractPassages(String question, List<Keyterm> keyterms,
          List<RetrievalResult> documents) {
    
   
    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    for (RetrievalResult document : documents) {
      System.out.println("RetrievalResult: " + document.toString());
      String id = document.getDocID();
      double denThres = 0.6;
      try {
        String htmlText = wrapper.getDocText(id);
        List<Integer> ParaStarts = new ArrayList<Integer>();
        List<Integer> ParaEnds = new ArrayList<Integer>();
        Pattern p = Pattern.compile( ".*" );
        Matcher m = p.matcher( htmlText );
        while ( m.find() ) {
          String tt = m.group();
          if (m.group().length()==0)
            continue;
          int tmp = 0;
          int useless = 0;
          boolean inTag = false;
          int stage = 0;
          for(char c :m.group().toCharArray()){
            if (c=='<'){
              inTag = !inTag;stage++;
            }
            if (inTag)
              tmp++;
            if (c=='>'){
              useless+=tmp;
              tmp = 0;
              inTag = !inTag;stage++;
            }
          }
          
          double score = 1.0*(1.0*m.group().length()-useless)/m.group().length();
          if(!inTag&&score>denThres){
            ParaStarts.add(m.start());
            ParaEnds.add(m.end());  
          }
          
        }
        List<String> all = new ArrayList<String>();
        for(int i=0;i<ParaStarts.size();i++){
          all.add(htmlText.substring(ParaStarts.get(i), ParaEnds.get(i)));
          
        }
        int a = 2;

        for(int i=0;i<ParaStarts.size();i++){
          String text = htmlText.substring(ParaStarts.get(i), ParaEnds.get(i));
          PassageCandidateFinder finder = new PassageCandidateFinder(id, text,
                  new KeytermWindowScorerSum());
          List<String> keytermStrings = Lists.transform(keyterms, new Function<Keyterm, String>() {
            public String apply(Keyterm keyterm) {
              return keyterm.getText();
            }
          });
          List<PassageCandidate> passageSpans = finder.extractPassages(keytermStrings
                  .toArray(new String[0]));
          
          for (PassageCandidate passageSpan : passageSpans){
            int start = passageSpan.getStart();
            int end = passageSpan.getEnd();
            start += ParaStarts.get(i);
            end += ParaStarts.get(i);
            passageSpan.setStart(start);
            passageSpan.setEnd(end);
            result.add(passageSpan);
          }
          
        }


      } catch (SolrServerException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

}
