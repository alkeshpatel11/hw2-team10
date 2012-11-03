package edu.cmu.lti.bio.alkesh.genetrainer;

import edu.cmu.lti.bio.alkesh.comparators.CompGeneName;
import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SearchForNGram {

	ArrayList<GeneCount> nGramList = new ArrayList<GeneCount>();
	
	
	public static void main(String args[]) {

		try {
			SearchForNGram main = new SearchForNGram();
			main.nGramList = main.loadGeneNGrams("/home/alkesh/Desktop/Semester1/F12-Software_Engineering_for_Information_Systems/Assignments/hw1-alkeshku/allGenes-sorted-weight.txt");
			Comparator<GeneCount>compGeneName=new CompGeneName();
			Collections.sort(main.nGramList,compGeneName);
			
			BufferedWriter bfw=new BufferedWriter(new FileWriter("/home/alkesh/Desktop/Semester1/F12-Software_Engineering_for_Information_Systems/Assignments/hw1-alkeshku/allGenes-sorted.txt"));
			for (int i = 0; i < main.nGramList.size(); i++) {
				bfw.write(main.nGramList.get(i).getGeneName()+"\t"+ main.nGramList.get(i).getCount());
				bfw.newLine();
			}
			bfw.close();
			bfw=null;
						
			GeneCount key=new GeneCount("y19d2b",1);
			int foundIdx=Collections.binarySearch(main.nGramList, key, compGeneName);
			System.out.println(foundIdx);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<GeneCount>loadGeneNGrams(String fileName) throws Exception {
		BufferedReader bfr = new BufferedReader(new FileReader(fileName));
		String text = "";
		char chars[] = new char[3072];
		while ((bfr.read(chars)) != -1) {
			text += new String(chars);
			chars = null;
			chars = new char[3072];
		}
		bfr.close();
		bfr = null;

		text = text.trim();

		String rowText[] = text.split("[\\n]");

		ArrayList<GeneCount> geneList = new ArrayList<GeneCount>();
		for (int i = 0; i < rowText.length; i++) {

			String rec[] = rowText[i].split("[\\t]");
			if (rec.length < 2) {
				continue;
			}
			String key = rec[0].replace("'", "").trim();
			Integer val=Integer.parseInt(rec[1]);
			geneList.add(new GeneCount(key, val));

		}
		return geneList;
	}
}
