import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

	// https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a
	// https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html
	// https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL

	/**
	 * Removes the fragment component of a URL (if present), and properly
	 * encodes the query string (if necessary).
	 *
	 * @param url
	 *            url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		}
		catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url
	 *            url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws UnknownHostException 
	 */
	public static String fetchHTML(URL url) {
		String result = "";
		try {
			result = HTTPFetcher.fetchHTML(url);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base
	 *            base url used to convert relative links to absolute3
	 * @param html
	 *            raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html) throws MalformedURLException {
		ArrayList<URL> links = new ArrayList<URL>();
		
		html = html.replaceAll("\\s", "").trim();
		
		Pattern p = Pattern.compile("(?i)<a[^>]*\\s*href\\s*=\\s*\"(.*?)\\s*\"\\s*");
		Matcher m = p.matcher(html);
		
		int group = 1;
		URL link = null;
		
		while (m.find()) {
			String s = m.group(group);
			
			if(!s.startsWith("http")) {
				link = new URL(base, s);
			}
			else {
				link = new URL(s);
			}
			
			if (link.toString().startsWith("http")) {
				links.add(clean(link));
			}
		}

		return links;
	}
}