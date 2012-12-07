package edu.cmu.lti.bio.alkesh.genetrainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;

@SuppressWarnings("deprecation")
public class NGramLuceneWrapper {

	String NGRAM_FILE = "allGenes-sorted.txt";
	String INDEX_DIRECTORY = "data/index";
	

	
	public static void main(String args[]) {
		try {
			// String currDir=new File(".").getAbsolutePath();
			// System.out.println(currDir);
			NGramLuceneWrapper main = new NGramLuceneWrapper();
			//main.createIndex();
			ArrayList<GeneCount> geneList = main
					.searchIndex("MMS2",100);
			for(int i=0;i<geneList.size();i++){
				System.out.println(geneList.get(i).getGeneName()+"\t"+geneList.get(i).getCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the index of all N-Grams extracted from corpus
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	
	@SuppressWarnings("deprecation")
	public void createIndex() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);

		@SuppressWarnings("deprecation")
		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(
				INDEX_DIRECTORY)), analyzer, MaxFieldLength.UNLIMITED);
		File nGramFile = new File(NGRAM_FILE);
		BufferedReader bfr = new BufferedReader(new FileReader(nGramFile));
		String nGram;

		int lineNo = 0;
		while ((nGram = bfr.readLine()) != null) {
			if (nGram.trim().equals("")) {
				continue;
			}

			String nGrams[] = nGram.split("[\\t]");

			if (nGrams.length < 2) {
				System.out.println(nGram);
				continue;
			}

			nGrams[0] = nGrams[0].replace(">< ", "").trim();
			// double freq=Double.parseDouble(nGrams[1]);

			Document document = new Document();

			String nGramId = String.valueOf(lineNo);
			document.add(new Field("id", nGramId, Field.Store.YES,
					Field.Index.NOT_ANALYZED));

			document.add(new Field("ngram", nGrams[0], Field.Store.YES,
					Field.Index.ANALYZED));

			document.add(new Field("freq", nGrams[1], Field.Store.YES,
					Field.Index.ANALYZED));

			indexWriter.addDocument(document);
			lineNo++;
			if (lineNo % 100 == 0) {
				System.out.println("Processed: " + lineNo);
			}
		}
		indexWriter.optimize();
		indexWriter.close();
		bfr.close();
		bfr = null;
	}

	/**
	 * Search for the query in lucene index
	 * @param searchString
	 * @param n
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public ArrayList<GeneCount> searchIndex(String searchString,int n)
			throws IOException, ParseException {

		System.out.println("Searching for '" + searchString + "'");
		Directory directory = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		QueryParser queryParser = new QueryParser(Version.LUCENE_36, "ngram",
				analyzer);
		Query query = queryParser.parse(searchString);
		TopDocs hits = indexSearcher.search(query, n);
		//System.out.println("Number of hits: " + hits.totalHits);
		ScoreDoc scoreDocs[] = hits.scoreDocs;
		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("ngram");
		fields.add("freq");
		FieldSelector fieldSelector = new MapFieldSelector(fields);
		ArrayList<GeneCount> result = new ArrayList<GeneCount>();

		for (int i = 0; i < scoreDocs.length; i++) {
			int docID = scoreDocs[i].doc;
			Document doc = indexSearcher.doc(docID, fieldSelector);
			double score = scoreDocs[i].score;

			Fieldable nGram = doc.getFieldable("ngram");

			if (nGram == null) {
				continue;
			}
			Fieldable freqStr = doc.getFieldable("freq");
			double freq = 1.0;
			if (freqStr == null) {
				continue;
			}
			freq = Double.parseDouble(freqStr.stringValue());
			result.add(new GeneCount(nGram.stringValue(), score));
			//System.out.println(nGram + "\t" + freq + "\t" + score);
			// System.out.println(doc.getFieldable("id")+"\t"+doc.getFieldable("ngram")+"\t"+score);
		}
		indexReader.close();
		indexSearcher.close();
		return result;
	}

}
