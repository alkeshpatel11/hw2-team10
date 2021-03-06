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
package edu.cmu.lti.oaqa.openqa.test.team10.keyterm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.oaqa.bio.resource_wrapper.obo.OBOGraph;
import edu.cmu.lti.oaqa.bio.resource_wrapper.obo.OBONode;
import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

/**
 * 
 * @author Bharat Dutt <bdutt@andrew.cmu.edu>
 * 
 */
public class GOSynonymExtracter extends AbstractKeytermExtractor {
  
  
  private int MAX_SIZE;
  private String GoFilePath;

  @Override
  protected List<Keyterm> getKeyterms(String question) {
    
    question = question.replace('?', ' ');
    question = question.replace('(', ' ');
    question = question.replace('[', ' ');
    question = question.replace(')', ' ');
    question = question.replace(']', ' ');
    question = question.replace('/', ' ');
    question = question.replace('\'', ' ');
    
    String[] questionTokens = question.split("\\s+");
    List<Keyterm> keyterms = new ArrayList<Keyterm>();    
   
    //Simply adding question terms to the list of keyterms
    for(int i=0; i<questionTokens.length;i++)
    {
        keyterms.add(new Keyterm(questionTokens[i]));
    }
  
    
    ArrayList<String> SynFromGO = findBestRelatedGenesFromGO(questionTokens);    
    
    if(SynFromGO.size()>0)
    {
      for(int i=0; i< MAX_SIZE;i++)
      {
         keyterms.add(new Keyterm(SynFromGO.get(i)));
         System.out.println(SynFromGO.get(i));
      }
    }
    
    
    return keyterms;
  }

  
 @Override
public void initialize(UimaContext c) throws ResourceInitializationException {
  
  super.initialize(c);
  MAX_SIZE = (Integer)c.getConfigParameterValue("max_size");  
  GoFilePath = (String) c.getConfigParameterValue("goFilePath");
}
  
  private ArrayList<String> findBestRelatedGenesFromGO(String[] genes)
  {  
  ArrayList<String> geneListToSend = new ArrayList<String>();
  try {
    OBOGraph graph = new OBOGraph(new FileReader(new File(GoFilePath)));
    //System.out.println(graph.toString());
    //System.out.println("Genes array size = " + genes.size());    
    for(int i=0; i<genes.length; i++)
    {
      
      ArrayList<OBONode> oboNodes = graph.search(genes[i]);
      for(OBONode o: oboNodes)
      {
               
        for(String syn: o.getAllSynonyms())
        {          
          geneListToSend.add(syn);
          
        }
      }
    }
  } catch (FileNotFoundException e) {
    
    e.printStackTrace();
  }
  
  return geneListToSend;
  }
  
}
