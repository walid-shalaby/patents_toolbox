package topic_crawler;

import java.util.ArrayList;

import commons.Concept;
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

enum TopicLookupModeEnum {
	e_UNKNOWN,
	e_TITLE, /* search for topic text inside patent title only */
	e_ABSTRACT, /*search for topic text inside patent abstract only */
	e_TITLE_ABSTRACT /*search for topic text inside patent title and abstract */
}
public class TopicCrawler {

	static String datasrcPath = "";
	static PatentsLookupModeEnum patentsLookupMode = PatentsLookupModeEnum.e_UNKNOWN;
	static TopicLookupModeEnum topicLookupMode = TopicLookupModeEnum.e_UNKNOWN;
	static String patentsSrcPath = "";
	static final String datasrcPattern = "--topics-db";
	static final String patentsLookupModePattern = "--patents-lookup-mode";
	static final String topicLookupModePattern = "--topic-lookup-mode";
	static final String patentsSrcPattern = "--patents-src";
	
	// given patent lookup mode as string in commandline return corresponding enum value
	static PatentsLookupModeEnum GetPatentLookupMode(String lookupMode) {
		if(lookupMode.compareTo("index")==0)
			return PatentsLookupModeEnum.e_INDEX;
		else if(lookupMode.compareTo("db")==0)
			return PatentsLookupModeEnum.e_DB;
		else
			return PatentsLookupModeEnum.e_UNKNOWN;
	}		
		
	// given lookup mode as string in commandline return corresponding enum value
	static TopicLookupModeEnum GetTopicLookupMode(String lookupMode) {
		if(lookupMode.compareTo("title")==0)
			return TopicLookupModeEnum.e_TITLE;
		else if(lookupMode.compareTo("abstract")==0)
			return TopicLookupModeEnum.e_ABSTRACT;
		else if(lookupMode.compareTo("title_abstract")==0)
			return TopicLookupModeEnum.e_TITLE_ABSTRACT;
		else
			return TopicLookupModeEnum.e_UNKNOWN;
	}
	
	static boolean parseArgs(String[] args) {
		boolean result = false;
		if(args.length==8) {
			for(int i=0; i<args.length; i++) {
				if(args[i].compareTo(datasrcPattern)==0 && i+1<args.length)
					datasrcPath = args[++i];
				
				if(args[i].compareTo(patentsLookupModePattern)==0 && i+1<args.length)
					patentsLookupMode = GetPatentLookupMode(args[++i]);
				
				if(args[i].compareTo(topicLookupModePattern)==0 && i+1<args.length)
					topicLookupMode = GetTopicLookupMode(args[++i]);
				
				if(args[i].compareTo(patentsSrcPattern)==0 && i+1<args.length)
					patentsSrcPath = args[++i];				
			}
			if(!datasrcPath.isEmpty() && 
					patentsLookupMode!=PatentsLookupModeEnum.e_UNKNOWN &&
					topicLookupMode!=TopicLookupModeEnum.e_UNKNOWN && 
					!patentsSrcPath.isEmpty())
				result = true;
		}
		if(result==false)
			System.out.println(printUsage());
		return result;
	}
	static String printUsage() {
		String usage = "Usage: java -jar topic_crawler.jar " + datasrcPattern + " \"path\" " + topicLookupModePattern + " title|abstract|title_abstract " + patentsLookupModePattern + " db|index " + patentsSrcPattern + " \"path\"";
		return usage;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(parseArgs(args)) {
			System.out.println("topic DB path: "+datasrcPath);
			System.out.println("patents lookup path: "+patentsSrcPath);
			System.out.println("patents lookup mode: "+patentsLookupMode.toString());
			System.out.println("topics lookup mode: "+topicLookupMode.toString());
			
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
				for(Concept t : topics) {
					// search for topic at source index
					ArrayList<Patent> patents = searcher.search(t, topicLookupMode);
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
