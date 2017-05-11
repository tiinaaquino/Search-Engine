import java.io.IOException; 
import java.nio.file.Paths;

/**
 * Parses command-line arguments into the index.
 */
public class Driver 
{
	/**
	 * Builds the inverted index data structure
	 * 
	 * @param args
	 * 				arguments to be parsed into
	 * 				into the index
	 */
	public static void main(String[] args)
	{	
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = null;
		ThreadSafeInvertedIndexBuilder builder = null;
		QueryInterface query = null;
		WorkQueue worker = null;
		int threads;
		String results = argMap.getString("-results", "results.json");
		String output = argMap.getString("-index", "index.json");
		
		try {
			threads = Integer.parseInt(argMap.getValue("-threads"));
			if (threads <= 0) {
				threads = 5;
			}
		}
		catch (NumberFormatException e) {
			threads = 5;
		}
		
		if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			worker = new WorkQueue(threads);
			builder = new ThreadSafeInvertedIndexBuilder(threadedIndex, worker);
			query = new ThreadSafeQueryHelper(threadedIndex, worker); 
			index = threadedIndex;
			
			if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
				try {
					builder.traverse(Paths.get(argMap.getValue("-path")), threadedIndex);
				}
				catch (IOException e) {
					System.out.println("Unable to build index from the path " + argMap.getString("-path"));
				}
			}
		}
		else {
			index = new InvertedIndex();
			query = new QueryHelper(index);
			
			if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
				try {
					InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
				}
				catch (IOException e) {
					System.out.println("Unable to build index from the path " + argMap.getString("-path"));
				}
			}
		}
		
		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
			try {
				query.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
			}
			catch (IOException e) {
				System.out.println("Unable to read query");
			}
		}
		
		if (argMap.hasFlag("-index")) {
			try {
				index.asJSON(Paths.get(output));
			}
			catch (IOException e) {
				System.out.println("Unable to write index from " + output);
			}
		}
		
		if (argMap.hasFlag("-results")) {
			try {
				query.asJSON(Paths.get(results));
			}
			catch (IOException e) {
				System.out.println("Unable to write results from " + results);
			}
		}
		
		if (worker != null) {
			worker.shutdown();
		}
		
	}	
}