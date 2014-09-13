package queryDataPreprocessor;

// Parser to preprocess the input dataset and output to a JSON dataset that could be directly
// imported into MongoDB.
// Arg0: Input filename which contains the data as follows:
// why are sims 3 sims so ugly	12
// why do we have armpits	7
// Output with a file name as inputfile.json is in a json format as follows:
//{ text: "why are sims 3 sims so ugly", score : 12, w0:"why", w1:"are", w2:"sims", w3:"3", w4:"sims", w5:"so", w6:"ugly"}
//{ text: "why do we have armpits", score : 7, w0:"why", w1:"do", w2:"we", w3:"have", w4:"armpits"}
//
//This output file can be imported into mongodb with the following command
//mongoimport --db queryData --collection import1 --type json --file data.json 
// Note that the data file has some rows with words that have quotes (") 
// those rows do not import correctly. For this excercise ignoring that 

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class inputParser {

	public static void main(String[] args) {
		BufferedReader reader = null;
		// exit if the input filename is not specified. Not much error checking on the input format
		if(args.length < 1){
			System.out.println("Specify the input data file as command line argument");
			return;
		}

		try{
			FileInputStream file = new FileInputStream(args[0]);
			//FileOutputStream oFile = new FileOutputStream("data2.json");
			//Output file name is inputfilename.json
			PrintWriter writer = new PrintWriter(args[0]+".json", "UTF-8");

			reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));

			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				//split the line into the text and score
				String[] fields = line.split("\t");
				//System.out.println(fields[0]);
				//Now split the text on spaces to create json fields w0 (1st word) w1 (2nd word) so on and so forth
				String[] words = fields[0].split(" ");
				//Output string holds the collection of JSON outputs
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
