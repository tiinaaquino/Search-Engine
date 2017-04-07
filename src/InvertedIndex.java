import java.io.File; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store paths, strings, and their positions.
 *
 */
public class InvertedIndex {
	
	/**
	 * Data structure variable to store input.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	/**
	 * Constructor
	 * 
	 * Initializes the index
	 */
	public InvertedIndex(){
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}
	
	/**
	 * Returns number of words in the index.
	 * 
	 * @return number of words
	 */
	public int numWords() {
		return index.size();
	}
	
	/**
	 * 
	 * @param word
	 * 			word to search for
	 * @return number of locations the word was 
	 *		   found in
	 */
	public int numLocations(String word) {
		int count = 0;
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : index.entrySet()) {
			if (entry.equals(word)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @param word
	 * @param location
	 * @return
	 */
	public int numPositions(String word, String location) {
		int positionCount = 0;
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : index.entrySet()) {
			if (entry.equals(word) && entry.equals(location)) {
				positionCount++;
			}
		}
		return positionCount;
	}
	
	/**
	 * Returns whether the word is in the index.
	 * 
	 * @param word
	 * 				word to find
	 * @return true if the word is in the index,
	 * otherwise false.
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * Returns whether the word is in the index at a 
	 * specific location.
	 * 
	 * @param word
	 * 				word to find
	 * @param location
	 * 				location to find word in
	 * @return true is the word is in the index at the
	 * 		   location, otherwise false.
	 */
	public boolean contains(String word, String location) {
		return index.containsKey(word) && index.get(word).containsKey(location);
	}
	
	/**
	 * Returns whether the word is in the index at a
	 * specific location and certain word position.
	 * 
	 * @param word
	 * 				word to find
	 * @param location
	 * 				location to find word in
	 * @param wordPosition
	 * 				word position to find
	 * @return true if the word is in the index at the
	 * 		   location and word position, otherwise false.
	 */
	public boolean contains (String word, String location, int wordPosition) {
		return index.containsKey(word) && index.get(word).containsKey(location) && index.get(word).containsValue(wordPosition);
	}
	
	/**
	 * Returns a copy of the words in this index as a sorted list.
	 *
	 * @return sorted list of words
	 *
	 * @see ArrayList#ArrayList(java.util.Collection)
	 * @see Collections#sort(List)
	 */
	public List<String> copyWords() {
		return new ArrayList<String>(index.keySet());
	}
		
	/**
	 * Stores the word, its path, and word position
	 * to the index
	 * 
	 * @param word
	 * 				word to store
	 * @param path
	 * 				path to store
	 * @param wordPosition
	 * 				word position to store
	 */
	public void add(String word, String path, int wordPosition) {
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<>());
		}
		
		if (!index.get(word).containsKey(path)) {
			index.get(word).put(path, new TreeSet<>());
		}
		
		index.get(word).get(path).add(wordPosition);
	}
	
	/**
	 * Adds array of words all at once, implementing add method
	 * 
	 * @param words
	 * 				array of words to add
	 * @param path
	 */
	public void addAll(String[] words, Path path) {
		int position = 1;
		for (String i : words) {
			add(i, path.toString(), position);
			position++;
		}
	}
	
	/**
	 * Calls "asNestedOject" of the JSONWriter class to convert object 
	 * to JSON object.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void asJSON(Path path) throws IOException {
		JSONWriter.asNestedObject(index, path);
	}
	
	/**
	 * Returns the index as a string.
	 */
	public String toString(){
		return index.toString();
	}
}