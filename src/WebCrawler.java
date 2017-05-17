import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebCrawler {

	private ArrayList<String> links;
	private ThreadSafeInvertedIndex index;
	private final WorkQueue workers;
	private static final Logger logger = LogManager.getLogger();
	private final ReadWriteLock lock;
	private final int MAX_CAPACITY = 50;
	
	public WebCrawler (int numThreads, ThreadSafeInvertedIndex index) {
		this.index = index;
		workers = new WorkQueue(numThreads);
		links = new ArrayList<>();
		lock = new ReadWriteLock();
	}
	
	public void traverse(String url) throws MalformedURLException {
		logger.debug("Adding {} into links", url);
		links.add(url);
		
		for (int i = 0; (i < MAX_CAPACITY) && (i < links.size()); i++) {
			String link = links.get(i);
			logger.debug("Executing for link {}", link);
			workers.execute(new CrawlWorker(link, links));
			finish();
		}
		logger.debug("Links size: {}", links.size());
	}
	
	private class CrawlWorker implements Runnable {
		private String link;
		private ArrayList<String> links;
		
		private CrawlWorker(String link, ArrayList<String> links) {
			this.link = link;
			this.links = links;
			logger.debug("Worker created for {}", link);
		}
		
		@Override
		public void run() {
			try {
				String html = HTTPFetcher.fetchHTMLString(link);
				ArrayList<String> innerURLs = LinkParser.linksList(html);
				
				for (int i = 0; (i < innerURLs.size()) && (links.size() < MAX_CAPACITY); i++) {
					String innerURL = innerURLs.get(i);
					URL base = new URL(link);
					URL absolute = new URL(base, innerURL);
					logger.debug("link {}", absolute.toString());
					
					if ((!link.contains(absolute.toString())) && (!absolute.toString().contains("#"))) {
						links.add(absolute.toString());
						logger.debug("added, {}", absolute.toString());
					}
				}
				
				html = HTMLCleaner.stripHTML(html);
				String[] words = WordParser.split(html);
				int position = 1;
				
				for (String word : words) {
					index.add(word, link, position);
					position++;
				}
				logger.debug("Passed words for this {} into index", link);
			}
			catch (IOException e) {
				e.printStackTrace();
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