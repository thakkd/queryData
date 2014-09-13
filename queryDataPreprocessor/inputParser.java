package queryDataPreprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class inputParser {
	
public static void main(String[] args) {
	BufferedReader reader = null;
	
	try{
	FileInputStream file = new FileInputStream("data2");
	//FileOutputStream oFile = new FileOutputStream("data2.json");
	PrintWriter writer = new PrintWriter("data2.json", "UTF-8");
	
	reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
	
	  while (true) {
	    String line = reader.readLine();
	    if (line == null) break;
	    String[] fields = line.split("\t");
	    //System.out.println(fields[0]);
	    String[] words = fields[0].split(" ");
	    String output = new String("{ text: ");
	    
	    output += "\"" + fields[0]+ "\", ";
	    
	    output += "score : " + fields[1];
	    for(int ctr=0;ctr < words.length; ctr++){
	    	output += ", w"+ ctr+":" + "\"" + words[ctr]+ "\"";
	    }
	    output += "}";	
	    System.out.print(".");
	    writer.println(output);
	    //System.out.println(fields[1]);
	    // process fields here
	  }
	writer.close();
	}
	catch (IOException ex) {
		ex.printStackTrace();
	}
	finally {
			try {
				if (reader != null) reader.close();
				
			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}		  
	}
	
	
	
}

}
