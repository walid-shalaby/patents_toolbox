package topic_indexer;

import java.util.ArrayList;

import commons.Patent;


/**
 * @author walid-shalaby
 *
 */
public interface TopicSearcher {
	
	public ArrayList<Patent> search(Topic topic);
}
