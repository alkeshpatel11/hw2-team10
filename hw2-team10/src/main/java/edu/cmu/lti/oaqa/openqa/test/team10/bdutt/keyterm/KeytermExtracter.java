package edu.cmu.lti.oaqa.openqa.test.team10.bdutt.keyterm;

import java.util.ArrayList;
import java.util.List;
import abner.Tagger;
import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class KeytermExtracter extends AbstractKeytermExtractor{

  @Override
  protected List<Keyterm> getKeyterms(String arg0) {
    // TODO Auto-generated method stub
    arg0 = arg0.replace('?', ' ');
    arg0 = arg0.replace('(', ' ');
    arg0 = arg0.replace('[', ' ');
    arg0 = arg0.replace(')', ' ');
    arg0 = arg0.replace(']', ' ');
    arg0 = arg0.replace('/', ' ');
    arg0 = arg0.replace('\'', ' ');
    
    Tagger BioCreativeTagger = new Tagger(Tagger.BIOCREATIVE); // Using Abner
    
    String[] questionTokens = arg0.split("\\s+");
    List<Keyterm> keyterms = new ArrayList<Keyterm>();
    for (int i = 0; i < questionTokens.length; i++) {
      String result = BioCreativeTagger.tagSGML(questionTokens[i]);      
      if(result.contains("PROTEIN") || result.contains("DNA") || result.contains("RNA") || result.contains("CELL-LINE") || result.contains("CELL-TYPE"))
      {      
      keyterms.add(new Keyterm(questionTokens[i]));
      }
    }
    
    
    return keyterms;
  }

}
