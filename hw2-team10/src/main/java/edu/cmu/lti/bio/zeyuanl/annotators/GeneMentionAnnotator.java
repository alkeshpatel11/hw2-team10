package edu.cmu.lti.bio.zeyuanl.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.zeyuanl.types.GeneMention;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * This method is not used in the cpe engine!<br>
 * A gene mention annotator which uses the naive POS heuristic method. <br>
 * 
 */
public class GeneMentionAnnotator extends JCasAnnotator_ImplBase {

  private StanfordCoreNLP pipeline;

  public GeneMentionAnnotator() throws ResourceInitializationException {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    pipeline = new StanfordCoreNLP(props);
  }

  /**
   * This method uses naive POS heuristic to tag testing sentence and output their span.
   *  
   * @param text the testing sentence
   * @param tokenArr temporary token array
   * @return Map<Integer, Integer> the gene mention span pairs
   */
  public Map<Integer, Integer> getGeneSpans(String text, List<String> tokenArr) {
    Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    for (CoreMap sentence : sentences) {
      List<CoreLabel> candidate = new ArrayList<CoreLabel>();
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        // save tokens
        tokenArr.add(token.value());
        
        String pos = token.get(PartOfSpeechAnnotation.class);
        if (pos.startsWith("NN")) {
          candidate.add(token);
        } else if (candidate.size() > 0) {
          int begin = candidate.get(0).beginPosition();
          int end = candidate.get(candidate.size() - 1).endPosition();
          begin2end.put(begin, end);
          candidate.clear();
        }
      }
      if (candidate.size() > 0) {
        int begin = candidate.get(0).beginPosition();
        int end = candidate.get(candidate.size() - 1).endPosition();
        begin2end.put(begin, end);
        candidate.clear();
      }
    }
    return begin2end;
  }

  /**
   * @see JCasAnnotator_ImplBase#process(JCas)
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    String text = aJCas.getDocumentText();
    String[] textLine = text.split("\n");

    for (int i = 0; i < textLine.length; i++) {
      String line = textLine[i];
      String sentid = line.substring(0, line.indexOf(' '));
      String sent = line.substring(line.indexOf(' ') + 1);
      List<String> tokenArr = new ArrayList<String>();
      Map<Integer, Integer> gslineMap = getGeneSpans(sent, tokenArr);
      StringArray sa = new StringArray(aJCas, tokenArr.size());
      String[] tokens = new String[tokenArr.size()];
      for(int i1 = 0; i1 < tokenArr.size(); i1++)
        tokens[i1] = tokenArr.get(i1);
      sa.copyFromArray(tokens, 0, 0, tokenArr.size());
      
      Set<Map.Entry<Integer, Integer>> entries = gslineMap.entrySet();
      for (Map.Entry<Integer, Integer> entry : entries) {
        GeneMention gmAnnot = new GeneMention(aJCas);
        gmAnnot.setBegin(getIdx(sent, entry.getKey()));
        gmAnnot.setEnd(getIdx(sent, entry.getValue() - 1));
        gmAnnot.setId(sentid);
        gmAnnot.setText(sent);
        gmAnnot.setTag(sent.substring(entry.getKey(), entry.getValue()));
        gmAnnot.setTokenArr(sa);
        gmAnnot.addToIndexes();
      }
    }
  }
  
  int getIdx(String sent, int end) {
    int idx = 0;
    for(int i = 0; i < end; i++)
      if(sent.charAt(i) != ' ')
        idx++;
    return idx;
  }
}
