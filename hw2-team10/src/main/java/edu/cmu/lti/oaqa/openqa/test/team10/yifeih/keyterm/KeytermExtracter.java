package edu.cmu.lti.oaqa.openqa.test.team10.yifeih.keyterm;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.bio.yifeih.tools.*;

import java.util.List;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;

public class KeytermExtracter extends AbstractKeytermExtractor{
  static int allcount = 0;
  static int partcount = 0;
  
  String hmmPath = null;//"data//yifeih//ne-en-bio-genetag.HmmChunker";
  String bannerPath = null;//"data//yifeih//gene_model_v02.bin";
  public void initialize(UimaContext c) throws ResourceInitializationException{
    super.initialize(c);
    hmmPath = (String)c.getConfigParameterValue("hmmPath");
    bannerPath = (String)c.getConfigParameterValue("bannerPath");
  }
  @Override
  protected List<Keyterm> getKeyterms(String arg0) {
    // TODO Auto-generated method stub
    allcount++;
    List<Keyterm> keyterms = new ArrayList<Keyterm>();
    hmmConfidentChunk myChunk = null;
    try {
      myChunk = new hmmConfidentChunk(hmmPath);
    } catch (ResourceInitializationException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Map<Integer, Double> tmpMap = null;
    try {
      tmpMap = myChunk.getgene(arg0);
    } catch (IOException e) {
      
    } 
    Iterator<Entry<Integer, Double>> it = tmpMap.entrySet().iterator();
    int N=0;
    double allConf = 0;
    while (it.hasNext()){    
      
      Map.Entry entry = (Map.Entry) it.next()   ;  
      Integer start = (Integer) entry.getKey();
      Integer end = (int) Math.floor((Double)entry.getValue());// + matcher.start(2);
      Double conf = (Double)entry.getValue() - end;
      if(conf < 0.000000001 ){
        conf = 1.0;end = end -1;
      }
      N++;
      allConf+=conf;
      keyterms.add(new Keyterm(arg0.substring(start, end)));
    }
    /*
    if(N!=0&& allConf/N>0.5){
      partcount++;
      bannerHelp myBanner = null;
        try {
          myBanner = new bannerHelp(bannerPath);
        } catch (ResourceInitializationException e) {
          e.printStackTrace(); 
        } catch (Exception e) {
          e.printStackTrace(); 
        }

  
      // get document text
      
      Map<Integer, Integer> tmpMap2 = null;
      try {
        tmpMap2 = myBanner.getgene(arg0);
      } catch (IOException e) {

      } 
      Iterator<Entry<Integer, Integer>> it2 = tmpMap2.entrySet().iterator();
           //look for space end
      while(it2.hasNext()){    
        Map.Entry entry = (Map.Entry) it2.next();    
        Integer start = (Integer)entry.getKey();  
        Integer end = (Integer)entry.getValue();
        keyterms.add(new Keyterm(arg0.substring(start, end)));
        System.out.println(arg0.substring(start, end));
        System.out.println("check here");
      }  
    }
    System.out.println(partcount+"/"+allcount);

    */
    return keyterms;
  }

}
