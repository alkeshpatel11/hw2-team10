package edu.cmu.lti.bio.yifeih.tools;
import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;

import com.aliasi.util.AbstractExternalizable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.uima.resource.ResourceInitializationException;
public class hmmChunk {
  static private File modelFile;
  static private Chunker chunker;
  public hmmChunk(String text) throws ResourceInitializationException,Exception {
    if(modelFile == null){
      modelFile = new File(text);
      chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
      System.out.println("init chuner!");
    }
  }
    public Map<Integer, Integer> getgene(String text)  {
  

  //System.out.println("Reading chunker from file=" + modelFile);
      Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
      String tmp;
      Chunking chunking = chunker.chunk(text);
      Set<Chunk> a =chunking.chunkSet();
      
      for (Chunk str : a) {
       tmp = str.toString();
       int start = tmp.indexOf("-");
       int end = tmp.indexOf(":");
       int begin = Integer.parseInt(tmp.substring(0, start));
       int ending = Integer.parseInt(tmp.substring(start+1, end));
       begin2end.put(begin, ending);
        //System.out.println(text.substring(begin,ending));
      }
      return begin2end;
    }

}