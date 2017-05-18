//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
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
//		int limit;
//		String results = argMap.getString("-results", "results.json");
//		String output = argMap.getString("-index", "index.json");
//		WebCrawler crawler = null;
//		URL url;
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
//		try {
//			limit = Integer.parseInt(argMap.getValue("-limit"));
//			if (limit <= 50) {
//				limit = 50;
//			}
//		}
//		catch (NumberFormatException e) {
//			limit = 50;
//		}
//		
//		try {
//			url = new URL(argMap.getValue("-url"));
//		}
//		catch (MalformedURLException e) {
//			System.out.println("Invalid URL");
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
//		if (argMap.hasFlag("-url") && argMap.hasValue("-url")) {
//			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
//			worker = new WorkQueue(threads);
//			index = threadedIndex;
//			crawler = new WebCrawler(threadedIndex, worker);
//			try {
//				url = new URL(argMap.getValue("-url"));
//				crawler.crawl(url, limit);
//			}
//			catch (MalformedURLException e) {
//				System.out.println("Invalid URL");
//			}
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
//				System.out.println(1);
//				query.asJSON(Paths.get(results));
//				System.out.println(1);
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
		ArgumentMap argument = new ArgumentMap(args);
		InvertedIndex invertedIndex = null;
		QueryInterface queryHelper = null;
		WorkQueue queue = null;
		WebCrawler crawler = null;
		int threads;
		String results = argument.getString("-results", "results.json");
		String output = argument.getString("-index", "index.json");
		
		int total;
		URL url = null;
		
		try  {
			threads = Integer.parseInt(argument.getValue("-threads"));
			
			if (threads <= 0) {
				threads = 5;
			}
		} 
		catch (NumberFormatException e)  {
			threads = 5;
		}
		
		try {
			total = Integer.parseInt(argument.getValue("-limit"));
			
			if (total <= 0) {
				total = 50;
			}
		}
		
		catch (NumberFormatException e) {
			total = 50;
		}
		
		try {
			url = new URL(argument.getValue("-url"));
		} 
		
		catch (MalformedURLException e1)  {
			System.out.println("invalid url");
		}
		
		
		if (argument.hasFlag("-threads") && argument.hasValue("-threads")) {
			queue = new WorkQueue(threads);
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafeIndex;
			queryHelper = new ThreadSafeQueryHelper(threadSafeIndex, queue);
						
			if (argument.hasFlag("-path") && argument.hasValue("-path")) {
				ThreadSafeInvertedIndexBuilder builder = new ThreadSafeInvertedIndexBuilder(threadSafeIndex, queue);
				try  {
					builder.traverse(Paths.get(argument.getValue("-path")), threadSafeIndex);
					
				}
				catch (IOException e) {
					System.out.println("Unable to build index from the path" + argument.getString("-path"));
				}
			}

		}

		else {
			invertedIndex = new InvertedIndex();
			queryHelper = new QueryHelper(invertedIndex);
			
			if (argument.hasFlag("-path") && argument.hasValue("-path")) {	
				try  {
					InvertedIndexBuilder.traverse(Paths.get(argument.getValue("-path")), invertedIndex);
				}
				
				catch (IOException e) {
					System.out.println("Unable to build index from the path" + argument.getString("-path"));
				}
			}
		}
		
		if (argument.hasFlag("-url") && argument.hasValue("-url")) {
			ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
			invertedIndex = threadedIndex;
			queue = new WorkQueue();
			crawler = new WebCrawler(threadedIndex, queue);

			try {
				URL urlFlag = new URL(argument.getValue("-url"));
				crawler.crawl(urlFlag, total);
			} 
			catch (MalformedURLException e) {
				System.out.println();
			}
		}
		
		if (argument.hasFlag("-query") && argument.hasValue("-query")) {
			try {
				queryHelper.parse(Paths.get(argument.getValue("-query")), argument.hasFlag("-exact"));
			}
			catch(IOException e) {
				System.out.println("Unable to read query file");
			}
		}
		
		if (argument.hasFlag("-index")) {
			try  {
				invertedIndex.asJSON(Paths.get(output));
			}
			catch (IOException e) {
				System.out.println("Unable to write index to the path" + output);
			}
		}

		if (argument.hasFlag("-results")) {
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