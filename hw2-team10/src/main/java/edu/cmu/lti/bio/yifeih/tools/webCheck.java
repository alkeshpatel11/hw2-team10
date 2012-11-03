package edu.cmu.lti.bio.yifeih.tools;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 *wrap function of network checking.
 *input: phrase to search in onlione database
 *output: the input phrase is contained or not in data
 */
public class webCheck {

   public boolean  check(String text) throws IOException
   { 
     if(text.indexOf("%")!=-1||text.indexOf("\"")!=-1)
       return false;
     String urlString = "http://bergmanlab.smith.man.ac.uk:8081/?text="+text+"&species=9606,10090,10116,9031,562,4932,3702,7227,9913";
     System.out.println("checking "+ urlString);
     URL url = new URL(urlString);
     URLConnection conn = url.openConnection();
     InputStream is = conn.getInputStream() ;
     byte[] buffer = new byte[1024];  
     int len = -1;  
     if ((len = is.read(buffer)) != -1) {
       is.close();
       return true;
       
     }else{
       is.close();
       System.out.println("No");
       return false;
       
     }
     
   }
}

