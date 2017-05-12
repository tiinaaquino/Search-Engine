import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ThreadSafeInvertedIndex extends InvertedIndex{
	
	/**
	 * Lock object
	 */
	private final ReadWriteLock lock;

	/**
	 * Initializes the index with a lock object
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	@Override
	public void add(String word, String path, int wordPosition) {
		lock.lockReadWrite();
		
		try {
			super.add(word, path, wordPosition);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void addAll(String[] words, Path path) {
		lock.lockReadWrite();
		
		try {
			super.addAll(words, path);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void addAll(InvertedIndex other) {
		lock.lockReadOnly();
		
		try {
			super.addAll(other);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	
	@Override
	public ArrayList<SearchResult> partialSearch(String[] queryWords) {
		lock.lockReadOnly();
		
		try {
			return super.partialSearch(queryWords);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public ArrayList<SearchResult> exactSearch(String[] queryWords) {
		lock.lockReadOnly();
		
		try {
			return super.exactSearch(queryWords);
		}
		finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void asJSON(Path path) throws IOException {
		lock.lockReadOnly();
		
		try {
			super.asJSON(path);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public int numWords() {
		lock.lockReadOnly();
		try {
			return super.numWords();
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public int numLocations(String word) {
		lock.lockReadOnly();
		try {
			return super.numLocations(word);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public List<String> copyWords() {
		lock.lockReadOnly();
		try {
			return super.copyWords();
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return super.contains(word);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean contains(String word, String location) {
		lock.lockReadOnly();
		try {
			return super.contains(word, location);
		}
		finally {
			lock.unlockReadOnly();
		}	
	}
	
	@Override
	public boolean contains (String word, String location, int wordPosition) {
		lock.lockReadOnly();
		try {
			return super.contains(word, location, wordPosition);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
}