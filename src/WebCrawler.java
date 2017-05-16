import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public class WebCrawler {
//
//	private HashSet<String> links;
//	private ThreadSafeInvertedIndex index;
//	private final WorkQueue workers;
//	private static final Logger logger = LogManager.getLogger();
//	private final int MAX_CAPACITY = 50;
//	
//	public WebCrawler (int numThreads, ThreadSafeInvertedIndex index) {
//		this.index = index;
//		workers = new WorkQueue(numThreads);
//		links = new HashSet<>();
//	}
//	
//	public void traverse(String url, int thread) throws MalformedURLException {
//		logger.debug("Adding {} into links", url);
//		links.add(url);
//		
//		Iterator<String> iterator = links.iterator();
//		int count = 1;
//		while (iterator.hasNext()) {
//			if (count < MAX_CAPACITY) {
//				continue;
//			}
//			String link = iterator.next();
//			logger.debug("Executing for link {}", link);
//			workers.execute(new CrawlWorker(link, links));
//			finish();
//		}
//
//		logger.debug("Links size: {}", links.size());
//	}
//	
//	private class CrawlWorker implements Runnable {
//		private String link;
//		private HashSet<String> links;
//		
//		private CrawlWorker(String link, HashSet<String> links) {
//			this.link = link;
//			this.links = links;
//			logger.debug("Worker created for {}", link);
//		}
//		
//		@Override
//		public void run() {
//			try {
//				String html = HTTPFetcher.fetchHTMLString(link);
//				ArrayList<String> innerURLs = LinkParser.linksList(html);
//				
//				for (int i = 0; (i < innerURLs.size()) && (links.size() < MAX_CAPACITY); i++) {
//					String innerURL = innerURLs.get(i);
//					URL base = new URL(link);
//					URL absolute = new URL(base, innerURL);
//					logger.debug("link {}", absolute.toString());
//					
//					if ((!link.contains(absolute.toString())) && (!absolute.toString().contains("#"))) {
//						links.add(absolute.toString());
//						logger.debug("added, {}", absolute.toString());
//					}
//				}
//				
//				html = HTMLCleaner.stripHTML(html);
//				String[] words = WordParser.split(html);
//				int position = 1;
//				
//				for (String word : words) {
//					index.add(word, link, position);
//					position++;
//				}
//				logger.debug("Passed words for this {} into index", link);
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//			logger.debug("Worker finished {}", link);
//		}
//	}
//	
//	public void finish() {
//		workers.finish();
//	}
//	
//	public void shutdown() {
//		finish();
//		workers.shutdown();
//	}
//	
//}

public class WebCrawler {
	private final HashSet<String> URLList;
	private static final Logger logger = LogManager.getLogger();
	private final WorkQueue worker;
	private final ThreadSafeInvertedIndex global;
	
	public WebCrawler(InvertedIndex globalIndex, WorkQueue worker) {
		this.global = (ThreadSafeInvertedIndex) globalIndex;
		this.worker = worker;
		URLList = new HashSet<>();
	}
	
	public void startCrawl(String seed) {
		String baseURL = getBaseURL(seed);
		URLList.add(seed);
	}
	
	private void crawl(String currentURL, String baseURL, InvertedIndex localIndex) {
		String html = HTTPFetcher.fetchHTMLString(currentURL, baseURL);
		ArrayList<String> list = LinkParser.linksList(html);
		list = HTTPFetcher.
	}

	private String getBaseURL(String seed) {
		String seedBase = "";
		try {
			URL url = new URL(seed);
			seedBase = url.getProtocol() + "://" + url.getHost() + url.getPath();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return seedBase;
	}
	
	public class WebCrawlerWorker implements Runnable {
		private final String baseURL, currentURL;
		private final InvertedIndex localIndex;
		
		public WebCrawlerWorker(String currentURL, String baseURL) {
			this.currentURL = currentURL;
			this.baseURL = baseURL;
			localIndex = new InvertedIndex();
		}
		
		@Override
		public void run() {
			crawl(currentURL, baseURL, localIndex);
			global.addAll(localIndex);
		}
	}
}