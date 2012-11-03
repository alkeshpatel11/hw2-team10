
/* First created by JCasGen Wed Oct 10 18:31:27 EDT 2012 */
package edu.cmu.lti.bio.bdutt.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Oct 29 18:44:12 EDT 2012
 * @generated */
public class Gene_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Gene_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Gene_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Gene(addr, Gene_Type.this);
  			   Gene_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Gene(addr, Gene_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Gene.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.bio.bdutt.types.Gene");
 
  /** @generated */
  final Feature casFeat_ID;
  /** @generated */
  final int     casFeatCode_ID;
  /** @generated */ 
  public String getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "edu.cmu.lti.bio.bdutt.types.Gene");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "edu.cmu.lti.bio.bdutt.types.Gene");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Name;
  /** @generated */
  final int     casFeatCode_Name;
  /** @generated */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_Name == null)
      jcas.throwFeatMissing("Name", "edu.cmu.lti.bio.bdutt.types.Gene");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Name);
  }
  /** @generated */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_Name == null)
      jcas.throwFeatMissing("Name", "edu.cmu.lti.bio.bdutt.types.Gene");
    ll_cas.ll_setStringValue(addr, casFeatCode_Name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_StartSpan;
  /** @generated */
  final int     casFeatCode_StartSpan;
  /** @generated */ 
  public int getStartSpan(int addr) {
        if (featOkTst && casFeat_StartSpan == null)
      jcas.throwFeatMissing("StartSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    return ll_cas.ll_getIntValue(addr, casFeatCode_StartSpan);
  }
  /** @generated */    
  public void setStartSpan(int addr, int v) {
        if (featOkTst && casFeat_StartSpan == null)
      jcas.throwFeatMissing("StartSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    ll_cas.ll_setIntValue(addr, casFeatCode_StartSpan, v);}
    
  
 
  /** @generated */
  final Feature casFeat_EndSpan;
  /** @generated */
  final int     casFeatCode_EndSpan;
  /** @generated */ 
  public int getEndSpan(int addr) {
        if (featOkTst && casFeat_EndSpan == null)
      jcas.throwFeatMissing("EndSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    return ll_cas.ll_getIntValue(addr, casFeatCode_EndSpan);
  }
  /** @generated */    
  public void setEndSpan(int addr, int v) {
        if (featOkTst && casFeat_EndSpan == null)
      jcas.throwFeatMissing("EndSpan", "edu.cmu.lti.bio.bdutt.types.Gene");
    ll_cas.ll_setIntValue(addr, casFeatCode_EndSpan, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Gene_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_Name = jcas.getRequiredFeatureDE(casType, "Name", "uima.cas.String", featOkTst);
    casFeatCode_Name  = (null == casFeat_Name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Name).getCode();

 
    casFeat_StartSpan = jcas.getRequiredFeatureDE(casType, "StartSpan", "uima.cas.Integer", featOkTst);
    casFeatCode_StartSpan  = (null == casFeat_StartSpan) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_StartSpan).getCode();

 
    casFeat_EndSpan = jcas.getRequiredFeatureDE(casType, "EndSpan", "uima.cas.Integer", featOkTst);
    casFeatCode_EndSpan  = (null == casFeat_EndSpan) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_EndSpan).getCode();

  }
}



    