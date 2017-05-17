import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebCrawler {

	private HashSet<URL> urlSet;
	private ThreadSafeInvertedIndex index;
	private final WorkQueue workers;
	private static final Logger logger = LogManager.getLogger();
	private final ReadWriteLock lock;
	private final int MAX_CAPACITY = 50;
	
	public WebCrawler (int numThreads, ThreadSafeInvertedIndex index) {
		this.index = index;
		workers = new WorkQueue(numThreads);
		urlSet = new HashSet<>();
		lock = new ReadWriteLock();
	}
	
	public void crawl(URL url) throws MalformedURLException {
		if ((urlSet.size() < MAX_CAPACITY) && (!urlSet.contains(url))) {
			workers.execute(new CrawlWorker(url, urlSet));
		}
		finish();
	}
	
	private class CrawlWorker implements Runnable {
		private URL link;
		private HashSet<URL> urlSet;
		
		public CrawlWorker(URL link, HashSet<URL> urlSet) {
			this.link = link;
			this.urlSet = urlSet;
			logger.debug("Worker created for {}", link);
		}
		
		@Override
		public void run() {
			try {
				lock.lockReadWrite();
				urlSet.add(link);
				lock.unlockReadWrite();
				
				String html = HTTPFetcher.fetchHTML(link);
				
				lock.lockReadWrite();
				ArrayList<URL> URLs = LinkParser.listLinks(link, html);
				
				for (int i = 0; (i < URLs.size()) && (urlSet.size() < MAX_CAPACITY); i++) {
					URL absolute = URLs.get(i);
					workers.execute(new CrawlWorker(absolute, urlSet));
				}
				
				lock.unlockReadWrite();
				
				html = HTMLCleaner.stripHTML(html);
				String[] words = WordParser.split(html);
				int position = 1;
				
				InvertedIndex local = new InvertedIndex();
				
				for (String word : words) {
					local.add(word, link.toString(), position);
					position++;
				}
				
				index.addAll(local);
								
			}
			catch (IOException e) {
				System.out.println("Invalid link");
			}
			logger.debug("Worker finished {}", link);
		}
	}
	
	public void finish() {
		workers.finish();
	}
	
	public void shutdown() {
		finish();
		workers.shutdown();
	}
}