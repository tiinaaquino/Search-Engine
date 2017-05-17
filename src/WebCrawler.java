//import java.io.IOException; 
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashSet;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public class WebCrawler {
//
//	private HashSet<URL> urlSet;
//	private ThreadSafeInvertedIndex index;
//	private final WorkQueue workers;
//	private static final Logger logger = LogManager.getLogger();
//	private final ReadWriteLock lock;
//	private final int MAX_CAPACITY = 50;
//	
//	public WebCrawler (int numThreads, ThreadSafeInvertedIndex index) {
//		this.index = index;
//		workers = new WorkQueue(numThreads);
//		urlSet = new HashSet<>();
//		lock = new ReadWriteLock();
//	}
//	
//	public void crawl(URL url) throws MalformedURLException {
//		if ((urlSet.size() < MAX_CAPACITY) && (!urlSet.contains(url))) {
//			workers.execute(new CrawlWorker(url, urlSet));
//		}
//		workers.finish();
//	}
//	
//	private class CrawlWorker implements Runnable {
//		private URL link;
//		private HashSet<URL> urlSet;
//		
//		public CrawlWorker(URL link, HashSet<URL> urlSet) {
//			this.link = link;
//			this.urlSet = urlSet;
//			logger.debug("Worker created for {}", link);
//		}
//		
//		@Override
//		public void run() {
//			try {
////				lock.lockReadWrite();
//				urlSet.add(link);
////				lock.unlockReadWrite();
//				
//				String html = HTTPFetcher.fetchHTML(link);
//				
////				lock.lockReadWrite();
//				ArrayList<URL> URLs = LinkParser.listLinks(link, html);
//				
//				for (int i = 0; (i < URLs.size()) && (urlSet.size() < MAX_CAPACITY); i++) {
//					URL absolute = URLs.get(i);
//					workers.execute(new CrawlWorker(absolute, urlSet));
//				}
//				
////				lock.unlockReadWrite();
//				
//				html = HTMLCleaner.stripHTML(html);
//				String[] words = WordParser.split(html);
//				int position = 1;
//				
//				InvertedIndex local = new InvertedIndex();
//				
//				for (String word : words) {
//					local.add(word, link.toString(), position);
//					position++;
//				}
//				
//				index.addAll(local);
//								
//			}
//			catch (IOException e) {
//				System.out.println("Invalid link");
//			}
//			logger.debug("Worker finished {}", link);
//		}
//	}
//
//}

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebCrawler 
{
	private WorkQueue queue = new WorkQueue();
	private ReadWriteLock lock = new ReadWriteLock();
	private ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
	private final HashSet<String> set;
	private static final Logger logger = LogManager.getLogger();
	private final int MAX = 50;
	
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue)
	{
		this.index = index;
		this.queue = queue;
		this.set = new HashSet<>();
	}
	
	
	public void crawl(URL url, int limit) throws MalformedURLException 
	{
		if (limit > MAX)
		{
			limit = MAX;
		}
		
		System.out.println(url);
		
		set.add(url.toString());
		
		queue.execute(new CrawlWorker(url, set, index));
		queue.finish();
		
	}
	
	private class CrawlWorker implements Runnable
	{
		private URL url;
		private final HashSet<String> set;
		private final ThreadSafeInvertedIndex index;
		
		private CrawlWorker(URL url, HashSet<String> set, ThreadSafeInvertedIndex index)
		{
			this.url = url;
			this.set = set;
			this.index = index;
		}
		
		private String getBase(URL url) throws MalformedURLException
		{
			String base = "";
			base = url.getProtocol() + "://" + url.getHost() + url.getPath();
			
			return base;
		}
		
		
		@Override
		public void run()
		{
			String html = LinkParser.fetchHTML(url);
			ArrayList<URL> processedURL;
			try {
				processedURL = LinkParser.listLinks(url, html);
				
				for (int i = 0; i < processedURL.size(); i++)
				{
					if (!set.contains(processedURL.get(i)))
					{
						set.add(processedURL.get(i).toString());
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			

			String cleaned = HTMLCleaner.stripHTML(html);
			String[] words = WordParser.parseWords(cleaned);

			index.addAll(words, Paths.get(url.toString()));

		}
	}
}