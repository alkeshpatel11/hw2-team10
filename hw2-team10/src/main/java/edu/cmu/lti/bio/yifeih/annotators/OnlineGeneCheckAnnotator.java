package edu.cmu.lti.bio.yifeih.annotators;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.bio.yifeih.tools.*;
/**
 * input: nounPhrases with confidence.
 * output: nounPhrases with rescored confidence
 * This model will check the confidence of candidate gene nounPhrases from "allNouns" view
 * If the confidence is lower than <code>lower</code> then we will recheck it onilone
 * and give it a new confidence.(for now the new confidence will be the threhold in next component )
 */
public class OnlineGeneCheckAnnotator extends JCasAnnotator_ImplBase {
  //private Pattern mYorktownPattern = Pattern.compile("[P](\\d)*\\s");
  private Pattern oneLine = Pattern.compile("([^ ]*)[ ](.*)");//add[\n] at the end when single

  /**
   * @throws AnalysisEngineProcessException 
   * @see JCasAnnotator_ImplBase#process(JCas)
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    webCheck checker = new webCheck();
    JCas theView = null;
    Type cross;
    FSIndex anIndex;
    FSIterator anIter;
    AnnotationFS annot;
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
    Feature NOUNS = cross.getFeatureByBaseName("nouns");
    Feature BEGIN = cross.getFeatureByBaseName("begin");
    Feature END = cross.getFeatureByBaseName("end");
    String nouns = null;
    Double conf;
    Double lower = 0.5;
    int begin = 0,end = 0;
    while (anIter.isValid()) {
      annot = (AnnotationFS) anIter.get();
      //System.out.println(" " + annot.getType().getName() + ": " );
      nouns = annot.getFeatureValueAsString(NOUNS);
      begin = annot.getIntValue(BEGIN);
      end = annot.getIntValue(END);
      conf = annot.getDoubleValue(CONFIDENCE);
      if(conf<lower){
        System.out.println(conf);
        try {
          if(checker.check(nouns));
           annot.setDoubleValue(CONFIDENCE, lower);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          System.out.println("wrong");
        }
      }
      anIter.moveToNext();
    }


    }
  }



