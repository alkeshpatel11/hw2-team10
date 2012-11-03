package edu.cmu.lti.bio.bdutt.casconsumers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;

import edu.cmu.lti.bio.bdutt.types.Gene;

public class GeneTaggingCasConsumer extends CasConsumer_ImplBase {

  /**
   * This method is overriden version of the processCas method in CasConsumer_ImplBase
   * It accepts the gene annotations and writes it to the output file
   * @param aCas the global CAS context
   */
  @Override
  public void processCas(CAS aCAS) throws ResourceProcessException {
    // TODO Auto-generated method stub
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    try
    {
    String OutputFile = (String) getConfigParameterValue("OutputFile");
    BufferedWriter outputFile = new BufferedWriter(new FileWriter(OutputFile));  
    FSIterator iter = jcas.getAnnotationIndex(Gene.type).iterator();
    while(iter.hasNext())
    {
      Gene FinalAnnotate = (Gene)iter.next();
      outputFile.write(FinalAnnotate.getID() +"|"+ FinalAnnotate.getStartSpan() +" "+FinalAnnotate.getEndSpan()+"|"+ FinalAnnotate.getName() + "\n");
    }
    }
    catch(IOException ioE)
    {
      ioE.printStackTrace();
    }
    
   }

}
