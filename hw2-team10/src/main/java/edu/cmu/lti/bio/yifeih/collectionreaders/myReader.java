package edu.cmu.lti.bio.yifeih.collectionreaders;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import edu.cmu.lti.bio.yifeih.types.*;
/**
 * This simple collection reader file line by line and paring them into id and sentence body.
 * <ul>
 * 1) reads input file from directory. It can be configured with the following parameters:
 * <li><code>InputDirectory</code> - path to directory containing files</li>
 * <li><code>Encoding</code> (optional) - character encoding of the input files</li>
 * <li><code>Language</code> (optional) - language of the input documents</li>
 * 2) Chuck the file into sentence for CAS holding
 * 3) Chuck each sentence into id and sentence and save them into a new annotation: @see myCpe.line
 * 4) Create a new view "freshLine" holding all the annotations
 * </ul>
 * 
 */
public class myReader extends CollectionReader_ImplBase {
  /**
   * Name of configuration parameter that must be set to the path of a directory containing input
   * files.
   */
  public static final String PARAM_INPUTFILE = "InputDirectory";

  /**
   * Name of configuration parameter that contains the character encoding used by the input files.
   * If not specified, the default system encoding will be used.
   */
  public static final String PARAM_ENCODING = "Encoding";

  /**
   * Name of optional configuration parameter that contains the language of the documents in the
   * input directory. If specified this information will be added to the CAS.
   */
  public static final String PARAM_LANGUAGE = "Language";

  /**
   * Name of optional configuration parameter that indicates including
   * the subdirectories (recursively) of the current input directory.
   */
  public static final String PARAM_SUBDIR = "BrowseSubdirectories";
  
  private ArrayList<File> mFiles;
  private String mEncoding;
  private String mLanguage;  
  private Boolean mRecursive;
  private int mCurrentIndex;
  private int lineBegin;
  private int lineEnd;
  private String allText;
  /**
      parsing regex for one line
   */
  private Pattern oneLine = Pattern.compile("([^ ]*)[ ](.*)");

  /**
   * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
   */
  public void initialize() throws ResourceInitializationException {
    File inFile = new File(((String) getConfigParameterValue(PARAM_INPUTFILE)).trim());
    mEncoding  = (String) getConfigParameterValue(PARAM_ENCODING);
    mLanguage  = (String) getConfigParameterValue(PARAM_LANGUAGE);
    mRecursive = (Boolean) getConfigParameterValue(PARAM_SUBDIR);
    if (null == mRecursive) { // could be null if not set, it is optional
      mRecursive = Boolean.FALSE;
    }
    mCurrentIndex = 0;
    if (!inFile.exists()) {
      throw new ResourceInitializationException(ResourceConfigurationException.DIRECTORY_NOT_FOUND,
              new Object[] { PARAM_INPUTFILE, this.getMetaData().getName(), inFile.getPath() });
    }
    try {
      allText = FileUtils.file2String(inFile, mEncoding);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    lineBegin = 0;
    lineEnd = -1;
  }
  

  /**
   * Ends when there is no more sentence in file
   * @see org.apache.uima.collection.CollectionReader#hasNext()
   */
  public boolean hasNext() {
    if(lineEnd==-1)
       return (allText.indexOf("\n")!=-1);
    return (allText.indexOf("\n", lineEnd+1)!=-1);
  }

  /**
   * Parsing each sentence into <code>line</code>: id + sentences; put into <code>freshLine</code> view
   * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
   */
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    lineBegin = lineEnd+1;
    if(lineEnd==-1)
      lineEnd = 0;
    lineEnd = allText.indexOf("\n", lineBegin);
    JCas jcasline;
    try {
      jcasline = jcas.createView("freshLine");
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    Matcher matcher = oneLine.matcher(allText.substring(lineBegin,lineEnd));
    while (matcher.find()) {
      line annotation = new line(jcasline);
      nounPhrases nouns = null;
      annotation.setBegin(matcher.start(2));
      annotation.setEnd(matcher.end(2));
      annotation.setId(matcher.group(1));
      annotation.setSentence(matcher.group(2));
      annotation.addToIndexes();
    }
    jcasline.setDocumentText(allText.substring(lineBegin,lineEnd));


  }

  /**
   * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
   */
  public void close() throws IOException {
  }

  /**
   * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
   */
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(mCurrentIndex, 1, Progress.ENTITIES) };
  }

}
