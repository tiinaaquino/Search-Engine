/**
 * This class is responsible for storing basic search
 * information.
 */

public class SearchResult implements Comparable<SearchResult> {
	
	/** Path of the query(s). */
	private final String path;
		
	/** Frequency of the query(s). */
	private int frequency;
	
	/** Position of the query(s). */
	private int firstPosition;
	
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
	public SearchResult(String path, int frequency, int firstPosition) {
		this.path = path;
		this.frequency = frequency;
		this.firstPosition = firstPosition;
	}
	
	/**
	 * Get the frequency of the search result
	 * 
	 * @return frequency of the result
	 */
	public int getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Get the position of the search result
	 * 
	 * @return position of the result
	 */
	public int getPosition() {
		return this.firstPosition;
	}
	
	/**
	 *  Get the path of the search result
	 *  
	 * @return path of the result
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 *  Updates the query with the given frequency and position.
	 *  
	 * @param frequency
	 * 			frequency to update
	 * @param position
	 * 			position to update
	 */
	public void update(int frequencyToUpdate, int positionToUpdate) {
		if (positionToUpdate < firstPosition) {
			firstPosition = positionToUpdate;
		}
		frequency += frequencyToUpdate;
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
			if (this.firstPosition == other.firstPosition) {
				return this.path.compareTo(other.path);
			}
			return Integer.compare(this.firstPosition, other.firstPosition);
		}
		return Integer.compare(other.frequency, this.frequency);
	}
	
	/**
	 * Returns the search result as a string.
	 */
	@Override
	public String toString() {
		return ("Path: " + this.path + "Count: " + this.frequency + "Position: " + this.firstPosition);
	}
 
}