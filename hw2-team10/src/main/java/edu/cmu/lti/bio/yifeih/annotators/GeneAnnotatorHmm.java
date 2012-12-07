package edu.cmu.lti.bio.yifeih.annotators;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import edu.cmu.lti.bio.yifeih.types.*;
import edu.cmu.lti.bio.yifeih.tools.*;
/**

 * This model will use Lingpipe hmm model to give confidence to phrases in the sentence
 * If the confidence is higher than some threhold, it will be added into annotation lists
 *
 */
public class GeneAnnotatorHmm extends JCasAnnotator_ImplBase {
  //private Pattern mYorktownPattern = Pattern.compile("[P](\\d)*\\s");
  //private Pattern oneLine = Pattern.compile("([^ ]*)[ ](.*)");//add[\n] at the end when single
  /**
   * @throws AnalysisEngineProcessException 
   * @see JCasAnnotator_ImplBase#process(JCas)
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
      Map<Integer, Double> tmpMap = null;
      JCas lineView = null;
      try {
        lineView =aJCas.getView("freshLine");
      } catch (CASException e1) {
        e1.printStackTrace();
      }
    
      Type cross = lineView.getTypeSystem().getType("edu.cmu.lti.bio.yifeih.types.line");
      FSIndex anIndex = lineView.getAnnotationIndex(cross);
      FSIterator anIter = anIndex.iterator();
      AnnotationFS annot = null;
      if (anIter.isValid()) {
        annot = (AnnotationFS) anIter.get();
      }
      Feature ID = cross.getFeatureByBaseName("id");
      Feature SENTENCE = cross.getFeatureByBaseName("sentence");

      hmmConfidentChunk myChunk;;
      try {
        myChunk = new hmmConfidentChunk((String)getContext().getConfigParameterValue("hmmPath"));
      } catch (ResourceInitializationException e) {
        throw  new AnalysisEngineProcessException(e);
      } catch (Exception e) {
        throw  new AnalysisEngineProcessException(e);
      }

    // get document text
      String docText = annot.getStringValue(SENTENCE);;

      try {
        tmpMap = myChunk.getgene(docText);
      } catch (IOException e) {
        throw  new AnalysisEngineProcessException(e);
      } 
      Iterator<Entry<Integer, Double>> it = tmpMap.entrySet().iterator();
      
      //look for space
      String curSentence = docText;
      int[] spaceBeforeAt = new int[curSentence.length()+1];
      int spaceBefore = 0;
      for(int i=0;i<curSentence.length();i++){
        spaceBeforeAt[i] = spaceBefore;
        if(curSentence.charAt(i) == ' ')
          spaceBefore++;
      }
       nounPhrases nouns;
       JCas jcasnouns;
       try {
         jcasnouns = aJCas.createView("allNouns");
       } catch (CASException e) {
         throw new AnalysisEngineProcessException(e);
       }
      //look for space end
      while   (it.hasNext()){    
        nouns = new nounPhrases(jcasnouns);
        Map.Entry entry = (Map.Entry) it.next()   ;    
        Integer start = (Integer)entry.getKey();// + matcher.start(2);    
        Integer end = (int) Math.floor((Double)entry.getValue());// + matcher.start(2);
        Double conf = (Double)entry.getValue() - end;
        if(conf < 0.000000001 ){
          conf = 1.0;end = end -1;
        }
        nouns.setNouns(docText.substring(start, end));
        nouns.setBegin(start-spaceBeforeAt[start]);
        nouns.setEnd(end-1-spaceBeforeAt[end-1]);
        nouns.setConfidence(conf);
        nouns.addToIndexes(); 
      }  
    }
  }



