

/* First created by JCasGen Wed Oct 10 13:38:03 EDT 2012 */
package edu.cmu.lti.bio.zeyuanl.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Oct 10 13:38:03 EDT 2012
 * XML source: /Users/lzy/Code/hw1-zeyuanl/src/main/resources/descriptors/CRFModelTypeSystem.xml
 * @generated */
public class CRFModel extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CRFModel.class);
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
  protected CRFModel() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public CRFModel(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public CRFModel(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public CRFModel(JCas jcas, int begin, int end) {
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
  //* Feature: path

  /** getter for path - gets 
   * @generated */
  public String getPath() {
    if (CRFModel_Type.featOkTst && ((CRFModel_Type)jcasType).casFeat_path == null)
      jcasType.jcas.throwFeatMissing("path", "edu.lti.cmu.hw1.typesys.CRFModel");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CRFModel_Type)jcasType).casFeatCode_path);}
    
  /** setter for path - sets  
   * @generated */
  public void setPath(String v) {
    if (CRFModel_Type.featOkTst && ((CRFModel_Type)jcasType).casFeat_path == null)
      jcasType.jcas.throwFeatMissing("path", "edu.lti.cmu.hw1.typesys.CRFModel");
    jcasType.ll_cas.ll_setStringValue(addr, ((CRFModel_Type)jcasType).casFeatCode_path, v);}    
  }

    