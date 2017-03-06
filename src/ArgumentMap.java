import java.util.HashMap;
import java.util.Map;

// TODO Homework: Fill in or fix all of the methods with TODO comments.

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
		for (String elem : args) {
			if (isFlag(elem) == true){
				map.put(elem, null);
			}
		}
		for (String elem : args) {
			if (isValue(elem) == true){
				String valLetter = String.valueOf(elem.charAt(0));
				for (String entry : map.keySet()){
					String keyLetter = String.valueOf(entry.charAt(1));
					if (valLetter.equals(keyLetter))
						map.replace(entry, elem);
					else
						map.replace(null, elem);
				}
			}
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
			return false;
		if (arg.length() > 1){
				if (arg.startsWith("-") == true || arg.equals("") == true){
					return false;
				}
				else
					return true;
		}
		else
			return false; 
	}

	/**
	 * Returns the number of unique flags stored in the argument map.
	 *
	 * @return number of flags
	 */
	public int numFlags() {
		int count = 0;

		for (Map.Entry<String, String> flag: map.entrySet()){
			if (!isFlag(flag.getValue()) == true) {
				map.remove(flag);
				count = (int) map.values().stream().distinct().count();
			}
			else 
				return count;
		}
		return count;
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
		if (map.containsKey(flag))
			return true;
		return false; 
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
		if (map.containsValue(flag) && map.get(flag)!= null)
			return true;
		return false; 
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
			if (map.get(flag) != null)
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
		return defaultValue;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
