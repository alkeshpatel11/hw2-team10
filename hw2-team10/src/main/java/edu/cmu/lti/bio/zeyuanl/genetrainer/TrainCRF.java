package edu.cmu.lti.bio.zeyuanl.genetrainer;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import cc.mallet.fst.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.pipe.tsf.*;
import cc.mallet.types.*;
import edu.cmu.lti.bio.zeyuanl.types.CRFModel;

/**
 * A sequence tagging trainer using CRF model. It can be
 * configured with the following parameters:
 * <ul>
 * <li><code>trainFile</code> - path to the training file</li>
 * <li><code>testFile</code>  - path to the training file</li>
 * <li><code>modelPath</code>  - path to the model file</li>
 * <li><code>iterations</code> - number of max iteration in training process</li>
 * </ul>
 * @author Zeyuan Li (zeyuanl@cs.cmu.edu)
 */
public class TrainCRF extends JCasAnnotator_ImplBase {
  private String mtrainFile, mtestFile, mmodelPath;
  private int miter;

  // Must have 0-argument constructor for UIMA
  public TrainCRF(){}
  
  /**
   * This method trains the CRF model using orthographic and gene specific features.
   * The model is persisted in local file when training finised, which path parameter is set in type system "CRFModel". 
   * 
   * @param trainingFile the path of the training file
   * @param testingFile the path of the testing file
   */
  public void TrainModelCRF(String trainingFile, String testingFile) throws IOException {
    ArrayList<Pipe> pipes = new ArrayList<Pipe>();

    int[][] conjunctions = new int[6][];
    conjunctions[0] = new int[] { -1 };
    conjunctions[1] = new int[] { 1 };
    conjunctions[2] = new int[] { -2 };
    conjunctions[3] = new int[] { 2 };
    conjunctions[4] = new int[] { 0, 1 };
    conjunctions[5] = new int[] { -1, 0 };

    pipes.add(new SimpleTaggerSentence2TokenSequence());
    pipes.add(new OffsetConjunctions(conjunctions));
    // pipes.add(new FeaturesInWindow("PREV-", -1, 1));
    pipes.add(new TokenTextCharSuffix("C1=", 1));
    pipes.add(new TokenTextCharSuffix("C2=", 2));
    pipes.add(new TokenTextCharSuffix("C3=", 3));
    pipes.add(new TokenTextCharPrefix("P1=", 1));
    pipes.add(new TokenTextCharPrefix("P1=", 1));
    pipes.add(new TokenTextCharPrefix("P1=", 1));
    pipes.add(new RegexMatches("CAPITALIZED", Pattern.compile("^\\p{Lu}.*")));
    pipes.add(new RegexMatches("STARTSNUMBER", Pattern.compile("^[0-9].*")));
    pipes.add(new RegexMatches("HYPHENATED", Pattern.compile(".*\\-.*")));
    pipes.add(new RegexMatches("DOLLARSIGN", Pattern.compile(".*\\$.*")));
    pipes.add(new RegexMatches("HASDASH", Pattern.compile(".*-.*")));
    pipes.add(new RegexMatches("HASDIGIT", Pattern.compile(".*[0-9].*")));
    pipes.add(new RegexMatches("ALPHNUM", Pattern.compile("[A-Za-z0-9]+")));
    pipes.add(new RegexMatches("ALLCAP", Pattern.compile("[A-Z]+")));
    pipes.add(new RegexMatches("MIXCASE", Pattern.compile("[A-Za-z]+")));
    pipes.add(new RegexMatches("HASBRACKET", Pattern.compile(".*[()].*")));
    pipes.add(new RegexMatches("PARTOFGENE", Pattern.compile(".*gene|.*like|.*ase|homeo.*")));
    // additional features
    /*
    pipes.add(new RegexMatches("PUNC", Pattern.compile(".*[.,-=$?/!\"].*")));
    pipes.add(new RegexMatches("SINGLEDIGIT", Pattern.compile("[0-9]")));
    pipes.add(new RegexMatches("DOUBLEDIGIT", Pattern.compile("[0-9]{2}")));
    pipes.add(new RegexMatches("ENDDASH", Pattern.compile(".*-")));
    pipes.add(new RegexMatches("TWOCAPS", Pattern.compile("[A-Z]{2}")));
    pipes.add(new RegexMatches("THREECAPS", Pattern.compile("[A-Z]{3}")));*/
    
    pipes.add(new TokenFirstPosition("FIRSTTOKEN"));
    pipes.add(new TokenSequence2FeatureVectorSequence());

    Pipe pipe = new SerialPipes(pipes);

    InstanceList trainingInstances = new InstanceList(pipe);
    InstanceList testingInstances = new InstanceList(pipe);

    trainingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(
            new FileInputStream(trainingFile))), Pattern.compile("^\\s*$"),
            true));
    testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(
            new FileInputStream(testingFile))), Pattern.compile("^\\s*$"),
            true));

    CRF crf = new CRF(pipe, null);
    // crf.addStatesForLabelsConnectedAsIn(trainingInstances);
    crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
    crf.addStartState();

    CRFTrainerByLabelLikelihood trainer = new CRFTrainerByLabelLikelihood(crf);
    trainer.setGaussianPriorVariance(1.0);

    // trainer.addEvaluator(new PerClassAccuracyEvaluator(trainingInstances, "training"));
    trainer.addEvaluator(new PerClassAccuracyEvaluator(testingInstances, "testing"));
    trainer.addEvaluator(new TokenAccuracyEvaluator(testingInstances, "testing"));
    trainer.train(trainingInstances, miter);

    // saving model
    FileOutputStream fos = new FileOutputStream(mmodelPath);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(crf);
    oos.close();
  }

  /**
   * Initializes this TrainCRF with the parameters specified in the descriptor.
   * 
   * @throws ResourceInitializationException
   *           if there is error in initializing the resources
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    mtrainFile = (String) aContext.getConfigParameterValue("trainFile");
    mtestFile = (String) aContext.getConfigParameterValue("testFile");
    mmodelPath = (String) aContext.getConfigParameterValue("modelPath");
    miter = (Integer) aContext.getConfigParameterValue("iterations");
  }

  @Override   
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    CRFModel crfmodel = new CRFModel(aJCas);

    File model = new File(mmodelPath);
    if (!model.exists()) {
      try {
        TrainModelCRF(mtrainFile, mtestFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    crfmodel.setPath(mmodelPath);
    crfmodel.addToIndexes();
  }

}