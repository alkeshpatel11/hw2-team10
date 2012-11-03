

/* First created by JCasGen Sat Oct 13 17:12:36 EDT 2012 */
package edu.cmu.lti.bio.alkesh.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sat Oct 13 17:12:36 EDT 2012
 * XML source: /host/Users/alkesh/Desktop/Semester1/F12-Software_Engineering_for_Information_Systems/Assignments/hw1-alkeshku/src/main/resources/TypeSystemDescriptor.xml
 * @generated */
public class GeneTag extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(GeneTag.class);
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
  protected GeneTag() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public GeneTag(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public GeneTag(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: geneName

  /** getter for geneName - gets 
   * @generated */
  public String getGeneName() {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_geneName == null)
      jcasType.jcas.throwFeatMissing("geneName", "edu.cmu.lti.bio.types.GeneTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneTag_Type)jcasType).casFeatCode_geneName);}
    
  /** setter for geneName - sets  
   * @generated */
  public void setGeneName(String v) {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_geneName == null)
      jcasType.jcas.throwFeatMissing("geneName", "edu.cmu.lti.bio.types.GeneTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneTag_Type)jcasType).casFeatCode_geneName, v);}    
   
    
  //*--------------*
  //* Feature: start

  /** getter for start - gets 
   * @generated */
  public int getStart() {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_start == null)
      jcasType.jcas.throwFeatMissing("start", "edu.cmu.lti.bio.types.GeneTag");
    return jcasType.ll_cas.ll_getIntValue(addr, ((GeneTag_Type)jcasType).casFeatCode_start);}
    
  /** setter for start - sets  
   * @generated */
  public void setStart(int v) {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_start == null)
      jcasType.jcas.throwFeatMissing("start", "edu.cmu.lti.bio.types.GeneTag");
    jcasType.ll_cas.ll_setIntValue(addr, ((GeneTag_Type)jcasType).casFeatCode_start, v);}    
   
    
  //*--------------*
  //* Feature: end

  /** getter for end - gets 
   * @generated */
  public int getEnd() {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_end == null)
      jcasType.jcas.throwFeatMissing("end", "edu.cmu.lti.bio.types.GeneTag");
    return jcasType.ll_cas.ll_getIntValue(addr, ((GeneTag_Type)jcasType).casFeatCode_end);}
    
  /** setter for end - sets  
   * @generated */
  public void setEnd(int v) {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_end == null)
      jcasType.jcas.throwFeatMissing("end", "edu.cmu.lti.bio.types.GeneTag");
    jcasType.ll_cas.ll_setIntValue(addr, ((GeneTag_Type)jcasType).casFeatCode_end, v);}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets 
   * @generated */
  public double getScore() {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.bio.types.GeneTag");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((GeneTag_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets  
   * @generated */
  public void setScore(double v) {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.bio.types.GeneTag");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((GeneTag_Type)jcasType).casFeatCode_score, v);}    
  }

    