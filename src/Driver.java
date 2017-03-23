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
			if (!map.hasFlag("-index") && !map.hasFlag("-path")) {
				System.out.println("No path and index flag input");
			}
		
			if (!map.hasFlag("-index") && map.hasFlag("-path")) {
				if (map.hasValue("-path")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
				}
			}
		
			if (map.hasFlag("-index") && !map.hasFlag("-path")) {
				if (map.hasValue("-index")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
				}
				index.asJSON(Paths.get(defaultValue));
				System.out.println("contains just index flag");
			}
		
			if (map.hasFlag("-index") && map.hasFlag("-path")) {	
				if (map.hasValue("-index") && map.hasValue("-path")) {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
					index.asJSON(Paths.get(map.getValue("-index")));		
				}
			
				if (map.hasValue("-path") && !map.hasValue("-index"))  {
					InvertedIndexBuilder.traverseDirectory(Paths.get(map.getValue("-path")), index);
					index.asJSON(Paths.get(defaultValue));
				}
			
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