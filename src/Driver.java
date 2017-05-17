//import java.io.IOException; 
//import java.nio.file.Paths;
//
///**
// * Parses command-line arguments into the index.
// */
//public class Driver 
//{
//	/**
//	 * Builds the inverted index data structure
//	 * 
//	 * @param args
//	 * 				arguments to be parsed into
//	 * 				into the index
//	 */
//	public static void main(String[] args)
//	{	
//		ArgumentMap argMap = new ArgumentMap(args);
//		InvertedIndex index = null;
//		ThreadSafeInvertedIndexBuilder builder = null;
//		QueryInterface query = null;
//		WorkQueue worker = null;
//		int threads;
//		String results = argMap.getString("-results", "results.json");
//		String output = argMap.getString("-index", "index.json");
//		
//		try {
//			threads = Integer.parseInt(argMap.getValue("-threads"));
//			if (threads <= 0) {
//				threads = 5;
//			}
//		}
//		catch (NumberFormatException e) {
//			threads = 5;
//		}
//		
//		if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
//			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
//			worker = new WorkQueue(threads);
//			builder = new ThreadSafeInvertedIndexBuilder(threadedIndex, worker);
//			query = new ThreadSafeQueryHelper(threadedIndex, worker); 
//			index = threadedIndex;
//		}
//		else {
//			index = new InvertedIndex();
//			query = new QueryHelper(index);
//			
//		}
//		
//		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
//			try {
//				if (builder == null) {
//					InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
//				}
//				else {
//					builder.traverse(Paths.get(argMap.getValue("-path")), (ThreadSafeInvertedIndex) index);
//				}
//			}
//			catch (IOException e) {
//				System.out.println("Unable to build index from the path " + argMap.getString("-path"));
//			}
//		}
//		
//		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
//			try {
//				query.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
//			}
//			catch (IOException e) {
//				System.out.println("Unable to read query");
//			}
//		}
//		
//		if (argMap.hasFlag("-index")) {
//			try {
//				index.asJSON(Paths.get(output));
//			}
//			catch (IOException e) {
//				System.out.println("Unable to write index from " + output);
//			}
//		}
//		
//		if (argMap.hasFlag("-results")) {
//			try {
//				query.asJSON(Paths.get(results));
//			}
//			catch (IOException e) {
//				System.out.println("Unable to write results from " + results);
//			}
//		}
//		
//		if (worker != null) {
//			worker.shutdown();
//		}
//		
//	}	
//}
import java.io.IOException;
import java.net.URL;
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
//		ArgumentMap argMap = new ArgumentMap(args);
//		InvertedIndex index = null;
//		ThreadSafeInvertedIndexBuilder builder = null;
//		QueryInterface query = null;
//		WorkQueue worker = null;
//		int threads;
//		String results = argMap.getString("-results", "results.json");
//		String output = argMap.getString("-index", "index.json");
//		
//		try {
//			threads = Integer.parseInt(argMap.getValue("-threads"));
//			if (threads <= 0) {
//				threads = 5;
//			}
//		}
//		catch (NumberFormatException e) {
//			threads = 5;
//		}
//		
//		if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
//			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
//			worker = new WorkQueue(threads);
//			builder = new ThreadSafeInvertedIndexBuilder(threadedIndex, worker);
//			query = new ThreadSafeQueryHelper(threadedIndex, worker); 
//			index = threadedIndex;
//		}
//		else {
//			index = new InvertedIndex();
//			query = new QueryHelper(index);
//			
//		}
		
		ArgumentMap argMap = new ArgumentMap(args);
		ThreadSafeInvertedIndexBuilder builder = null;
		InvertedIndex index = null;
		QueryInterface query = null;
		WorkQueue worker = null;
		String results = argMap.getString("-results", "results.json");
		String output = argMap.getString("-index", "index.json");
		
		if ((!argMap.hasFlag("-threads")) && (!argMap.hasFlag("-url"))) {
			index = new InvertedIndex();
			query = new QueryHelper(index);
			
			try {
				InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
			}
			catch (IOException e) {
				System.out.println("No arguments");
			}
		}
		else {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			index = threadedIndex;
			int numThreads = 5;
			try {
				if (argMap.hasFlag("-threads")) {
					numThreads = Integer.parseInt(argMap.getValue("-threads"));
				}
			}
			catch (NumberFormatException e) {
				System.out.println("Incorrect number of threads");
			}
			worker = new WorkQueue(numThreads);
			query = new ThreadSafeQueryHelper(threadedIndex, worker); 
			ThreadSafeInvertedIndexBuilder threadedBuilder = new ThreadSafeInvertedIndexBuilder(threadedIndex, worker);
			
			if (!argMap.hasFlag("-url")) {
				try {
					threadedBuilder.traverse(Paths.get(argMap.getValue("-path")), (ThreadSafeInvertedIndex) index);
				}
				catch (Exception e) {
					System.out.println("No arguments");
				}
			}
			else {
				try {
					String value = argMap.getValue("-url");
					URL target = new URL(value);
					threadedBuilder.traverseURL(target, (ThreadSafeInvertedIndex) index);
				}
				catch (IOException e) {
					System.out.println("Invalid link");
				}
			}
			
		}
		
		
		
		
		
		
		
		
		
		
//		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
//			try {
//				if (builder == null) {
//					InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
//				}
//				else {
//					builder.traverse(Paths.get(argMap.getValue("-path")), (ThreadSafeInvertedIndex) index);
//				}
//			}
//			catch (IOException e) {
//				System.out.println("Unable to build index from the path " + argMap.getString("-path"));
//			}
//		}
		
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