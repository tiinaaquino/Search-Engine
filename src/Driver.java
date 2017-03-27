import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;


/**
 * Parses command-line arguments into the index.
 */
public class Driver 
{
	public static void main(String[] args)
	{	
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();
		String defaultValue = "index.json";
		
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
			String output = map.getString("-index", defaultValue);
			try {
				index.asJSON(Paths.get(output));

			}
			catch (IOException e) {
				System.out.println("Unable to build index from " + map.getString("-index"));
			}
		}
	}	
}