package edu.cmu.lti.bio.alkesh.tools;

import java.io.BufferedReader;
import java.io.FileReader;

public class Test {

	public static void main(String args[]){
		try{
			Test test=new Test();
			BufferedReader bfr=new BufferedReader(new FileReader("C:/Users/alkesh/Desktop/9182672_.txt"));
			String str;
			String text="";
			while((str=bfr.readLine())!=null){
				text+=str;
			}
			bfr.close();
			
			System.out.println(text.length());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
