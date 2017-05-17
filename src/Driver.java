import java.io.IOException;
import java.net.MalformedURLException;
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
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = null;
		ThreadSafeInvertedIndexBuilder builder = null;
		QueryInterface query = null;
		WorkQueue worker = null;
		int threads;
		int limit;
		String results = argMap.getString("-results", "results.json");
		String output = argMap.getString("-index", "index.json");
		WebCrawler crawler = null;
		URL url;
		
		try {
			threads = Integer.parseInt(argMap.getValue("-threads"));
			if (threads <= 0) {
				threads = 5;
			}
		}
		catch (NumberFormatException e) {
			threads = 5;
		}
		
		try {
			limit = Integer.parseInt(argMap.getValue("-limit"));
			if (limit <= 50) {
				limit = 50;
			}
		}
		catch (NumberFormatException e) {
			limit = 50;
		}
		
		try {
			url = new URL(argMap.getValue("-url"));
		}
		catch (MalformedURLException e) {
			System.out.println("Invalid URL");
		}
		
		if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			worker = new WorkQueue(threads);
			builder = new ThreadSafeInvertedIndexBuilder(threadedIndex, worker);
			query = new ThreadSafeQueryHelper(threadedIndex, worker); 
			index = threadedIndex;
		}
		else {
			index = new InvertedIndex();
			query = new QueryHelper(index);
			
		}
		
		if (argMap.hasFlag("-url") && argMap.hasValue("-url")) {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			worker = new WorkQueue(threads);
			index = threadedIndex;
			crawler = new WebCrawler(threadedIndex, worker);
			try {
				url = new URL(argMap.getValue("-url"));
				crawler.crawl(url, limit);
			}
			catch (MalformedURLException e) {
				System.out.println("Invalid URL");
			}
		}
		
		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
			try {
				if (builder == null) {
					InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
				}
				else {
					builder.traverse(Paths.get(argMap.getValue("-path")), (ThreadSafeInvertedIndex) index);
				}
			}
			catch (IOException e) {
				System.out.println("Unable to build index from the path " + argMap.getString("-path"));
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