

/* First created by JCasGen Sat Oct 13 21:35:55 EDT 2012 */
package edu.cmu.lti.bio.yifeih.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Oct 17 12:30:58 EDT 2012
 * XML source: /Users/Yitei/Documents/workspace/11791/hw1-yifeh/hw1-yifeih/src/main/resources/descriptors/lineType.xml
 * @generated */
public class nounPhrases extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(nounPhrases.class);
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
  protected nounPhrases() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public nounPhrases(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public nounPhrases(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public nounPhrases(JCas jcas, int begin, int end) {
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
  //* Feature: nouns

  /** getter for nouns - gets 
   * @generated */
  public String getNouns() {
    if (nounPhrases_Type.featOkTst && ((nounPhrases_Type)jcasType).casFeat_nouns == null)
      jcasType.jcas.throwFeatMissing("nouns", "myCpe.nounPhrases");
    return jcasType.ll_cas.ll_getStringValue(addr, ((nounPhrases_Type)jcasType).casFeatCode_nouns);}
    
  /** setter for nouns - sets  
   * @generated */
  public void setNouns(String v) {
    if (nounPhrases_Type.featOkTst && ((nounPhrases_Type)jcasType).casFeat_nouns == null)
      jcasType.jcas.throwFeatMissing("nouns", "myCpe.nounPhrases");
    jcasType.ll_cas.ll_setStringValue(addr, ((nounPhrases_Type)jcasType).casFeatCode_nouns, v);}    
   
    
  //*--------------*
  //* Feature: confidence

  /** getter for confidence - gets confidence that this word is gene related words
   * @generated */
  public double getConfidence() {
    if (nounPhrases_Type.featOkTst && ((nounPhrases_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "myCpe.nounPhrases");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((nounPhrases_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets confidence that this word is gene related words 
   * @generated */
  public void setConfidence(double v) {
    if (nounPhrases_Type.featOkTst && ((nounPhrases_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "myCpe.nounPhrases");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((nounPhrases_Type)jcasType).casFeatCode_confidence, v);}    
  }

    