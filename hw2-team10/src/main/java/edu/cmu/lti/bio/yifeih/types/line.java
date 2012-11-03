

/* First created by JCasGen Sat Oct 13 15:55:34 EDT 2012 */
package edu.cmu.lti.bio.yifeih.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Oct 17 12:30:58 EDT 2012
 * XML source: /Users/Yitei/Documents/workspace/11791/hw1-yifeh/hw1-yifeih/src/main/resources/descriptors/lineType.xml
 * @generated */
public class line extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(line.class);
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
  protected line() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public line(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public line(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public line(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /** getter for id - gets 
   * @generated */
  public String getId() {
    if (line_Type.featOkTst && ((line_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "myCpe.line");
    return jcasType.ll_cas.ll_getStringValue(addr, ((line_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated */
  public void setId(String v) {
    if (line_Type.featOkTst && ((line_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "myCpe.line");
    jcasType.ll_cas.ll_setStringValue(addr, ((line_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: sentence

  /** getter for sentence - gets 
   * @generated */
  public String getSentence() {
    if (line_Type.featOkTst && ((line_Type)jcasType).casFeat_sentence == null)
      jcasType.jcas.throwFeatMissing("sentence", "myCpe.line");
    return jcasType.ll_cas.ll_getStringValue(addr, ((line_Type)jcasType).casFeatCode_sentence);}
    
  /** setter for sentence - sets  
   * @generated */
  public void setSentence(String v) {
    if (line_Type.featOkTst && ((line_Type)jcasType).casFeat_sentence == null)
      jcasType.jcas.throwFeatMissing("sentence", "myCpe.line");
    jcasType.ll_cas.ll_setStringValue(addr, ((line_Type)jcasType).casFeatCode_sentence, v);}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets score of the sentence. The higher the more possible it will have contain gene words.
   * @generated */
  public double getScore() {
    if (line_Type.featOkTst && ((line_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "myCpe.line");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((line_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets score of the sentence. The higher the more possible it will have contain gene words. 
   * @generated */
  public void setScore(double v) {
    if (line_Type.featOkTst && ((line_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "myCpe.line");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((line_Type)jcasType).casFeatCode_score, v);}    
  }

    