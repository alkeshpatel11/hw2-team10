

/* First created by JCasGen Sat Oct 06 20:30:32 EDT 2012 */
package edu.cmu.lti.bio.zeyuanl.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat Oct 06 22:40:37 EDT 2012
 * XML source: /Users/lzy/Code/hw1-zeyuanl/src/main/resources/descriptors/SourceDocInfoTypeSystem.xml
 * @generated */
public class SourceDocInfo extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SourceDocInfo.class);
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
  protected SourceDocInfo() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SourceDocInfo(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SourceDocInfo(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public SourceDocInfo(JCas jcas, int begin, int end) {
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
  //* Feature: uri

  /** getter for uri - gets 
   * @generated */
  public String getUri() {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_uri);}
    
  /** setter for uri - sets  
   * @generated */
  public void setUri(String v) {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_uri, v);}    
   
    
  //*--------------*
  //* Feature: offsetInSource

  /** getter for offsetInSource - gets 
   * @generated */
  public int getOffsetInSource() {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_offsetInSource == null)
      jcasType.jcas.throwFeatMissing("offsetInSource", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    return jcasType.ll_cas.ll_getIntValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_offsetInSource);}
    
  /** setter for offsetInSource - sets  
   * @generated */
  public void setOffsetInSource(int v) {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_offsetInSource == null)
      jcasType.jcas.throwFeatMissing("offsetInSource", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    jcasType.ll_cas.ll_setIntValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_offsetInSource, v);}    
   
    
  //*--------------*
  //* Feature: documentSize

  /** getter for documentSize - gets 
   * @generated */
  public int getDocumentSize() {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    return jcasType.ll_cas.ll_getIntValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_documentSize);}
    
  /** setter for documentSize - sets  
   * @generated */
  public void setDocumentSize(int v) {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    jcasType.ll_cas.ll_setIntValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_documentSize, v);}    
   
    
  //*--------------*
  //* Feature: lastSegment

  /** getter for lastSegment - gets 
   * @generated */
  public boolean getLastSegment() {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_lastSegment == null)
      jcasType.jcas.throwFeatMissing("lastSegment", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_lastSegment);}
    
  /** setter for lastSegment - sets  
   * @generated */
  public void setLastSegment(boolean v) {
    if (SourceDocInfo_Type.featOkTst && ((SourceDocInfo_Type)jcasType).casFeat_lastSegment == null)
      jcasType.jcas.throwFeatMissing("lastSegment", "edu.lti.cmu.hw1.typesys.SourceDocInfo");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((SourceDocInfo_Type)jcasType).casFeatCode_lastSegment, v);}    
  }

    