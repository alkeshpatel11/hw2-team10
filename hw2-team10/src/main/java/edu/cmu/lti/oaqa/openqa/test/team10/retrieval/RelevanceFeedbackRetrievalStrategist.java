package edu.cmu.lti.oaqa.openqa.test.team10.retrieval;

/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.cse.basephase.retrieval.AbstractRetrievalStrategist;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * RS using pseudo relevance feedback
 * 
 * @author Zeyuan Li <zeyuanl@cs.cmu.edu>
 * 
 */
public class RelevanceFeedbackRetrievalStrategist extends SuperRetrievalStrategist {

  
  @Override
  protected List<RetrievalResult> retrieveDocuments(String questionText,
          List<Keyterm> keyterms) {
    String query = formulateQuery(questionText, keyterms);
    return retrieveDocuments(query);
  }
  
  /**
   * formulateQuery: boosting the long phrase and using stemming
   * haven't implemented yet
   * */
  protected String formulateQuery(String questionText, List<Keyterm> keyterms) {
    StringBuffer result = new StringBuffer();
    PorterStemmer stemmer = new PorterStemmer();
    
    for (int i = 0; i < keyterms.size(); i++) {
      Keyterm keyterm = keyterms.get(i);
      String text = keyterm.getText();
      String textstem = stemmer.stem(text);
      double weight = keyterm.getProbability();
      int coef = text.length();
      
      String qs = String.format("(%s OR %s)^%f", text, textstem, coef*weight);
      if(i < keyterms.size() - 1)
        result.append(qs + " AND ");
      else
        result.append(qs);
    }
    
    String query = result.toString();
    System.out.println(" QUERY: " + query);
    return query;
  }
}
