import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

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
		/*
		ArgumentMap argMap = new ArgumentMap(args);

		InvertedIndex index = null;
		QueryHelperInterface query = null;

		int threads;

		if (-threads) {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			index = threadedIndex;
			threads = ????;
			query = new ThreadSafeQueryHelper(threadedIndex, threads);
		}
		else {
			index = new InvertedIndex();
			query = QueryHelper(index);
		}
		
		if (-path) {

			if (threads > 0) {
				ThreadSafeInvertedIndexBuilder builder = ...
			}
			else {
				InvertedIndexBuilder....
			}
		}
		
		if(-query) {
			query.parse(...);
		}

		if (-index) {
			index.toJSON(...);
		}

		if (-results) {
			query.toJSON(...);
		}
		 */
		
		
		
		
		
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();
		QueryHelper query = new QueryHelper(index);
		String results = argMap.getString("-results", "results.json");
		String output = argMap.getString("-index", "index.json");

		ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
		int threads;
		
		try {
			threads = Integer.parseInt(argMap.getValue("-threads"));
		}
		catch (NumberFormatException e) {
			threads = 5;
		}
		if (threads <= 0) {
			threads = 5;
		}
		
		WorkQueue worker = new WorkQueue(threads);
		ThreadSafeInvertedIndexBuilder builder = new ThreadSafeInvertedIndexBuilder(threadedIndex, threads);
		ThreadSafeQueryHelper threadedQuery = new ThreadSafeQueryHelper(threadedIndex, threads);
		
		System.out.println(Arrays.toString(args));
		
//		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
//			try {
//				InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
//			}
//			catch (IOException e) {
//				System.out.println("Unable to build index from the path " + argMap.getString("-path"));
//			}
//		}
		
		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
			if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
				try {
					builder.traverse(Paths.get(argMap.getValue("-path")), threadedIndex);
					worker.shutdown();
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
		
//		if (argMap.hasFlag("-index")) {
//			String output = argMap.getString("-index", "index.json");
//			try {
//				index.asJSON(Paths.get(output));
//			}
//			catch (IOException e) {
//				System.out.println("Unable to build index from " + output);
//			}
//		}
		
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

//		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
//			try {
//				query.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
//			}
//			catch (IOException e) {
//				System.out.println("Unable to build from query file");
//			}
//		}
		
		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
			if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
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
		
//		if (argMap.hasFlag("-results")) {
//			try {
//				query.toJSON(Paths.get(results));
//			}
//			catch (IOException e) {
//				System.out.println("Unable to build from path" + results);
//			}
//		}
		
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
		
		

	}	
}