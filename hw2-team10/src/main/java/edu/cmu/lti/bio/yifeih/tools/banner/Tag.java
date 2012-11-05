/* 
 Copyright (c) 2007 Arizona State University, Dept. of Computer Science and Dept. of Biomedical Informatics.
 This file is part of the BANNER Named Entity Recognition System, http://banner.sourceforge.net
 This software is provided under the terms of the Common Public License, version 1.0, as published by http://www.opensource.org.  For further information, see the file 'LICENSE.txt' included with this distribution.
 */

package edu.cmu.lti.bio.yifeih.tools.banner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.cmu.lti.bio.yifeih.tools.banner.processing.PostProcessor;
import edu.cmu.lti.bio.yifeih.tools.banner.tagging.CRFTagger;
import edu.cmu.lti.bio.yifeih.tools.banner.tagging.TaggedToken;
import edu.cmu.lti.bio.yifeih.tools.banner.tagging.TaggedToken.TagFormat;
import edu.cmu.lti.bio.yifeih.tools.banner.tagging.TaggedToken.TagPosition;
import edu.cmu.lti.bio.yifeih.tools.banner.tokenization.Tokenizer;


public class Tag
{

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException
    {
        String propertiesFilename = args[0]; // banner.properties
        String modelFilename = args[1]; // model.bin
        String inputFilename = args[2];
        String outputFilename = args[3];

        // Get the properties and create the tagger
        BannerProperties properties = BannerProperties.load(propertiesFilename);
        Tokenizer tokenizer = properties.getTokenizer();
        CRFTagger tagger = null;
        try {
          tagger = CRFTagger.load(new File(modelFilename), properties.getLemmatiser(), properties.getPosTagger());
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          //e.printStackTrace();
        }
        PostProcessor postProcessor = properties.getPostProcessor();
        System.out.println("done");
        // Get the input text
        BufferedReader inputReader = new BufferedReader(new FileReader(inputFilename));
        String text = "";
        String sentenceText = null;
        /*while (line != null)
        {
            text += line.trim() + " ";
            line = inputReader.readLine();
        }
        inputReader.close();*/

        // Break the input into sentences, tag and write to the output file
        PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
        BreakIterator breaker = BreakIterator.getSentenceInstance();
        breaker.setText(text);
        int start = breaker.first();
        int count = 0;
        //for (int end = breaker.next(); end != BreakIterator.DONE; start = end, end = breaker.next())
        do
        {   
            sentenceText = inputReader.readLine();
            System.out.println(count++);
            if (sentenceText.charAt(sentenceText.length()-1) != '.')
                sentenceText = sentenceText + '.';
            //sentenceText = text.substring(start, end).trim();
            //look for space
            
            
              String curSentence =  sentenceText.substring(15, sentenceText.length());
            int[] spaceBeforeAt = new int[curSentence.length()];
            int spaceBefore = 0;
            for(int i=0;i<curSentence.length();i++){
              spaceBeforeAt[i] = spaceBefore;
              if(curSentence.charAt(i) == ' ')
                spaceBefore++;
            }
            //look for space end
            
            
            
            
            if (sentenceText.length() > 0)
            {
                Sentence sentence = new Sentence(null, sentenceText);
                tokenizer.tokenize(sentence);
                tagger.tag(sentence);
                //--------------------------------
                Map tmpMap = sentence.getOffsets();
                Iterator<Entry<Integer, Integer>> it = tmpMap.entrySet().iterator();
                while  (it.hasNext()){    
                 
                  Map.Entry entry = (Map.Entry) it.next()   ;    
                  Integer s = (Integer)entry.getKey();// + matcher.start(2);    
                  Integer e = (Integer)entry.getValue();// + matcher.start(2);
                  //System.out.println(sentenceText.substring(s, e));
                  //System.out.println(e-15-1+"~~~~");
                  Integer s_,e_;
                  if(s!=0){
                    s_ = s - 15;
                    e_ = e - 16;
                    s_ = s_ - spaceBeforeAt[s_];
                    e_ = e_ - spaceBeforeAt[e_];
                    outputWriter.println(sentenceText.substring(0, 14)+"|"+s_+" "+e_+"|"+sentenceText.substring(s, e));
                  }
                  
                  //System.out.println(s+" ~ "+e);
                  //System.out.println(s+" "+e);
                }  
                /*if (postProcessor != null)
                    postProcessor.postProcess(sentence);
                outputWriter.println(sentence.getSGML());
                System.out.println(sentence.getSGML());*/
 
                //----------------------------------
            }
        }while(sentenceText!=null);
        inputReader.close();
        outputWriter.close();
    }
}
