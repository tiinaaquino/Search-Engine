import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Driver 
{
	public static void main(String[] args)
	{	
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();
		String defaultValue = "index.json";
		
		System.out.println(Arrays.toString(args));
		
		try {
			// map without an index flag and without a path flag
			if (!map.hasFlag("-index") && !map.hasFlag("-path")) {
				System.out.println("No path and index flag input.");
			}
			
			// map without an index flag and with a path flag
			if (!map.hasFlag("-index") && map.hasFlag("-path")) {
				if (map.hasValue("-path")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
				}
			}
		
			// map with an index and without a path flag
			if (map.hasFlag("-index") && !map.hasFlag("-path")) {
				if (map.hasValue("-index")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
				}
				index.asJSON(Paths.get(defaultValue));
				System.out.println("Contains an index flag.");
			}
			
			// map with an index flag and a path flag
			if (map.hasFlag("-index") && map.hasFlag("-path")) {
				// map with an index value and a path value
				if (map.hasValue("-index") && map.hasValue("-path")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
					index.asJSON(Paths.get(map.getValue("-index")));		
				}
				
				// map with a path value and without an index value
				if (map.hasValue("-path") && !map.hasValue("-index"))  {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
					index.asJSON(Paths.get(defaultValue));
				}
				
				// map without a path value and with an index value
				if (!map.hasValue("-path") && map.hasValue("-index")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
				}
			}
	
		} 
		catch (IOException e) {
			System.out.println(e.toString());
		}
	}	
}