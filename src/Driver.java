import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class Driver 
{
	/**
	 * Initializes and runs the program
	 * @param args
	 * 			takes in arguments
	 * @throws IOException
	 */
	public static void main(String[] args)
	{
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = null;
		QueryInterface queryHelper = null;
		WorkQueue queue = null;
		int threads;
		String results = argMap.getString("-results", "results.json");
		String output = argMap.getString("-index", "index.json");
		
		WebCrawler crawler = null;
		int total;
		URL url = null;
		
		try  {
			threads = Integer.parseInt(argMap.getValue("-threads"));
			
			if (threads <= 0) {
				threads = 5;
			}
		} 
		catch (NumberFormatException e)  {
			threads = 5;
		}
		
		try {
			total = Integer.parseInt(argMap.getValue("-limit"));
			
			if (total <= 0) {
				total = 50;
			}
		}
		
		catch (NumberFormatException e) {
			total = 50;
		}
		
		try {
			url = new URL(argMap.getValue("-url"));
		} 
		
		catch (MalformedURLException e1)  {
			System.out.println("Invalid URL");
		}
		
		
		if (argMap.hasFlag("-threads") && argMap.hasValue("-threads")) {
			queue = new WorkQueue(threads);
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			queryHelper = new ThreadSafeQueryHelper(threadSafeIndex, queue);
						
			if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
				ThreadSafeInvertedIndexBuilder builder = new ThreadSafeInvertedIndexBuilder(threadSafeIndex, queue);
				try  {
					builder.traverse(Paths.get(argMap.getValue("-path")), threadSafeIndex);
					
				}
				catch (IOException e) {
					System.out.println("Unable to build index from the path" + argMap.getString("-path"));
				}
			}

		}

		else {
			index = new InvertedIndex();
			queryHelper = new QueryHelper(index);
			
			if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {	
				try  {
					InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
				}
				
				catch (IOException e) {
					System.out.println("Unable to build index from the path" + argMap.getString("-path"));
				}
			}
		}
		
		if (argMap.hasFlag("-url") && argMap.hasValue("-url")) {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			index = threadedIndex;
			queue = new WorkQueue();
			crawler = new WebCrawler(threadedIndex, queue);
			queryHelper = new ThreadSafeQueryHelper(threadedIndex, queue);


			try {
				URL urlFlag = new URL(argMap.getValue("-url"));
				crawler.crawl(urlFlag, total);
			} 
			catch (MalformedURLException e) {
				System.out.println();
			}
		}
		
		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
			try {
				queryHelper.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
			}
			catch(IOException e) {
				System.out.println("Unable to read query file");
			}
		}
		
		if (argMap.hasFlag("-index")) {
			try  {
				index.asJSON(Paths.get(output));
			}
			catch (IOException e) {
				System.out.println("Unable to write index to the path" + output);
			}
		}

		if (argMap.hasFlag("-results")) {
			try {
				queryHelper.asJSON(Paths.get(results));
			}
			catch(IOException e) {
				System.out.println("Unable to write results to path" + results);
			}
		}
		
		if (queue != null) {
			queue.shutdown();
		}
	}
}