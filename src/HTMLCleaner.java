import java.text.Normalizer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Cleans simple, validating HTML 4/5 into plain-text words using regular
 * expressions.
 */
public class HTMLCleaner {

	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");
	
	/**
	 * Replaces all HTML entities with a single space. For example,
	 * "2010&ndash;2012" will become "2010 2012".
	 *
	 * @param html
	 *            text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) {
		if (html == null)
			return null;
		else{
			String newWord = html.replaceAll("&.*?;", " ");
			if (html.contains("& "))
				return html;
			return newWord;
		}
	}

	/**
	 * Replaces all HTML comments with a single space. For example, "A<!-- B
	 * -->C" will become "A C".
	 *
	 * @param html
	 *            text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) {
		return html.replaceAll("(?s)<!--.*?-->", " ");
	}

	/**
	 * Replaces all HTML tags with a single space. For example, "A<b>B</b>C"
	 * will become "A B C".
	 *
	 * @param html
	 *            text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) {
		return html.replaceAll("(?s)(?i)<" + "\\n*(.*?)" + "\\n*>", " ");
	}

	/**
	 * Replaces everything between the element tags and the element tags
	 * themselves with a single space. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed,
	 * and replaced with a single space.
	 *
	 * @param html
	 *            text including HTML elements to remove
	 * @param name
	 *            name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) {
		if (html == null)
			return null;
		else {
			return html.replaceAll("(?s)(?i)<" + name + "\\s*(.*?)" + name + "\\s*>", " ");
			}
	}

	/**
	 * Removes all HTML (including any CSS and JavaScript).
	 *
	 * @param html
	 *            text including HTML to remove
	 * @return text without any HTML, CSS, or JavaScript
	 */
	public static String stripHTML(String html) {
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");

		html = stripTags(html);
		html = stripEntities(html);

		return html;
	}
	
	public static String cleanString(String s){
		s = Normalizer.normalize(s, Normalizer.Form.NFC);
		s = CLEAN_REGEX.matcher(s).replaceAll(" ");
		return s;
	}
}
