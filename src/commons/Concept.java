package commons;

public class Concept {
	/**
	 * placeholder for each DB concept e.g., wikipedia bigram or sustainability and resilience related topic 
	 */
	public final int id;
	public final String text;
	public Concept(int id, String text) {
		this.id = id;
		this.text = text;
		//System.out.println(this.id + "_" + this.text);
	}
}
