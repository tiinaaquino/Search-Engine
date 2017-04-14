import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

// TODO Address warnings

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
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();
		TreeMap<String, ArrayList<SearchResult>> treeMap = new TreeMap<>();
		
		System.out.println(Arrays.toString(args));
		
		if (argMap.hasFlag("-path") && argMap.hasValue("-path")) {
			try {
				InvertedIndexBuilder.traverse(Paths.get(argMap.getValue("-path")), index);
			}
			catch (IOException e) {
				System.out.println("Unable to build index from the path " + argMap.getString("-path"));
			}
		}
		
		if (argMap.hasFlag("-index")) {
			String output = argMap.getString("-index", "index.json");
			try {
				index.asJSON(Paths.get(output));
			}
			catch (IOException e) {
				System.out.println("Unable to build index from " + output);
			}
		}
		
		if (argMap.hasFlag("-results")) {
			
			if (argMap.hasFlag("-query") && argMap.hasValue("-query")) { // TODO This should happen even if you do not output the results to file
				
				try {
					ArrayList<String> list = QueryHelper.parse(Paths.get(argMap.getValue("-query")));
					Set<String> set = new HashSet<String>(list);
					ArrayList<String> queryList = new ArrayList<String>(set);
					Collections.sort(queryList);
					
					for (String query : queryList) {
						
						if (argMap.hasFlag("-exact")) {
							treeMap.put(query, index.exactSearch(query));
						} 
						else {			
							if (!query.equals("")) {
								treeMap.put(query, index.partialSearch(query));
							}
						}
						
					}
					JSONWriter.asSearchObject(treeMap, Paths.get(argMap.getString("-results", "results.json")));
				}
				catch (IOException e) {
					System.out.println("Search failed.");
				}
			}
		}
			
			try {
				index.asJSON(Paths.get("-results"));
			}
			catch (IOException e) {
				System.out.println("Error.");
			}
		
		if (argMap.hasFlag("-results") && !argMap.hasValue("-results")) {
			try {
				index.asJSON(Paths.get(argMap.getString("-results", "results.json")));
			}
			catch (IOException e) {
				System.out.println("No value for results.");
			}
		}
	}	
}