import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

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
		QueryHelper query = new QueryHelper(index);
		String results = argMap.getString("-results", "results.json");
		
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

		if (argMap.hasFlag("-query") && argMap.hasValue("-query")) {
			try {
				query.parse(Paths.get(argMap.getValue("-query")), argMap.hasFlag("-exact"));
			}
			catch (IOException e) {
				System.out.println("Unable to build from query file");
			}
		}
		
		if (argMap.hasFlag("-results")) {
			try {
				query.toJSON(Paths.get(results));
			}
			catch (IOException e) {
				System.out.println("Unable to build from path" + results);
			}
		}

	}	
}