package edu.cmu.lti.bio.alkesh.casconsumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceProcessException;
import org.xml.sax.SAXException;

import edu.cmu.lti.bio.alkesh.types.*;

public class GeneCasConsumer extends CasConsumer_ImplBase {

	int mDocNum;
	File mOutputFile = null;
	BufferedWriter bfw = null;
	double THRESHOLD = 4.0;

	@Override
	public void initialize() {

		mDocNum = 0;
		try {
			mOutputFile = new File(
					(String) getConfigParameterValue("OUTPUT_FILE"));

			bfw = new BufferedWriter(new FileWriter(mOutputFile));
			THRESHOLD = Double
					.parseDouble((String) getConfigParameterValue("THRESHOLD"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void processCas(CAS aCAS) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		// retreive the gene names list from the CAS
		FSIterator it = jcas.getAnnotationIndex(GeneTagList.type).iterator();

		GeneTagList geneLoc = null;
		if (it.hasNext()) {
			geneLoc = (GeneTagList) it.next();

		}
		// System.out.println("Consumed: "+geneTag);

		try {
			writeIntoFile(geneLoc);
		} catch (IOException e) {
			throw new ResourceProcessException(e);
		} catch (SAXException e) {
			throw new ResourceProcessException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeIntoFile(GeneTagList geneTagList) throws Exception {
		String id = geneTagList.getId();
		String sentenceText = geneTagList.getText();
		FSList fsList = geneTagList.getGeneList();

		// System.out.println("Sentence Id: " + geneTagList.getId());
		int i = 0;
		while (true) {

			GeneTag geneTag = null;
			try {
				geneTag = (GeneTag) fsList.getNthElement(i);
			} catch (Exception e) {
				break;
			}

			if (geneTag.getScore() > THRESHOLD) {
				int start = geneTag.getStart();
				int end = geneTag.getEnd();
				String geneName = sentenceText.substring(start, end);

				int countBegin = nSpaces(sentenceText, start);
				int countEnd = nSpaces(sentenceText, end);
				bfw.write(id + "|" + (start - countBegin) + " "
						+ (end - countEnd - 1) + "|" + geneName + "\t"
						+ geneTag.getScore());
				bfw.newLine();

			}
			i++;
		}

		// bfw.flush();
	}

	/**
	 * Adjusts the offsets of annotation by removing whitespace
	 * @param orgSentence
	 * @param beforeIdx
	 * @return
	 */
	public int nSpaces(String orgSentence, int beforeIdx) {

		String sentence = orgSentence.substring(0, beforeIdx);
		int count = 0;
		while (sentence.indexOf(" ") > -1) {
			sentence = sentence.replaceFirst(" ", "");
			count++;
		}
		return count;

	}

	/**
	 * Closes the file and other resources initialized during the process
	 * 
	 */
	@Override
	public void destroy() {

		try {
			if (bfw != null) {
				bfw.close();
				bfw = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
