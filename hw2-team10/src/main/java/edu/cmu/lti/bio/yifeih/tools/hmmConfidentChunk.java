package edu.cmu.lti.bio.yifeih.tools;
import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.uima.resource.ResourceInitializationException;
/**
 *wrap function of Lingpipe hmm nlp class.
 *input: text to tag.
 *output: array of gene tagged arraylist with score contained
 */
public class hmmConfidentChunk {
    static final int MAX_N_BEST_CHUNKS = 8;
    static private File modelFile;
    static private ConfidenceChunker chunker;
    public hmmConfidentChunk(String text) throws ResourceInitializationException,Exception {
      if(modelFile == null){
        modelFile = new File(text);
        chunker = (ConfidenceChunker) AbstractExternalizable.readObject(modelFile);
        System.out.println("init chuner!");
      }
    }
    public Map<Integer, Double> getgene(String text) throws IOException  {
               Map<Integer, Double> begin2end = new HashMap<Integer, Double>();
        char[] cs = text.toCharArray();
        double upper = 0.7;
        double lower = 0.7;
        Iterator<Chunk> it = chunker.nBestChunks(cs,0,cs.length,MAX_N_BEST_CHUNKS);
        for (int n = 0; it.hasNext(); ++n) {
            Chunk chunk = it.next();
            double conf = java.lang.Math.pow(2.0,chunk.score());
            if (conf>lower){
              begin2end.put(chunk.start(), chunk.end()+conf);
            }                
        }
        return begin2end;
    }
}

