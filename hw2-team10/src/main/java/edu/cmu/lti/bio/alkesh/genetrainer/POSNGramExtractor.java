package edu.cmu.lti.bio.alkesh.genetrainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.bio.alkesh.comparators.CompGeneCount;
import edu.cmu.lti.bio.alkesh.customtypes.GeneCount;

import java.util.Comparator;

public class POSNGramExtractor {

	int count=0;
	HashMap<String, GeneCount> hshMap = new HashMap<String, GeneCount>();
	public static void main(String args[]) {
		try {
			POSNGramExtractor main = new POSNGramExtractor();

			main.extractGeneFromDB();
			main.extractGeneName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void extractGeneFromDB()throws Exception{
		BufferedReader bfr = new BufferedReader(new FileReader("/home/alkesh/Desktop/Semester1/F12-Software_Engineering_for_Information_Systems/Assignments/GeneDatabase.txt"));
		String text = "";
		char chars[] = new char[3072];
		while ((bfr.read(chars)) != -1) {
			text += new String(chars);
			chars = null;
			chars = new char[3072];
		}
		bfr.close();
		bfr = null;

		text = text.trim().replaceAll("\\);$","").trim();

		String rowText[]=text.split("\\),\\(");
		//BufferedWriter bfw=new BufferedWriter(new FileWriter("allGenes.txt"));
		for(int i=0;i<rowText.length;i++){
			
			String rec[]=rowText[i].split("[,]");
			if(rec.length<3){
				continue;
			}
			String key=rec[2].replace("'","").trim();
			//bfw.write(rec[2].replace("'","").trim());
			//bfw.newLine();
			//System.out.println(rec[2].trim());
			hshMap.put(key, new GeneCount(key,1));
			count++;
		}
		//bfw.close();
		//bfw=null;
		
		
	}
	public void extractGeneName() throws Exception {

		File files[] = new File(
				"/home/alkesh/Desktop/Semester1/F12-Software_Engineering_for_Information_Systems/Assignments/GENETAG/trained_genes")
				.listFiles();
		//int count = 0;
		//HashMap<String, GeneCount> hshMap = new HashMap<String, GeneCount>();

		for (int i = 0; i < files.length; i++) {
			BufferedReader bfr = new BufferedReader(new FileReader(files[i]));
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

			Pattern pattern = Pattern
					.compile("([\\S]+[/]NEWGENE[ 1-9]?){1,10}");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String geneTag = matcher.group();
				geneTag = geneTag.replace("/NEWGENE", "").trim();
				// System.out.println(geneTag);
				GeneCount val = hshMap.get(geneTag);
				if (val == null) {
					val = new GeneCount(geneTag, 1);
					count++;
				} else {
					val.setCount(val.getCount() + 1);
				}
				hshMap.put(geneTag, val);
				
			}
		}
		/*
		 * Iterator<String> it = hshMap.keySet().iterator(); while
		 * (it.hasNext()) { String key = it.next(); GeneCount val =
		 * hshMap.get(key);
		 * System.out.println(val.getGeneName()+"\t"+val.getCount()); }
		 */
		ArrayList<GeneCount> geneList = new ArrayList<GeneCount>(
				hshMap.values());
		Comparator<GeneCount> compCount = new CompGeneCount();
		Collections.sort(geneList, compCount);
		BufferedWriter bfw=new BufferedWriter(new FileWriter("allGenes.txt"));
		for (int i = 0; i < geneList.size(); i++) {
			System.out.println(geneList.get(i).getGeneName() + "\t"
					+ geneList.get(i).getCount());
			bfw.write(geneList.get(i).getGeneName()+"\t"+ geneList.get(i).getCount());
			bfw.newLine();
		}
		bfw.close();
		bfw=null;

		System.out.println("###Count: " + count);

	}

}
