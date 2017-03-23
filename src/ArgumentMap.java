import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line arguments into flag/value pairs, and stores those pairs
 * in a map for easy access.
 */
public class ArgumentMap {

	private final Map<String, String> map;

	/**
	 * Initializes the argument map.
	 */
	public ArgumentMap() {
		map = new HashMap<>();
	}

	/**
	 * Initializes the argument map and parses the specified arguments into
	 * key/value pairs.
	 *
	 * @param args
	 *            command line arguments
	 *
	 * @see #parse(String[])
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the specified arguments into key/value pairs and adds them to the
	 * argument map.
	 *
	 * @param args
	 *            command line arguments
	 */
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if ((isFlag(args[i]))) {
				map.put(args[i], null);
			}
			
			if (i == args.length - 1 && isFlag(args[i])) {
				map.put(args[i], null);
			}
			
			else if (isFlag(args[i])) {	
				if (isFlag(args[i+1])) {
					map.put(args[i], null);
				}
				
				else {
					map.put(args[i], args[i+1]);
				}
			}
			
			// TODO Whenever we see repeated code, there is a way to rethink the problem
			
//			if (isFlag(args[i]) && i < args.length && !isFlag(args[i+1])) {
//				map.put(args[i], args[i+1]);
//			}
//			else if (isFlag(args[i])) {
//				map.put(args[i], null);
//			}
		}
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static boolean isFlag(String arg) {
		if (arg != null) {
			String temp = arg.trim();
			if (temp.startsWith("-") == true && temp.length() > 1) {
				return true;
			}
		}
		return false; 
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static boolean isValue(String arg) {
		if (arg == null)
		{
			return false;
		}
		if (arg.trim().startsWith("-") || arg.trim().isEmpty())
		{
			return false;
		}
		return true;
	}

	/**
	 * Returns the number of unique flags stored in the argument map.
	 *
	 * @return number of flags
	 */
	public int numFlags() {
		return map.size();
	}

	/**
	 * Determines whether the specified flag is stored in the argument map.
	 *
	 * @param flag
	 *            flag to test
	 *
	 * @return true if the flag is in the argument map
	 */
	public boolean hasFlag(String flag) {
		return map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is stored in the argument map and
	 * has a non-null value stored with it.
	 *
	 * @param flag
	 *            flag to test
	 *
	 * @return true if the flag is in the argument map and has a non-null value
	 */
	public boolean hasValue(String flag) {
		return map.get(flag) != null;
	}

	/**
	 * Returns the value for the specified flag as a String object.
	 *
	 * @param flag
	 *            flag to get value for
	 *
	 * @return value as a String or null if flag or value was not found
	 */
	public String getString(String flag) {
		return map.get(flag); 
	}

	/**
	 * Returns the value for the specified flag as a String object. If the flag
	 * is missing or the flag does not have a value, returns the specified
	 * default value instead.
	 *
	 * @param flag
	 *            flag to get value for
	 * @param defaultValue
	 *            value to return if flag or value is missing
	 * @return value of flag as a String, or the default value if the flag or
	 *         value is missing
	 */
	public String getString(String flag, String defaultValue) {
		if (hasFlag(flag) == true){
			if (map.get(flag) != null) // TODO Only need this test really
				return map.get(flag);
			else
				return defaultValue;
		}
		return defaultValue; 
	}

	/**
	 * Returns the value for the specified flag as an int value. If the flag is
	 * missing or the flag does not have a value, returns the specified default
	 * value instead.
	 *
	 * @param flag
	 *            flag to get value for
	 * @param defaultValue
	 *            value to return if the flag or value is missing
	 * @return value of flag as an int, or the default value if the flag or
	 *         value is missing
	 */
	public int getInteger(String flag, int defaultValue) {	
		try {
			return Integer.parseInt(map.get(flag));
		}
		catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value for the specified flag.
	 * 
	 * @param flag
	 * 				flag to get value for
	 * @return value of flag
	 */
	public String getValue(String flag) {
		return map.get(flag);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
