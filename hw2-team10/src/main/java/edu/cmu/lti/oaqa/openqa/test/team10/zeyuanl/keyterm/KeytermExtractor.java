package edu.cmu.lti.oaqa.openqa.test.team10.zeyuanl.keyterm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import cc.mallet.fst.CRF;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

import edu.cmu.lti.oaqa.cse.basephase.keyterm.AbstractKeytermExtractor;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * A gene mention KeytermExtracter which uses the CRF model trained by previous training process.<br>
 * The KeytermExtracter first reads the CRF model from the file and annotates them, adding index to
 * CAS using type system GeneMention.
 * 
 * @author Zeyuan Li (zeyuanl@cs.cmu.edu)
 */
public class KeytermExtractor extends AbstractKeytermExtractor {
  public static final String BGM = "B_GM";

  public static final String IGM = "I_GM";

  public static final String OGM = "O";

  private static CRF crf = null;

  private static String mmodelPath;

  private StanfordCoreNLP pipeline;

  public void loadModels() {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit");
    pipeline = new StanfordCoreNLP(props);

    try {
      crf = readModel(mmodelPath);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize(UimaContext c) throws ResourceInitializationException {
    super.initialize(c);
    mmodelPath = (String) c.getConfigParameterValue("modelpath");
    loadModels();
  }

  /**
   * Read the CRF model from the file. The path is specified in path attribute in type system
   * "CRFModel".
   * 
   * @param path
   *          path where to read model file
   * @return CRF read CRF model from path
   */
  public CRF readModel(String path) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
    CRF crf = (CRF) ois.readObject();
    ois.close();
    return crf;
  }

  /**
   * A gene mention annotator which uses the CRF model trained by previous training process.<br>
   * The annotator first reads the CRF model from the file and annotates them, adding index to CAS
   * using type system GeneMention.
   * 
   * @author Zeyuan Li (zeyuanl@cs.cmu.edu)
   */
  @Override
  protected List<Keyterm> getKeyterms(String qtext) {
    List<Keyterm> keyterms = new ArrayList<Keyterm>();
    Annotation document = new Annotation(qtext);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    for (CoreMap sentence : sentences) {
      StringBuffer sb = new StringBuffer();
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        // Add target only for adaptable for the training pipeline!
        // target " O" is never used!
        // The input file does not contain tags (aka targets)
        sb.append(token.value() + " O" + "\n");
      }
      sb.append("\n");

      // test
      Pipe p = crf.getInputPipe();

      InstanceList applyData = new InstanceList(p);
      applyData.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(
              new ByteArrayInputStream(sb.toString().getBytes()))), Pattern.compile("^$"), true));

      Iterator iter = applyData.iterator();
      while (iter.hasNext()) {
        Sequence outseq = crf.transduce((Sequence) ((Instance) iter.next()).getData());
        // retrive result
        int idx = 0;
        List<CoreLabel> list = new ArrayList<CoreLabel>();

        for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
          if (!outseq.get(idx++).equals(OGM))
            list.add(token);
          else if (list.size() > 0) {
            int begin = list.get(0).beginPosition();
            int end = list.get(list.size() - 1).endPosition();
            keyterms.add(new Keyterm(qtext.substring(begin, end)));
            list.clear();
          }
        }
        if (list.size() > 0) {
          int begin = list.get(0).beginPosition();
          int end = list.get(list.size() - 1).endPosition();
          keyterms.add(new Keyterm(qtext.substring(begin, end)));
          list.clear();
        }
      }
    }

    return keyterms;
  }

}
