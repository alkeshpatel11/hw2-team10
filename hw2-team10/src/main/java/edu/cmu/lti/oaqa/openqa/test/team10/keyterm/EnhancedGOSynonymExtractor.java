package edu.cmu.lti.oaqa.openqa.test.team10.keyterm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;
import edu.cmu.lti.bio.alkesh.genetrainer.NGramLuceneWrapper;
import edu.cmu.lti.oaqa.bio.resource_wrapper.obo.OBOGraph;
import edu.cmu.lti.oaqa.bio.resource_wrapper.obo.OBONode;
import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class EnhancedGOSynonymExtractor extends AbstractKeytermExtractor {

  private int MAX_SIZE;
  private String GoFilePath;
  public NGramLuceneWrapper nGramLuceneWrapper;
  @Override
  public void initialize(UimaContext c) throws ResourceInitializationException {
   
    super.initialize(c);
    MAX_SIZE = (Integer)c.getConfigParameterValue("max_size");
    GoFilePath = (String) c.getConfigParameterValue("goFilePath","data/gene_ontology_ext.obo");
    nGramLuceneWrapper=new NGramLuceneWrapper();
  }
  
  
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
    ArrayList<GeneCount> ngramReturn = new ArrayList<GeneCount>();
    ArrayList<GeneCount> ngramTotal = new  ArrayList<GeneCount>();
    ArrayList<String> ngramSynonyms = new ArrayList<String>();
    //Simply adding question terms to the list of keyterms
    for(int i=0; i<questionTokens.length;i++)
    {
        keyterms.add(new Keyterm(questionTokens[i]));
    }

    
  //Getting synonyms from NGram
    
    SimilarNGramExtractor ngramExtractor = new SimilarNGramExtractor();
    for(String ngramSyn:questionTokens)
    {
    try {
      ngramReturn= ngramExtractor.getSynonyms(ngramSyn);
      
      for(GeneCount gc: ngramReturn)
      {
          ngramTotal.add(gc);   
      }
      
    } catch (Exception e) {
      
      e.printStackTrace();
    }
    }
    
    
    //Calling GO synonym extractor
    int j=0;
    for(GeneCount gca: ngramTotal)
    {
      ngramSynonyms.add(gca.getGeneName());
      
    }
    System.out.println("NGRAM =" + ngramSynonyms.toString());
    ArrayList<String> SynFromGO = findBestRelatedGenesFromGO(ngramSynonyms);
    
    
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

  private ArrayList<String> findBestRelatedGenesFromGO(ArrayList<String> genes)
  {  
  ArrayList<String> geneListToSend = new ArrayList<String>();
  try {
			OBOGraph graph = new OBOGraph(new FileReader(new File(GoFilePath)));
        
    for(int i=0; i<genes.size(); i++)
    {
      
      ArrayList<OBONode> oboNodes = graph.search(genes.get(i));
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
  
  public static void main(String args[]) {
		try {
			EnhancedGOSynonymExtractor extractor = new EnhancedGOSynonymExtractor();
			List<Keyterm> list = extractor
					.getKeyterms("beta-amyloid precursor");
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  
  
}
