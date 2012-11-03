package edu.cmu.lti.bio.bdutt.casreaders;

import java.io.*;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;

import edu.cmu.lti.bio.bdutt.types.SourceText;

public class GeneTaggingCollectionReader extends CollectionReader_ImplBase {

  Boolean hasNext = true;
  
  /**
   * Called when the AE wants to get the next Annotations to work on.
   * In our case its only called once and reads the whole file at once
   * @param aCas the global CAS context
   */
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    // TODO Auto-generated method stub
    
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    //Getting the Configuration Parameter
    
    String ImputFilePath = (String) getConfigParameterValue("InputFile");
    BufferedReader inputFile = new BufferedReader(new FileReader(ImputFilePath));
    
    //Breaking each sentence into ID and text
    String a[] = null;
    String str;
    while((str = inputFile.readLine()) != null)
    {
      
    //Splitting the string into Sentence ID and Sentence to be annotated
    a = str.split(" ",2);
    SourceText mySource = new SourceText(jcas);
    mySource.setSentenceID(a[0]);
    mySource.setSourceText(a[1]);
    mySource.addToIndexes(jcas);
    }
    
    jcas.setDocumentText(FileUtils.reader2String(inputFile)); //for book-keeping
    
    hasNext = false;
  }

  /**
   * Return True/False based on whether there are any more lines to be read
   * @return boolean if true then the AE will call getNext()
   */
  @Override
  public boolean hasNext() throws IOException, CollectionException {
    // TODO Auto-generated method stub
    return hasNext;
  }

  @Override
  public Progress[] getProgress() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }
  
   }


