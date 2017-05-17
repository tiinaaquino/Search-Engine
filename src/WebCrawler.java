import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class WebCrawler 
{
	private WorkQueue queue; // TODO  = new WorkQueue();
//	private ReadWriteLock lock = new ReadWriteLock();
	private ThreadSafeInvertedIndex index; // TODO = new ThreadSafeInvertedIndex();
	// private final HashSet<String> set;
	private final HashSet<URL> set;
//	private static final Logger logger = LogManager.getLogger();
//	private final int MAX = 50;
	private int MAX;
	
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue)
	{
		this.index = index;
		this.queue = queue;
		this.set = new HashSet<>();
		this.MAX = 0;
	}
	
	
	public void crawl(URL url, int limit) throws MalformedURLException 
	{
		MAX += limit;
//		if (limit > MAX)
//		{
//			limit = MAX;
//		}
		
		System.out.println(url);
		
		set.add(url);
		
		queue.execute(new CrawlWorker(url)); // TODO, set, index));
		queue.finish();
		
	}
	
	private class CrawlWorker implements Runnable
	{
		private URL url;
//		private final HashSet<String> set;
//		private final ThreadSafeInvertedIndex index;
		
		private CrawlWorker(URL url) // TODO, HashSet<String> set, ThreadSafeInvertedIndex index)
		{
			this.url = url;
//			this.set = set;
//			this.index = index;
		}
		
		@Override
		public void run()
		{
			String html = LinkParser.fetchHTML(url);
			ArrayList<URL> processedURL;
			try {
				processedURL = LinkParser.listLinks(url, html);
				synchronized (set) {
					for (int i = 0; i < processedURL.size(); i++)
					{
						if (set.size() >= MAX) {
							break;
						}
						
						if (!set.contains(processedURL.get(i)))
						{
							set.add(processedURL.get(i));
							queue.execute(new CrawlWorker(processedURL.get(i)));
						}
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			

			String cleaned = HTMLCleaner.stripHTML(html);
			String[] words = WordParser.parseWords(cleaned);

			//index.addAll(words, Paths.get(url.toString()));
			index.addAll(words, url.toString());

		}
	}
}