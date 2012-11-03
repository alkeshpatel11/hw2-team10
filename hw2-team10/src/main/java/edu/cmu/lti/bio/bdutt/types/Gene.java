

/* First created by JCasGen Wed Oct 10 18:31:27 EDT 2012 */
package edu.cmu.lti.bio.bdutt.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Oct 29 18:44:12 EDT 2012
 * XML source: C:/eclipse/Workspace/hw1-bdutt/src/main/resources/GeneTaggingCollectionReader.xml
 * @generated */
public class Gene extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Gene.class);
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
  protected Gene() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Gene(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Gene(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Gene(JCas jcas, int begin, int end) {
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
  //* Feature: ID

  /** getter for ID - gets This will be the ID of the sentence where we have annotated what we think is a Gene name
   * @generated */
  public String getID() {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "edu.cmu.lti.bio.bdutt.types.Gene");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Gene_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets This will be the ID of the sentence where we have annotated what we think is a Gene name 
   * @generated */
  public void setID(String v) {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "edu.cmu.lti.bio.bdutt.types.Gene");
    jcasType.ll_cas.ll_setStringValue(addr, ((Gene_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: Name

  /** getter for Name - gets This is the name of the Gene
   * @generated */
  public String getName() {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_Name == null)
      jcasType.jcas.throwFeatMissing("Name", "edu.cmu.lti.bio.bdutt.types.Gene");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Gene_Type)jcasType).casFeatCode_Name);}
    
  /** setter for Name - sets This is the name of the Gene 
   * @generated */
  public void setName(String v) {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_Name == null)
      jcasType.jcas.throwFeatMissing("Name", "edu.cmu.lti.bio.bdutt.types.Gene");
    jcasType.ll_cas.ll_setStringValue(addr, ((Gene_Type)jcasType).casFeatCode_Name, v);}    
   
    
  //*--------------*
  //* Feature: StartSpan

  /** getter for StartSpan - gets Starting span value of the Gene name.
   * @generated */
  public int getStartSpan() {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_StartSpan == null)
      jcasType.jcas.throwFeatMissing("StartSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Gene_Type)jcasType).casFeatCode_StartSpan);}
    
  /** setter for StartSpan - sets Starting span value of the Gene name. 
   * @generated */
  public void setStartSpan(int v) {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_StartSpan == null)
      jcasType.jcas.throwFeatMissing("StartSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    jcasType.ll_cas.ll_setIntValue(addr, ((Gene_Type)jcasType).casFeatCode_StartSpan, v);}    
   
    
  //*--------------*
  //* Feature: EndSpan

  /** getter for EndSpan - gets Ending span value of the gene name
   * @generated */
  public int getEndSpan() {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_EndSpan == null)
      jcasType.jcas.throwFeatMissing("EndSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Gene_Type)jcasType).casFeatCode_EndSpan);}
    
  /** setter for EndSpan - sets Ending span value of the gene name 
   * @generated */
  public void setEndSpan(int v) {
    if (Gene_Type.featOkTst && ((Gene_Type)jcasType).casFeat_EndSpan == null)
      jcasType.jcas.throwFeatMissing("EndSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    jcasType.ll_cas.ll_setIntValue(addr, ((Gene_Type)jcasType).casFeatCode_EndSpan, v);}    
  }

    