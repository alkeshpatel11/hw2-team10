

/* First created by JCasGen Wed Oct 10 18:59:04 EDT 2012 */
package edu.cmu.lti.bio.bdutt.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Oct 29 18:44:12 EDT 2012
 * XML source: C:/eclipse/Workspace/hw1-bdutt/src/main/resources/GeneTaggingCollectionReader.xml
 * @generated */
public class SourceText extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SourceText.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SourceText() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SourceText(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SourceText(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public SourceText(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: SentenceID

  /** getter for SentenceID - gets 		
   * @generated */
  public String getSentenceID() {
    if (SourceText_Type.featOkTst && ((SourceText_Type)jcasType).casFeat_SentenceID == null)
      jcasType.jcas.throwFeatMissing("SentenceID", "edu.cmu.lti.bio.bdutt.types.SourceText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SourceText_Type)jcasType).casFeatCode_SentenceID);}
    
  /** setter for SentenceID - sets 		 
   * @generated */
  public void setSentenceID(String v) {
    if (SourceText_Type.featOkTst && ((SourceText_Type)jcasType).casFeat_SentenceID == null)
      jcasType.jcas.throwFeatMissing("SentenceID", "edu.cmu.lti.bio.bdutt.types.SourceText");
    jcasType.ll_cas.ll_setStringValue(addr, ((SourceText_Type)jcasType).casFeatCode_SentenceID, v);}    
   
    
  //*--------------*
  //* Feature: SourceText

  /** getter for SourceText - gets 
   * @generated */
  public String getSourceText() {
    if (SourceText_Type.featOkTst && ((SourceText_Type)jcasType).casFeat_SourceText == null)
      jcasType.jcas.throwFeatMissing("SourceText", "edu.cmu.lti.bio.bdutt.types.SourceText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SourceText_Type)jcasType).casFeatCode_SourceText);}
    
  /** setter for SourceText - sets  
   * @generated */
  public void setSourceText(String v) {
    if (SourceText_Type.featOkTst && ((SourceText_Type)jcasType).casFeat_SourceText == null)
      jcasType.jcas.throwFeatMissing("SourceText", "edu.cmu.lti.bio.bdutt.types.SourceText");
    jcasType.ll_cas.ll_setStringValue(addr, ((SourceText_Type)jcasType).casFeatCode_SourceText, v);}    
  }

    