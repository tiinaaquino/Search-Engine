//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashSet;
//
//public class WebCrawler 
//{
//	private WorkQueue queue;
//	private ThreadSafeInvertedIndex index;
//	private final HashSet<URL> set;
//	private int MAX;
//	
//	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue) {
//		this.index = index;
//		this.queue = queue;
//		this.set = new HashSet<>();
//		this.MAX = 0;
//	}
//	
//	
//	public void crawl(URL url, int limit) throws MalformedURLException  {
//		MAX += limit;
//		
//		System.out.println(url);
//		
//		set.add(url);
//		
//		queue.execute(new CrawlWorker(url));
//		queue.finish();
//		
//	}
//	
//	private class CrawlWorker implements Runnable {
//		private URL url;
//		
//		private CrawlWorker(URL url) {
//			this.url = url;
//
//		}
//		
//		@Override
//		public void run()
//		{
//			String html = LinkParser.fetchHTML(url);
//			ArrayList<URL> processedURL;
//			try {
//				processedURL = LinkParser.listLinks(url, html);
//				synchronized (set) {
//					for (int i = 0; i < processedURL.size(); i++) {
//					
//						if (set.size() >= MAX) {
//							break;
//						}
//						
//						if (!set.contains(processedURL.get(i))) {
//							set.add(processedURL.get(i));
//							queue.execute(new CrawlWorker(processedURL.get(i)));
//						}
//					}
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//			
//			
//
//			String cleaned = HTMLCleaner.stripHTML(html);
//			String[] words = WordParser.parseWords(cleaned);
//
//			
//			index.addAll(words, url.toString());
//
//		}
//	}
//}

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebCrawler 
{
	private WorkQueue queue;
	private ThreadSafeInvertedIndex index;
	private final HashSet<String> links;
	private int MAX = 50;
	
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue)
	{
		this.index = index;
		this.queue = queue;
		this.links = new HashSet<>();
		this.MAX = 0;
	}
	
	public void crawl(URL url, int limit) throws MalformedURLException 
	{
		if (limit >= MAX) {
			MAX = limit;
		}
		
		else {
			limit=MAX;
		}
		
		links.add(url.toString());
		queue.execute(new CrawlWorker(url));
		queue.finish();
	}
	
	private class CrawlWorker implements Runnable {
		private URL url;
		
		private CrawlWorker(URL url) {
			this.url = url;
		}
		
		@Override
		public void run() {
			String html = LinkParser.fetchHTML(url);
			ArrayList<String> processedURL;
			int count = 1;
			
			try {
				processedURL = LinkParser.listLinks(url, html);
				synchronized (links) {
					for (String link : processedURL) {
						
						if (!links.contains(link)) {
							
							if (links.size() >= MAX) {
								break;
							}
							links.add(link);
							URL process = new URL(link);
							queue.execute(new CrawlWorker(process));
						}
					}
				}
			} 
			catch (MalformedURLException e) {
				e.printStackTrace();
			}

			String cleaned = HTMLCleaner.stripHTML(html);
			String[] words = WordParser.parseWords(cleaned);
			int position = 1;
			
			for (String word : words) {
				index.add(word, url.toString(), position);
				position++;
			}

		}
	}
}