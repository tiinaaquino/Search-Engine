import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.file.Path;

/**
 * This class is responsible for storing basic search
 * information.
 */

public class SearchResult implements Comparable<SearchResult>{
	
	/** Path of the query(s). */
	private final String path;
	
	/** Frequency of the query(s). */
	private int frequency;
	
	/** Position of the query(s). */
	private int position;
	
	/** Location of the query(s). */
	private String location;
	
	/**
	 * Initializes a searchResult from the provided parameters.
	 *
	 * @param path
	 *            path where the query word(s) is/ are stored
	 * @param frequency
	 *            locations where the query word(s) are more frequent
	 * @param position
	 *            position of the query word(s)
	 */
	public SearchResult(String path, int frequency, int position) {
		this.path = path;
		this.frequency = frequency;
		this.position = position;
	}
	
	/**
	 * Get the frequency of the search result
	 * 
	 * @return frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * Get the position of the search result
	 * 
	 * @return position
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 *  Updates the query with the given frequency and position.
	 *  
	 * @param frequency
	 * 			frequency to update
	 * @param position
	 * 			position to update
	 */
	public void update(int frequency, int position) {
		this.frequency += frequency;
		if (this.position > position) {
			this.position = position;
		}
	}
	
	/**
	 * Compares the given strings.
	 * 
	 * @param other
	 * 			the SearchResult to compare to
	 * @return an int value from the comparison
	 * 		
	 * @see String#compareTo(String)
	 * @see Integer#compare(int, int)
	 */
	@Override
	public int compareTo(SearchResult other) {
		if (this.frequency == other.frequency) {
			if (this.position == other.position) {
				return this.path.compareTo(other.path);
			}
			return Integer.compare(this.position, other.position);
		}
		return Integer.compare(other.frequency, this.frequency);
	}
	
	/**
	 * Returns the search result as a string.
	 */
	@Override
	public String toString() {
		return ("\nwhere: " + location + "\ncount: " + frequency + "\nindex: " + position);
	}
 
}