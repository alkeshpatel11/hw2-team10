

/* First created by JCasGen Sun Oct 07 00:32:25 EDT 2012 */
package edu.cmu.lti.bio.zeyuanl.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


import org.apache.uima.jcas.cas.StringArray;


/** 
 * Updated by JCasGen Wed Oct 10 21:43:50 EDT 2012
 * XML source: /Users/lzy/Code/hw1-zeyuanl/src/main/resources/descriptors/GeneMentionTypeSystem.xml
 * @generated */
public class GeneMention extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(GeneMention.class);
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
  protected GeneMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public GeneMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public GeneMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public GeneMention(JCas jcas, int begin, int end) {
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
     
  @Override
  public boolean equals(Object other){
      if (other == null) return false;
      if (other == this) return true;
      if (!(other instanceof GeneMention))return false;
      GeneMention gm = (GeneMention) other;
      return gm.getId().equals(this.getId()) &&
              gm.getStart() == this.getStart() &&
              gm.getEnd() == this.getEnd();
  }
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated */
  public String getId() {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "edu.lti.cmu.hw1.typesys.GeneMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneMention_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated */
  public void setId(String v) {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "edu.lti.cmu.hw1.typesys.GeneMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneMention_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.lti.cmu.hw1.typesys.GeneMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneMention_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.lti.cmu.hw1.typesys.GeneMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneMention_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: tag

  /** getter for tag - gets 
   * @generated */
  public String getTag() {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_tag == null)
      jcasType.jcas.throwFeatMissing("tag", "edu.lti.cmu.hw1.typesys.GeneMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tag);}
    
  /** setter for tag - sets  
   * @generated */
  public void setTag(String v) {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_tag == null)
      jcasType.jcas.throwFeatMissing("tag", "edu.lti.cmu.hw1.typesys.GeneMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tag, v);}    
   
    
  //*--------------*
  //* Feature: tokenArr

  /** getter for tokenArr - gets 
   * @generated */
  public StringArray getTokenArr() {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_tokenArr == null)
      jcasType.jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tokenArr)));}
    
  /** setter for tokenArr - sets  
   * @generated */
  public void setTokenArr(StringArray v) {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_tokenArr == null)
      jcasType.jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tokenArr, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for tokenArr - gets an indexed value - 
   * @generated */
  public String getTokenArr(int i) {
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_tokenArr == null)
      jcasType.jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tokenArr), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tokenArr), i);}

  /** indexed setter for tokenArr - sets an indexed value - 
   * @generated */
  public void setTokenArr(int i, String v) { 
    if (GeneMention_Type.featOkTst && ((GeneMention_Type)jcasType).casFeat_tokenArr == null)
      jcasType.jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tokenArr), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((GeneMention_Type)jcasType).casFeatCode_tokenArr), i, v);}
  }

    