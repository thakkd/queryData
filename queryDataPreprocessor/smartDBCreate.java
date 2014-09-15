package queryDataPreprocessor;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.bson.BSON;
import org.bson.BSONObject;

import static java.util.concurrent.TimeUnit.SECONDS;

public class smartDBCreate {


	public static void main (String[] args) {	
		
		int totalWordsCollections = 2;
		int keepTopNRecords = 5;
		
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient( "localhost" , 27017 );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		DB db = mongoClient.getDB( "queryData2" );
		
		
		// We have a connection to mongo here
		//Now start to read input file and populate mongo
		BufferedReader reader = null;
		// exit if the input filename is not specified. Not much error checking on the input format
		if(args.length < 1){
			System.out.println("Specify the input data file as command line argument");
			return;
		}

		try{
			FileInputStream file = new FileInputStream(args[0]);
			reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));

//			DBCollection coll_p1 = db.getCollection("p1");
//			DBCollection coll_p2 = db.getCollection("p2");
//			DBCollection coll_p3 = db.getCollection("p3");
//			DBCollection coll_p4 = db.getCollection("p4");
//			DBCollection coll_p5 = db.getCollection("p5");
			
			DBCollection coll_w[] = {null, null, null, null, null};
			
			for (int ctr = 0; ctr < totalWordsCollections; ctr++){
				coll_w[ctr] = db.getCollection("w"+ctr);
			}
			
			while (true) {
				String line = reader.readLine();
				if (line == null) break; //end of file returns null
				//split the line into the text and score
				//System.out.println(".");
				String[] fields = line.split("\t");
				DBObject bson = null;
				String text = fields[0];
				String score = fields[1];
				try{
					bson = (DBObject)JSON.parse("{ \""+score+"\" : \""+text+"\"}");
				}catch(JSONParseException ex){
					System.out.print("|||");
					continue;
				}
				//System.out.println(fields[0]);
				//Now split the text on spaces to create json fields w0 (1st word) w1 (2nd word) so on and so forth
				String[] words = fields[0].split(" ");

				//For now store the parts of the first word in different collections
				// so P1 is the collection of top 5 (by score) suggestions for that character of the word
				// Similarly P2 is a collection of top 5 suggestions for that combination of the first 2 characters of the word
				// For now we wont care about any words bigger than 5 characters ... so in this case just return the results based on 
				// the first 5 characters
				for(int word_num=1; word_num < words.length && word_num < 2; word_num++){
					// check w1 collection to see if we have an entry with this word as the key
					// If not insert it in
					// If yes insert this one and then make sure we keep only the top 5  (score wise)
					BasicDBObject query = new BasicDBObject("word",words[word_num]);

					DBCursor cursor = coll_w[word_num].find(query);
					
					
					if (cursor.hasNext())
					{
							DBObject record = cursor.next();
							HashMap map = (HashMap) record.toMap();
							
							DBObject list = (DBObject)map.get("lines");
							
							
							
							list.put((new Integer(list.keySet().size()).toString()),bson);
							
							if(list.keySet().size() > keepTopNRecords){
								Integer lowestScore = Integer.MAX_VALUE;
								int lowIndex = Integer.MAX_VALUE;
								
								for(int j = 0; j <= keepTopNRecords; j++){
										DBObject row = (DBObject)list.get(new Integer(j).toString());
										Integer currScore = new Integer(row.toMap().keySet().iterator().next().toString());
										
										if(currScore < lowestScore ){
											lowIndex = j;
											lowestScore = currScore;
										}
								}
									
								list.removeField(new Integer(lowIndex).toString());
							}
							
							
							
							
							//
							//coll_w[0].update(record.id
							//BasicDBObject thisText = new BasicDBObject();
							//thisText.put(text, score);
							
							//BasicDBObject update = new BasicDBObject();
							//update.put("$push", new BasicDBObject("lines",thisText));
							
							//coll_w[0].update(query,update,true,true);
							coll_w[word_num].save(record);
							//System.out.println(coll_w[word_num].toString());
							//System.out.println(record.toString());
					}
					else
					{
						try{
							bson = (DBObject)JSON.parse("{ \"word\" : \"" + words[word_num] + "\", \"lines\" : [ { \""+score+"\" : \""+text+"\"} ] }");
						}catch(JSONParseException ex){
							System.out.print("|||");
							continue;
						}
						coll_w[word_num].insert(bson);
						
						
					}

					
					cursor.close();
					
					
				}

			}
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
