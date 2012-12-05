package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.jsoup.Jsoup;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.SimplePassageExtractor;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class cut the formated docs(not html) into cents using corenlp.
 * Each N three sentences in order will be considered as a candidate passage.
 * Where N is set to be 3 as default.
 * the siteQ score method is used for this class
 * @author Yifei
 */
public class SiteQBioPassageExtractor extends SimplePassageExtractor {
  
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
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);  
    int NumSen = 4;
    for (RetrievalResult document : documents) {
      System.out.println("RetrievalResult: " + document.toString());
      String id = document.getDocID();
      try {
        String htmlText = wrapper.getDocText(id);
        String text=htmlText;
        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);
        
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        int offset = 0;
        if (sentences.size() < NumSen){
          NumSen = sentences.size();
        }
        int iSen = 0;
        int begin=0,end=0;
        List<Integer> begins = new ArrayList<Integer>();
        List<Integer> ends = new ArrayList<Integer>();
        for(CoreMap sentence: sentences) {
          String sent = sentence.toString();
          int start = text.substring(offset).indexOf(sent);
          int ending = start+sent.length();
          
          if(iSen%NumSen==0)
            begin = start+ offset;
          if(iSen%NumSen==NumSen-1){
            end = ending+ offset;
            begins.add(begin);
            ends.add(end);
          }
          offset += ending;
          iSen++;
          iSen = iSen%NumSen;   
          }
        if (offset!= text.length()){
          begins.add(offset);
          ends.add(text.length());
        }
        //now we have all the sentence windows in text
        for(int i=0;i<begins.size();i++){
          begin = begins.get(i);
          end = ends.get(i);
          String text2 = text.substring(begin, end);
          
          //score1 start
          double score1 = 0;
          int matched = 0;
          for ( String keyterm : keys ) {
            Pattern p = Pattern.compile( keyterm );
            Matcher m = p.matcher( text2 );
            if(m.find()){
              score1+=KeyIdf.get(keyterm);
              matched++;
            }
          }
          //score1 end
          if (matched==0)
            continue;
          
        //score 2 start
          String allKey = "";
          for ( String keyterm : keys ){
            allKey = allKey + "("+keyterm+")|";
            }
          if(allKey.length()>0)
              allKey = allKey.substring(0, allKey.length()-1);
          Pattern p = Pattern.compile( allKey );
          Matcher m = p.matcher( text2 );
          int k = 0;        
          double score2 = 0;
          double alpha = 0.5;
          double lastS = 0;
          int lastP = 0;
          if ( m.find() ) {
            double t = KeyIdf.get(m.group(0));
            lastS = t;
            lastP = m.end();k++;
          }
          if ( m.find() ) {
            double t = KeyIdf.get(m.group(0));
            int dist = 1;
            if (lastP<m.start()){
              String gap = text2.substring(lastP, m.start());
              dist = gap.length() - gap.replaceAll(" ", "").length()+1;
            }
            score2 += (lastS + t)/((alpha*dist)*(alpha*dist));
            lastS = t;
            lastP = m.end();k++;
          }
          while ( m.find() ) {
            double t = KeyIdf.get(m.group(0));
            int dist = 1;
            if (lastP<m.start()){
              String gap = text2.substring(lastP, m.start());
              dist = gap.length() - gap.replaceAll(" ", "").length()+1;
            }
            score2 += (lastS + t)/((alpha*dist)*(alpha*dist));
            lastS = t;
            lastP = m.end();k++;
          }
          if (k>1)
            score2 = score2/(k-1);
          //score2 finsihed
          double score = score1 + score2/matched;
          PassageCandidate window = null;
          try {
            window = new PassageCandidate( id , begin , end , (float) score , null );
          } catch (AnalysisEngineProcessException e) {
            e.printStackTrace();
          }
          result.add(window);
          
         }
         
         
      

      } catch (SolrServerException e) {
        e.printStackTrace();
      }
    }
  


    return result;
  }

}
