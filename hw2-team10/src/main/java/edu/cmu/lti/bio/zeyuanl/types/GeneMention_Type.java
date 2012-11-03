
/* First created by JCasGen Sun Oct 07 00:32:25 EDT 2012 */
package edu.cmu.lti.bio.zeyuanl.types;

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
 * Updated by JCasGen Wed Oct 10 21:43:50 EDT 2012
 * @generated */
public class GeneMention_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (GeneMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = GeneMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new GeneMention(addr, GeneMention_Type.this);
  			   GeneMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new GeneMention(addr, GeneMention_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = GeneMention.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.lti.cmu.hw1.typesys.GeneMention");
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "edu.lti.cmu.hw1.typesys.GeneMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "edu.lti.cmu.hw1.typesys.GeneMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "edu.lti.cmu.hw1.typesys.GeneMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "edu.lti.cmu.hw1.typesys.GeneMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_tag;
  /** @generated */
  final int     casFeatCode_tag;
  /** @generated */ 
  public String getTag(int addr) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "edu.lti.cmu.hw1.typesys.GeneMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tag);
  }
  /** @generated */    
  public void setTag(int addr, String v) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "edu.lti.cmu.hw1.typesys.GeneMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_tag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_tokenArr;
  /** @generated */
  final int     casFeatCode_tokenArr;
  /** @generated */ 
  public int getTokenArr(int addr) {
        if (featOkTst && casFeat_tokenArr == null)
      jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr);
  }
  /** @generated */    
  public void setTokenArr(int addr, int v) {
        if (featOkTst && casFeat_tokenArr == null)
      jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_tokenArr, v);}
    
   /** @generated */
  public String getTokenArr(int addr, int i) {
        if (featOkTst && casFeat_tokenArr == null)
      jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr), i);
  }
   
  /** @generated */ 
  public void setTokenArr(int addr, int i, String v) {
        if (featOkTst && casFeat_tokenArr == null)
      jcas.throwFeatMissing("tokenArr", "edu.lti.cmu.hw1.typesys.GeneMention");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokenArr), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public GeneMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_tag = jcas.getRequiredFeatureDE(casType, "tag", "uima.cas.String", featOkTst);
    casFeatCode_tag  = (null == casFeat_tag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tag).getCode();

 
    casFeat_tokenArr = jcas.getRequiredFeatureDE(casType, "tokenArr", "uima.cas.StringArray", featOkTst);
    casFeatCode_tokenArr  = (null == casFeat_tokenArr) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tokenArr).getCode();

  }
}



    