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
import edu.cmu.lti.bio.yifeih.tools.*;
import edu.cmu.lti.bio.yifeih.types.*;
/**
 * input: nounPhrases with confidence.
 * output: detected gene noun phrases for final out.
 * Using Banner trained model to tag qualified sentences. And put final gene tagged phases in to 
 * a new view "FinalNouns" to hold those annotators for the consumer 
 */
public class GeneAnnotatorCRF extends JCasAnnotator_ImplBase {
      static int allcount = 0;
      static int partcount = 0;

  /**
   * @throws AnalysisEngineProcessException 
   * @see JCasAnnotator_ImplBase#process(JCas)
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    
    allcount++;
    Map<Integer, Integer> tmpMap = null;
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
    if (anIter.isValid()){
      annot = (AnnotationFS) anIter.get();
    }
    Feature SENTENCE = cross.getFeatureByBaseName("sentence");
    String docText = annot.getStringValue(SENTENCE);
    
    JCas theView = null;
    try {
      theView =aJCas.getView("allNouns");
    } catch (CASException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    cross = theView.getTypeSystem().getType("edu.cmu.lti.bio.yifeih.types.nounPhrases");
    anIndex = theView.getAnnotationIndex(cross);
    anIter = anIndex.iterator();
    Feature CONFIDENCE = cross.getFeatureByBaseName("confidence");
    Double score = 0.0;
    int count = 0;
    while (anIter.isValid()) {
      annot = (AnnotationFS) anIter.get();
      //System.out.println(" " + annot.getType().getName() + ": " );
      score += annot.getDoubleValue(CONFIDENCE);
      count++;
      anIter.moveToNext();
    }
    if(count!=0)
      score = score/count;
    JCas jcasnouns;
    try {
      jcasnouns = aJCas.createView("FinalNouns");
    } catch (CASException e) {
      throw new AnalysisEngineProcessException(e);
    }
    if(score>0.5){
        partcount++;
        bannerHelp myBanner = null;
        try {
          myBanner = new bannerHelp((String)getContext().getConfigParameterValue("bannerPath"));
        } catch (ResourceInitializationException e) {
          // TODO Auto-generated catch block
          throw  new AnalysisEngineProcessException(e);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          throw  new AnalysisEngineProcessException(e);
        }
    
        // get document text
        

        try {
          tmpMap = myBanner.getgene(docText);
        } catch (IOException e) {
          throw  new AnalysisEngineProcessException(e);
        } 
        Iterator<Entry<Integer, Integer>> it = tmpMap.entrySet().iterator();
        
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

        //look for space end
        while(it.hasNext()){    
          nouns = new nounPhrases(jcasnouns);
          Map.Entry entry = (Map.Entry) it.next();    
          Integer start = (Integer)entry.getKey();  
          Integer end = (Integer)entry.getValue();
          nouns.setNouns(docText.substring(start, end));
          nouns.setBegin(start-spaceBeforeAt[start]);
          nouns.setEnd(end-1-spaceBeforeAt[end-1]);
          nouns.addToIndexes(); 
        }  
      }
    System.out.println(partcount+"/"+allcount);
    }
  }



