package edu.cmu.lti.bio.alkesh.collectionreaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import edu.cmu.lti.bio.alkesh.customtypes.SentenceInfo;
import edu.cmu.lti.bio.alkesh.types.Sentence;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
/**
 * 
 * @author alkesh
 *
 *GeneFileReader reads the input file and convert each sentence in CAS
 */
public class GeneFileReader extends CollectionReader_ImplBase {

	String mEncoding = null;
	String mLanguage = null;
	int mCurrentSentenceNo = 0;
	ArrayList<SentenceInfo> mSentences = null;

	@Override
	public void initialize() throws ResourceInitializationException {

		File file = new File((String) getConfigParameterValue("INPUT_FILE"));				

		// get list of sentences from the input file
		mSentences = new ArrayList<SentenceInfo>();
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new FileReader(file));
			String str;
			while ((str = bfr.readLine()) != null) {
				String rec[] = str.trim().split("[ ]");
				if (rec.length < 2) {
					continue;
				}
				String id = rec[0];// Separating sentence id from line
				String text = str.replace(id, "").trim();

				mSentences.add(new SentenceInfo(id, text));

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		} finally {
			if (bfr != null) {
				try {
					bfr.close();
				} catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
				bfr = null;
			}
		}

	}

	
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}

		// open input sentence list iterator
		SentenceInfo sentence = (SentenceInfo) mSentences
				.get(mCurrentSentenceNo++);
		//System.out.println("Read: "+sentence.getText());
		String text=sentence.getText();
		try {
			byte[] contents = new byte[(int) sentence.getText().length()];
		
			if (mEncoding != null) {
				text = new String(contents, mEncoding);
			} else {
				text = new String(contents);
			}
			// put document in CAS
			jcas.setDocumentText(text);			
		} finally {

		}
		
		// set language if it was explicitly specified
		// as a configuration parameter
		if (mLanguage != null) {
			((DocumentAnnotation) jcas.getDocumentAnnotationFs())
					.setLanguage(mLanguage);
		}

	
		Sentence sent=new Sentence(jcas);
		
		sent.setId(sentence.getId());
		sent.setText(sentence.getText());
		sent.addToIndexes();
	}

	/**
	 * Closes the file and other resources initialized during the process
	 * 
	 */

	@Override
	public void close() throws IOException {
		if(mSentences!=null){
			mSentences.clear();
			mSentences=null;
		}
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(mCurrentSentenceNo,
				mSentences.size(), Progress.ENTITIES) };
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return mCurrentSentenceNo < mSentences.size();
	}

}
