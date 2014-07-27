package topic_indexer;

/**
 * @author walid-shalaby
 *
 */
public class TopicIndexer {

	static String datasrcPath = "";
	static String indexPath = "";
	static final String datasrcPattern = "--topics-db";
	static final String indexPattern = "--patents-index";
	
	static boolean parseArgs(String[] args) {
		boolean result = false;
		if(args.length==4) {
			for(int i=0; i<args.length; i++) {
				if(args[i].compareTo(datasrcPattern)==0 && i+1<args.length)
					datasrcPath = args[++i];
				
				if(args[i].compareTo(indexPattern)==0 && i+1<args.length)
					indexPath = args[++i];
			}
			if(!indexPath.isEmpty() && !datasrcPath.isEmpty())
				result = true;
		}
		if(result==false)
			System.out.println(printUsage());
		return result;
	}
	static String printUsage() {
		String usage = "Usage: java -jar topic_indexer.jar " + datasrcPattern + " path " + indexPattern + " path";
		return usage;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(parseArgs(args)) {
			System.out.println("topic DB path: "+datasrcPath);
			System.out.println("patents index path: "+indexPath);
			
			// load target topics from DB
			Topics topics = new Topics(datasrcPath);
			topics.load();
			
			// search for topic matching patents in the main index and write them to DB 
			TopicSearcher searcher = new TopicSearcher(indexPath);
			//TopicSearcher searcher = new TopicSearcher("/home/wshalaby/work/patents/semantic-search-with-sustainability-patents/data/indexForReselience_prob-sol");
			
			for(Topic t : topics) {
				// search for topic at source index
				IndexPatent[] patents = searcher.search(t);
				if(patents!=null) {
					// write search results to DB
					topics.writePatents(patents);
				}
				else System.out.println("Searching for topic:\"" + t.text + "\" resulted in 0 patents...");				
			}
			System.out.println("Done!");
		}
		
	}

}
