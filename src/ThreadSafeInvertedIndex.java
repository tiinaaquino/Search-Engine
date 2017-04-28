import java.nio.file.Path;
import java.util.ArrayList;

public class ThreadSafeInvertedIndex extends InvertedIndex{
	
	private ReadWriteLock lock;
//	private static final Logger logger = LogManager.getLogger();

	
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
//		logger.debug();
		lock.lockReadWrite();
//		logger.debug();
		try {
			super.addAll(words, path);
		}
		finally {
//			logger.debug();
			lock.unlockReadWrite();
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
	
	
}
