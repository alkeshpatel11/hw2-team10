/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.cmu.lti.bio.zeyuanl.casconsumers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.bio.zeyuanl.types.GeneMention;


/**
 * An example of CAS Consumer. <br>
 * AnnotationPrinter prints to an output file all annotations in the CAS. <br>
 * Parameters needed by the AnnotationPrinter are
 * <ol>
 * <li>"outputFile" : file to which the output files should be written.</li>
 * </ol>
 * <br>
 * These parameters are set in the initialize method to the values specified in the descriptor file. <br>
 * These may also be set by the application by using the setConfigParameterValue methods.
 * 
 * 
 */

public class TrainFileWriter extends CasConsumer_ImplBase implements CasObjectProcessor {
  File outFile;

  FileWriter trainWriter, testWriter;

  private String mGoldFile;
  private int mTrainNum;
  private HashMap<String, String> mgoldMap = null;

  public TrainFileWriter() {
  }

  /**
   * Initializes this CAS Consumer with the parameters specified in the descriptor.
   * 
   * @throws ResourceInitializationException
   *           if there is error in initializing the resources
   */
  public void initialize() throws ResourceInitializationException {

    // extract configuration parameter settings
    String otrain = (String) getUimaContext().getConfigParameterValue("outTrainFile");
    String otest = (String) getUimaContext().getConfigParameterValue("outTestFile");
    mGoldFile = (String) getUimaContext().getConfigParameterValue("goldFile");
    mTrainNum = (Integer) getUimaContext().getConfigParameterValue("trainNum");
    
    // Output file should be specified in the descriptor
    if (otrain == null) {
      throw new ResourceInitializationException(
              ResourceInitializationException.CONFIG_SETTING_ABSENT, new Object[] { "outputFile" });
    }
    // TODO: try branching
    // If specified output directory does not exist, try to create it
    outFile = new File(otrain.trim());
    if (outFile.getParentFile() != null && !outFile.getParentFile().exists()) {
      if (!outFile.getParentFile().mkdirs())
        throw new ResourceInitializationException(
                ResourceInitializationException.RESOURCE_DATA_NOT_VALID, new Object[] { otrain,
                    "outputFile" });
    }
    try {
      trainWriter = new FileWriter(outFile);
      testWriter = new FileWriter(new File(otest));
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    
    // Read golden standard
    if(mgoldMap == null)
      mgoldMap = readGoldGM();
  }

  /**
   * Processes the CasContainer which was populated by the TextAnalysisEngines. <br>
   * In this case, the CAS index is iterated over selected annotations and printed out into an
   * output file
   * 
   * @param aCAS
   *          CasContainer which has been populated by the TAEs
   * 
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * 
   * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(CAS)
   */
  public synchronized void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    // iterate and print annotations
    // FSIndex gmIndex = jcas.getAnnotationIndex(GeneMention.type);
    FSIndex gmIndex = jcas.getIndexRepository().getIndex("edu.lti.cmu.hw1.idx.gmasc");
    Iterator annotationIter = gmIndex.iterator();
    int num = 0;
    String preid = "";
    FileWriter writer = trainWriter;

    while (annotationIter.hasNext()) {
      GeneMention annot = (GeneMention) annotationIter.next();
      // output only once per id
      if(preid.equals(""))
        preid = annot.getId();
      else if(preid.equals(annot.getId()))
        continue;
      else
        preid = annot.getId();
      
      // some be training samples, some test
      if(num++ == mTrainNum)
        writer = testWriter;
      
      String tag = mgoldMap.get(annot.getId());
      String[] tags = null;

      if (tag != null && !tag.isEmpty())
        tags = tag.split("\t\t");

      try {
        String[] texts = annot.getTokenArr().toArray();
        if (tags == null) {
          for (int i = 0; i < texts.length; i++)
            writer.write(texts[i] + "\t" + "O\n");
        } else {
          int preii = -1;
          for (int i = 0; i < texts.length; i++) {
            boolean hastag = false;
            
            for(int ii = preii+1; ii < tags.length; ii++) {
              String[] onetags = tags[ii].split(" ");
              
              if (texts[i].equals(onetags[0])) {
                preii = ii;
                hastag = true;
                writer.write(texts[i] + "\t" + "B_GM\n");
                for (int j = 1; j < onetags.length; j++) {
                  //System.out.println("onetags:" + onetags[j]);
                  //System.out.println("texts:" + texts[i+1]);
                  writer.write(texts[++i] + "\t" + "I_GM\n");
                }
              } 
            }
            if(!hastag)
              writer.write(texts[i] + "\t" + "O\n");
          }
        }

        // separate instances by "^$"
        writer.write("\n");
        writer.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Read gold standard gene mentions from sample.out
   */
  HashMap<String, String> readGoldGM() {
    HashMap<String, String> gmmap = new HashMap<String, String>();
    BufferedReader br;
    String line;
    
    try {
      br = new BufferedReader(new FileReader(mGoldFile));
      while ((line = br.readLine()) != null) {
        String[] lines = line.split("[|]");
        // merge multiple keys
        if(gmmap.containsKey(lines[0])) {
          String tmp = gmmap.get(lines[0]);
          gmmap.put(lines[0], tmp + "\t\t" + lines[2]);
        }
        else
          gmmap.put(line.split("[|]")[0], line.split("[|]")[2]);
      }
      br.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return gmmap;
  }

  /**
   * Called when a batch of processing is completed.
   * 
   * @param aTrace
   *          ProcessTrace object that will log events in this method.
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * @throws IOException
   *           if there is an IO Error
   * 
   * @see org.apache.uima.collection.CasConsumer#batchProcessComplete(ProcessTrace)
   */
  public void batchProcessComplete(ProcessTrace aTrace) throws ResourceProcessException,
          IOException {
    // nothing to do in this case as AnnotationPrinter doesnot do
    // anything cumulatively
  }

  /**
   * Called when the entire collection is completed.
   * 
   * @param aTrace
   *          ProcessTrace object that will log events in this method.
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * @throws IOException
   *           if there is an IO Error
   * @see org.apache.uima.collection.CasConsumer#collectionProcessComplete(ProcessTrace)
   */
  public void collectionProcessComplete(ProcessTrace aTrace) throws ResourceProcessException,
          IOException {
    if (trainWriter != null) {
      trainWriter.close();
    }
    if(testWriter != null)
      testWriter.close();
  }

  /**
   * Reconfigures the parameters of this Consumer. <br>
   * This is used in conjunction with the setConfigurationParameterValue to set the configuration
   * parameter values to values other than the ones specified in the descriptor.
   * 
   * @throws ResourceConfigurationException
   *           if the configuration parameter settings are invalid
   * 
   * @see org.apache.uima.resource.ConfigurableResource#reconfigure()
   */
  public void reconfigure() throws ResourceConfigurationException {
    super.reconfigure();
    // extract configuration parameter settings
    String oPath = (String) getUimaContext().getConfigParameterValue("outputFile");
    File oFile = new File(oPath.trim());
    // if output file has changed, close exiting file and open new
    if (!oFile.equals(this.outFile)) {
      this.outFile = oFile;
      try {
        trainWriter.close();

        // If specified output directory does not exist, try to create it
        if (oFile.getParentFile() != null && !oFile.getParentFile().exists()) {
          if (!oFile.getParentFile().mkdirs())
            throw new ResourceConfigurationException(
                    ResourceInitializationException.RESOURCE_DATA_NOT_VALID, new Object[] { oPath,
                        "outputFile" });
        }
        trainWriter = new FileWriter(oFile);
      } catch (IOException e) {
        throw new ResourceConfigurationException();
      }
    }
  }

  /**
   * Called if clean up is needed in case of exit under error conditions.
   * 
   * @see org.apache.uima.resource.Resource#destroy()
   */
  public void destroy() {
    if (trainWriter != null) {
      try {
        trainWriter.close();
      } catch (IOException e) {
        // ignore IOException on destroy
      }
    }
  }

}
