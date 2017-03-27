import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	
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
	 * Returns whether the word is in the index.\
	 * 
	 * @param word
	 * 				word to find
	 * @return true if the word is in the index,
	 * otherwise false.
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}
	
	// TODO Add more contains/size methods like... contains(String word, String file), etc.
	
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
		TreeMap<String, TreeSet<Integer>> oldPositions = index.get(word);
		TreeSet<Integer> newPositions;
		TreeMap<String, TreeSet<Integer>> newIndex;
		
		if (index.containsKey(word)){
			if (oldPositions.containsKey(path))
				oldPositions.get(path).add(wordPosition);
			else {
				newPositions = new TreeSet<Integer>();
				newPositions.add(wordPosition);
				oldPositions.put(path, newPositions);
			}
		}
		else {
			newPositions = new TreeSet<Integer>();
			newPositions.add(wordPosition);
			newIndex = new TreeMap<String, TreeSet<Integer>>();
			newIndex.put(path, newPositions);
			index.put(word, newIndex);
		}
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