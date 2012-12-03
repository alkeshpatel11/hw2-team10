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

import java.util.List;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.test.team10.keyterm.SimilarNGramExtractor;

/**
 * RS boosting phrase information
 * 
 * @author Zeyuan Li <zeyuanl@cs.cmu.edu>
 * 
 */
public class PhraseRetrievalStrategist extends SuperRetrievalStrategist {

  @Override
  protected List<RetrievalResult> retrieveDocuments(String questionText, List<Keyterm> keyterms) {
    String query = "";
    try {
      query = formulateQuery(questionText, keyterms);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return retrieveDocuments(query);
  }

  /**
   * formulateQuery: boosting the long phrase and using stemming
   * @throws Exception 
   * 
   * */
  protected String formulateQuery(String questionText, List<Keyterm> keyterms) throws Exception {
    StringBuffer result = new StringBuffer();
    PorterStemmer stemmer = new PorterStemmer();
    SimilarNGramExtractor synextrator = new SimilarNGramExtractor();

    for (int i = 0; i < keyterms.size(); i++) {
      Keyterm keyterm = keyterms.get(i);
      String text = keyterm.getText();
      // add quotation marks for compound phrases
      if (text.matches(".*?\\s.*+"))
        text = "\"" + text + "\"";
      String textstem = stemmer.stem(text);
      double weight = (keyterm.getProbability()-0.0)<1e-6 ? 1.0 : keyterm.getProbability();
      int coef = text.split(" ").length;

      String qs = String.format("(%s OR %s)^%f", text, textstem, coef * weight);
      // append synonym in query string
      List<GeneCount> syns = synextrator.getSynonyms(text);
      result.append("(" + qs);
      for (int j = 0; j < syns.size(); j++) {
        String cursyn = syns.get(j).getGeneName();
        if (cursyn.matches(".*?\\s.*+"))
          cursyn = "\"" + cursyn + "\"";

        result.append(" OR " + cursyn);
      }
      result.append(")"); // TODO: boost by keyterm weight
      
      
      if (i < keyterms.size() - 1)
        result.append(" AND ");
    }

    String query = result.toString();
    System.out.println(" QUERY: " + query);
    return query;
  }
}
