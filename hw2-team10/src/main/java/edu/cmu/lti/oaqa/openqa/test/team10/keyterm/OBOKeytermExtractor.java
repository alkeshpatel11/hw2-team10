package edu.cmu.lti.oaqa.openqa.test.team10.keyterm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.biojava.ontology.Ontology;
import org.biojava.ontology.Term;
import org.biojava.ontology.io.OboParser;

public class OBOKeytermExtractor {

	public static void main(String[] args) {

		BufferedWriter bfw=null;
		try {
			bfw=new BufferedWriter(new FileWriter("log.log"));

			String fileName = "C:/Users/alkesh/Downloads/go4j1.1/gene_ontology_ext.obo";

			OboParser parser = new OboParser();
			InputStream inStream = new FileInputStream(fileName);

			BufferedReader oboFile = new BufferedReader(new InputStreamReader(
					inStream));
			Ontology ontology = parser.parseOBO(oboFile, "my Ontology name",
					"description of ontology");

			
			Set<Term> keys = ontology.getTerms();
			System.out.println(keys.size());
			//keys.add(new Term.Impl(ontology,"HER2 gene"));
			Iterator<Term> iter = keys.iterator();
			int count=0;
			while (iter.hasNext()) {
				Term term = (Term) iter.next();
				System.out.println("TERM: " + term.getName() + " "
						+ term.getDescription());
				
				//bfw.newLine();
				//bfw.write(term.getAnnotation().toString());
				//bfw.newLine();
				/*Object[] synonyms = term.getSynonyms();
				for (Object syn : synonyms) {
					System.out.println(syn.toString());
					//bfw.newLine();
				}*/
				//if(count++>50){
					//break;
				//}
			}
		} catch (Exception e) {
			try{
			e.printStackTrace();
			bfw.write(e.toString());
			bfw.close();
			}catch(Exception e1){
				
			}
			
		}
	}

}
