import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Parses command-line arguments into the index.
 */
public class Driver 
{
	/**
	 * Builds the inverted index data structure
	 * 
	 * @param args
	 * 				arguments to be parsed into
	 * 				into the index
	 */
	public static void main(String[] args)
	{	
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();
		TreeMap<String, ArrayList<SearchResult>> treeMap = new TreeMap<>();
		
		System.out.println(Arrays.toString(args));
		
		if (map.hasFlag("-path") && map.hasValue("-path")) {
			try {
				InvertedIndexBuilder.traverse(Paths.get(map.getValue("-path")), index);
			}
			catch (IOException e) {
				System.out.println("Unable to build index from the path " + map.getString("-path"));
			}
		}
		
		if (map.hasFlag("-index")) {
			String output = map.getString("-index", "index.json");
			try {
				index.asJSON(Paths.get(output));
			}
			catch (IOException e) {
				System.out.println("Unable to build index from " + map.getString("-index"));
			}
		}
		
		String results = map.getString("-results", "results.json");
		if (map.hasFlag("-results")) {
			if (map.hasFlag("-query") && map.hasValue("-query")) {
				if (map.hasFlag("-exact")) {
					try {
						ArrayList<String> list = QueryHelper.parse(Paths.get(map.getValue("-query")));
						Set<String> set = new HashSet<String>(list);
						ArrayList<String> queryList = new ArrayList<String>(set);
						Collections.sort(queryList);
						
						for (String query : queryList) {
							treeMap.put(query, index.exactSearch(query));
						}
						JSONWriter.asSearchObject(treeMap, Paths.get(results));
					}
					catch (IOException e) {
						System.out.println("Exact search failed.");
					}
				}
				else {
					try {
						ArrayList<String> list = (QueryHelper.parse(Paths.get(map.getValue("-query"))));
						Set<String> set = new HashSet<String>(list);
						ArrayList<String> queryList = new ArrayList<String>(set);
						Collections.sort(queryList);
						
						for (String query : queryList) {
							if (!query.equals("")) {
								treeMap.put(query, index.partialSearch(query));
							}
							JSONWriter.asSearchObject(treeMap, Paths.get(results));
						}
					}
					catch (IOException e) {
						System.out.println("Partial search failed.");
					}
				}
			}
			
			try {
				index.asJSON(Paths.get("-results"));
			}
			catch (IOException e) {
				System.out.println("Error.");
			}
		}
		
		if (map.hasFlag("-results") && !map.hasValue("-results")) {
			try {
				index.asJSON(Paths.get(results));
			}
			catch (IOException e) {
				System.out.println("No value for results.");
			}
		}
		

	}	
}