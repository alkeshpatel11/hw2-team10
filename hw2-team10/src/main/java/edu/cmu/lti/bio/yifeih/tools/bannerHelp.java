/* 
 Copyright (c) 2007 Arizona State University, Dept. of Computer Science and Dept. of Biomedical Informatics.
 This file is part of the BANNER Named Entity Recognition System, http://banner.sourceforge.net
 This software is provided under the terms of the Common Public License, version 1.0, as published by http://www.opensource.org.  For further information, see the file 'LICENSE.txt' included with this distribution.
 */

package edu.cmu.lti.bio.yifeih.tools;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.yifeih.tools.banner.*;
import edu.cmu.lti.bio.yifeih.tools.banner.tagging.CRFTagger;
import edu.cmu.lti.bio.yifeih.tools.banner.tokenization.Tokenizer;
/**
 *wrap function of Banner class.
 *input: text to tag.
 *output: array of gene tagged arraylist
 */

public class bannerHelp
{
  String propertiesFilename = "src/main/resources/data/banner.properties";
  static BannerProperties properties;
  static Tokenizer tokenizer;
  static CRFTagger tagger = null;
  public bannerHelp(String text) throws ResourceInitializationException,Exception {
    if(tagger==null){
      System.out.println("inti banner...");
      String modelFilename = text; // model.bin
      properties = BannerProperties.load(propertiesFilename);
      tokenizer = properties.getTokenizer();
      try {
        tagger = CRFTagger.load(new File(modelFilename), properties.getLemmatiser(), properties.getPosTagger());
      } catch (ClassNotFoundException e) {; }
    }
  }
      
  
  /**
     * @param args
     */
    public static Map<Integer, Integer> getgene(String text) throws IOException
    {

        String curSentence =  text;
        int[] spaceBeforeAt = new int[curSentence.length()];
        int spaceBefore = 0;
        for(int i=0;i<curSentence.length();i++){
          spaceBeforeAt[i] = spaceBefore;
          if(curSentence.charAt(i) == ' ')
            spaceBefore++;
        }
        //look for space end
        Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
            
        if (text.length() > 0)
        {
            Sentence sentence = new Sentence(null, text);
            tokenizer.tokenize(sentence);
            tagger.tag(sentence);
            //--------------------------------
            Map tmpMap = sentence.getOffsets();
            Iterator<Entry<Integer, Integer>> it = tmpMap.entrySet().iterator();
            while  (it.hasNext()){    
             
              Map.Entry entry = (Map.Entry) it.next()   ;    
              Integer s = (Integer)entry.getKey();  
              Integer e = (Integer)entry.getValue();
              begin2end.put(s, e);

            }  
        }

        return begin2end;
    }
}
