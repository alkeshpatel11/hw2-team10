package edu.cmu.lti.bio.bdutt.annotators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.bdutt.tools.PosTagNamedEntityRecognizer;
import edu.cmu.lti.bio.bdutt.types.Gene;
import edu.cmu.lti.bio.bdutt.types.SourceText;
import abner.Tagger;

public class GeneTaggingAbnerNER extends JCasAnnotator_ImplBase {

  /**
   * Gets the SourceText type annotations and converts them into Gene type annotations
   * @param aJCas This is the Global Jcas context
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    
    try {
    
    FSIndex  SourceDocindex = aJCas.getAnnotationIndex(SourceText.type);
    Iterator sourceIter = SourceDocindex.iterator();
    Tagger BioCreativeTagger = new Tagger(Tagger.BIOCREATIVE); // Using Abner   
    
      //PosTagNamedEntityRecognizer ner = new PosTagNamedEntityRecognizer();
    PosTagNamedEntityRecognizer ner = new PosTagNamedEntityRecognizer();
      
      while(sourceIter.hasNext())
      {
        
          SourceText source = (SourceText)sourceIter.next();
          // running this through the Stanford CoreNLP
          
          Map<Integer, Integer> StanfordHashMap = new HashMap<Integer, Integer>();
          
          StanfordHashMap = ner.getGeneSpans(source.getSourceText());
          
          for (Map.Entry<Integer, Integer> entry : StanfordHashMap.entrySet()) 
          {
          Integer key = entry.getKey();
          Integer value = entry.getValue();
          
          String possibleGeneEntity = source.getSourceText().substring(key, value);
          String result = BioCreativeTagger.tagSGML(possibleGeneEntity);
          
          if(result.contains("PROTEIN") || result.contains("DNA") || result.contains("RNA") || result.contains("CELL-LINE") || result.contains("CELL-TYPE"))
          {
          String StrWsRemove = source.getSourceText().replaceAll(" ","");
          Gene InterAnnot = new Gene(aJCas); 
          InterAnnot.setID(source.getSentenceID());
          InterAnnot.setName(possibleGeneEntity);
          String geneEntity = possibleGeneEntity.replaceAll(" ", "");
          int StartSpan = StrWsRemove.indexOf(geneEntity);
          InterAnnot.setStartSpan(StartSpan);
          InterAnnot.setEndSpan(StartSpan + geneEntity.length() -1);
          InterAnnot.addToIndexes();
          }
          }
      }
          
    
    
    }
    
    catch (ResourceInitializationException e) {
      
      e.printStackTrace();
    }
   
    
    
  }

}
