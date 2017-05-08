import java.io.IOException;
import java.nio.file.Path;

public interface QueryInterface {
	
	/**
	 * Parses the query file, cleans the texts, sorts each line,
	 * then stores it in a list.
	 * 
	 * @param path
	 * 				path of query
	 * @return sorted list of queries
	 * @throws IOException
	 */
	public void parse(Path path, boolean exact) throws IOException;
	
	/**
	 * writes object to JSON format
	 * 
	 * @param path
	 * 			path to input
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException;

}