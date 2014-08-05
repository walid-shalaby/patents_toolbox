package topic_crawler;

import java.util.ArrayList;

import commons.Patent;

/**
 * @author walid-shalaby
 *
 */
enum PatentsLookupModeEnum {
	e_UNKNOWN,
	e_DB,
	e_INDEX
}
public class TopicCrawler {

	static String datasrcPath = "";
	static PatentsLookupModeEnum patentsLookupMode = PatentsLookupModeEnum.e_UNKNOWN;
	static String patentsSrcPath = "";
	static final String datasrcPattern = "--topics-db";
	static final String patentsLookupModePattern = "--patents-lookup-mode";
	static final String patentsSrcPattern = "--patents-src";
	
	// given lookup mode as string in commandline return corresponding enum value
	static PatentsLookupModeEnum GetLookupMode(String lookupMode) {
		if(lookupMode.compareTo("index")==0)
			return PatentsLookupModeEnum.e_INDEX;
		else if(lookupMode.compareTo("db")==0)
			return PatentsLookupModeEnum.e_DB;
		else
			return PatentsLookupModeEnum.e_UNKNOWN;
	}
	
	static boolean parseArgs(String[] args) {
		boolean result = false;
		if(args.length==6) {
			for(int i=0; i<args.length; i++) {
				if(args[i].compareTo(datasrcPattern)==0 && i+1<args.length)
					datasrcPath = args[++i];
				
				if(args[i].compareTo(patentsLookupModePattern)==0 && i+1<args.length)
					patentsLookupMode = GetLookupMode(args[++i]);
				
				if(args[i].compareTo(patentsSrcPattern)==0 && i+1<args.length)
					patentsSrcPath = args[++i];				
			}
			if(!datasrcPath.isEmpty() && 
					patentsLookupMode!=PatentsLookupModeEnum.e_UNKNOWN && 
					!patentsSrcPath.isEmpty())
				result = true;
		}
		if(result==false)
			System.out.println(printUsage());
		return result;
	}
	static String printUsage() {
		String usage = "Usage: java -jar topic_indexer.jar " + datasrcPattern + " \"path\" " + patentsLookupModePattern + " db|index " + patentsSrcPattern + " \"path\"";
		return usage;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(parseArgs(args)) {
			System.out.println("topic DB path: "+datasrcPath);
			System.out.println("patents lookup path: "+patentsSrcPath);
			System.out.println("patents lookup mode: "+patentsLookupMode.toString());
			
			// load target topics from DB
			Topics topics = new Topics(datasrcPath);
			topics.load();
			
			TopicSearcher searcher = null;
			
			switch(patentsLookupMode) {
				case e_DB:
					// search for topic matching patents in the DB and write them to DB
					searcher = new TopicDBSearcher(patentsSrcPath);
					break;
				case e_INDEX:
					// search for topic matching patents in the main index and write them to DB
					searcher = new TopicIndexSearcher(patentsSrcPath);
					break;
				default:
					break;
			}
			if(searcher!=null) {
				for(Topic t : topics) {
					// search for topic at source index
					ArrayList<Patent> patents = searcher.search(t);
					if(patents!=null) {
						// write search results to DB
						topics.writePatents(patents);
					}
					else System.out.println("Searching for topic:\"" + t.text + "\" resulted in 0 patents...");				
				}
			}
			System.out.println("Done!");
		}
		
	}
}
