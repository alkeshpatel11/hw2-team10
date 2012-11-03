package edu.cmu.lti.bio.zeyuanl.annotators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import cc.mallet.fst.CRF;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

import edu.cmu.lti.bio.zeyuanl.types.CRFModel;
import edu.cmu.lti.bio.zeyuanl.types.GeneMention;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * A gene mention annotator which uses the CRF model trained by previous training process.<br> 
 * The annotator first reads the CRF model from the file and annotates them, 
 * adding index to CAS using type system GeneMention.
 * 
 * @author Zeyuan Li (zeyuanl@cs.cmu.edu)
 */
public class GeneMentionAnnotatorCRF extends JCasAnnotator_ImplBase {
  public static final String BGM = "B_GM";
  public static final String IGM = "I_GM";
  public static final String OGM = "O";
  
  private StanfordCoreNLP pipeline;
  private String mmodelPath;
  
  public GeneMentionAnnotatorCRF() throws ResourceInitializationException {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit");
    pipeline = new StanfordCoreNLP(props);
  }

  /**
   * This method uses crf model to tag testing sentence and output their span.
   *  
   * @param crf the crf model
   * @param text the testing sentence
   * @return Map<Integer, Integer> the gene mention span pairs
   */
  public Map<Integer, Integer> getGeneSpansCRF(CRF crf, String text) {
    Map<Integer, Integer> begin2end = new HashMap<Integer, Integer>();
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    for (CoreMap sentence : sentences) {
      StringBuffer sb = new StringBuffer();
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        // Add target only for adaptable for the training pipeline!
        // target " O" is never used!
        // The input file does not contain tags (aka targets)
        sb.append(token.value() + " O"+ "\n");
      }
      sb.append("\n");

      // test
      Pipe p = crf.getInputPipe();

      InstanceList applyData = new InstanceList(p);
      applyData.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(
              new ByteArrayInputStream(sb.toString().getBytes()))), Pattern.compile("^$"), true));

      Iterator iter = applyData.iterator();
      while(iter.hasNext()) {
        Sequence outseq = crf.transduce((Sequence)((Instance) iter.next()).getData());
        // retrive result
        int idx = 0;
        List<CoreLabel> list = new ArrayList<CoreLabel>();
        
        for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
          if(!outseq.get(idx++).equals(OGM))
            list.add(token);
          else if(list.size() > 0) {
            int begin = list.get(0).beginPosition(); 
            int end = list.get(list.size()-1).endPosition(); 
            begin2end.put(begin, end); 
            list.clear();
          }
        }
        if(list.size() > 0) {
          int begin = list.get(0).beginPosition(); 
          int end = list.get(list.size()-1).endPosition(); 
          begin2end.put(begin, end); 
          list.clear();
        }
      }
    }
    return begin2end;
  }

  /**
   * Read the CRF model from the file. The path is specified in path attribute in type system "CRFModel".
   * 
   * @param path path where to read model file
   * @return CRF read CRF model from path
   */
  public CRF readModel(String path) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
    CRF crf = (CRF) ois.readObject();
    ois.close();
    return crf;
  }

  /**
   * The process method process the document read by the FileSystemCollectionReader and 
   * output the GeneMention type system to the CAS
   * 
   * @see JCasAnnotator_ImplBase#process(JCas)
   * @see FileSystemCollectionReader
   */
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    // get model path
    FSIndex crfIndex = jcas.getAnnotationIndex(CRFModel.type);
    Iterator iter = crfIndex.iterator();
    // only one instance
    while (iter.hasNext()) {
      CRFModel model = (CRFModel) iter.next();
      mmodelPath = model.getPath();
    }

    CRF crf = null;
    try {
      crf = readModel(mmodelPath);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    String text = jcas.getDocumentText();
    String[] textLine = text.split("\n");

    for (int i = 0; i < textLine.length; i++) {
      String line = textLine[i];
      String sentid = line.substring(0, line.indexOf(' '));
      String sent = line.substring(line.indexOf(' ') + 1);
      Map<Integer, Integer> gslineMap = getGeneSpansCRF(crf, sent);
      Set<Map.Entry<Integer, Integer>> entries = gslineMap.entrySet();
      for (Map.Entry<Integer, Integer> entry : entries) {
        GeneMention gmAnnot = new GeneMention(jcas);
        gmAnnot.setBegin(getIdx(sent, entry.getKey()));
        gmAnnot.setEnd(getIdx(sent, entry.getValue() - 1));
        gmAnnot.setId(sentid);
        gmAnnot.setText(sent);
        gmAnnot.setTag(sent.substring(entry.getKey(), entry.getValue()));
        gmAnnot.addToIndexes();
      }
    }
  }
  
  int getIdx(String sent, int end) {
    int idx = 0;
    for (int i = 0; i < end; i++)
      if (sent.charAt(i) != ' ')
        idx++;
    return idx;
  }
}
