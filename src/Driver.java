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
		InvertedIndex index = new InvertedIndex();
		String results = argMap.getString("-results", "results.json");
		String output = argMap.getString("-index", "index.json");
		
		ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
		QueryInterface query = new QueryHelper(index); 
		int threads;
		
		try {
			threads = Integer.parseInt(argMap.getValue("-threads"));
			if (threads <= 0) {
				threads = 5;
			}
		}
		catch (NumberFormatException e) {
			threads = 5;
		}


		WorkQueue worker = new WorkQueue(threads);
		ThreadSafeInvertedIndexBuilder builder = new ThreadSafeInvertedIndexBuilder(threadedIndex, worker);	
		QueryInterface threadedQuery = new ThreadSafeQueryHelper(threadedIndex, worker);
		
		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
			if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
				try {
					builder.traverse(Paths.get(argMap.getValue("-path")), threadedIndex);
				}
				catch (IOException e) {
					System.out.println("Unable to build index from the path " + argMap.getString("-path"));
				}
			}
			else {
				try {
					InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
				}
				catch (IOException e) {
					System.out.println("Unable to build index from the path " + argMap.getString("-path"));
				}
			}
		}
		
		if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
			if (argMap.hasFlag("-index")) {
				try {
					threadedIndex.asJSON(Paths.get(output));
				}
				catch (IOException e) {
					System.out.println("Unable to build index from " + output);
				}
			}
		}
		else {
			if (argMap.hasFlag("-index")) {
				try {
					index.asJSON(Paths.get(output));
				}
				catch (IOException e) {
					System.out.println("Unable to build index from " + output);
				}
			}
		}
		
		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
			if (argMap.hasFlag("-threads") && argMap.hasValue("-threads") && argMap.getValue("-threads") != null) {
				try {
					threadedQuery.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
				}
				catch (IOException e) {
					System.out.println("Unable to build from query file");
				}
			}
			else {
				try {
					query.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
				}
				catch (IOException e) {
					System.out.println("Unable to build from query file");
				}
			}
		}
		
		if (argMap.hasFlag("-results")) {
			if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
				try {
					threadedQuery.toJSON(Paths.get(results));
				}
				catch (IOException e) {
					System.out.println("Unable to build from path" + results);
				}
			}
			else {
				try {
					query.toJSON(Paths.get(results));
				}
				catch (IOException e) {
					System.out.println("Unable to build from path" + results);
				}
			}
		}
		worker.shutdown();
	}	
}