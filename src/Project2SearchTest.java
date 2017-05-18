import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;


@RunWith(Enclosed.class)
public class Project2SearchTest {
	@Rule
	public TestRule globalTimeout = Timeout.seconds(30);
	
	
	public static class ExactTest {
		private String[] sampleWords;
		private InvertedIndex index;
		
		@Before
		public void setup() {
			index = new InvertedIndex();
		}
		
		@Test
		public void testExactSearch01() {
			index.add("van", "", 5);
			index.add("car", "path1", 8);
			index.add("automobile", "path2", 79);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.exactSearch( new String[]{"vancouver"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		}
		
		@Test
		public void testExactSearch02() {
			index.add("hater", "", 1);
			index.add("that", "path1", 2);
			index.add("hate", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.exactSearch( new String[]{"hat"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		}
		
		
		@Test
		public void testExactSearch03() {
			index.add("courageous", "", 1);
			index.add("car", "path1", 2);
			index.add("current", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.exactSearch( new String[]{"cur"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		}
		
		
		
		@Test
		public void testExactSearch04() {
			index.add("apple", "", 1);
			index.add("application", "path1", 2);
			index.add("appetite", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.exactSearch( new String[]{"app"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		}
		
		
		
		@Test
		public void testExactSearch05() {
			index.add("basketball", "", 1);
			index.add("football", "path1", 2);
			index.add("baseball", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.exactSearch( new String[]{"ball"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		}		
		
	}
	
	public static class PartialTest {
		private String[] sampleWords;
		private InvertedIndex index;
		
		@Before
		public void setup() {
			index = new InvertedIndex();
		}
		
		@Test
		public void testPartialSearch01() {
			index.add("courageous", "", 1);
			index.add("car", "path1", 2);
			index.add("current", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.partialSearch( new String[]{"cur"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		
		}
		
		
		
		@Test
		public void testPartialSearch02() {
			index.add("pot", "", 1);
			index.add("path", "path1", 2);
			index.add("pen", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.partialSearch( new String[]{"pot"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		
		}
		
		
		
		@Test
		public void testPartialSearch03() {
			index.add("pot", "", 1);
			index.add("path", "path1", 2);
			index.add("pen", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.partialSearch( new String[]{"pot", "path", "pen"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		
		}
		
		
		
		@Test
		public void testPartialSearch04() {
			index.add("popper", "", 1);
			index.add("popping", "path1", 2);
			index.add("popped", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.partialSearch( new String[]{"pop"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		
		}
		
		
		
		@Test
		public void testPartialSearch05() {
			index.add("there", "", 1);
			index.add("theater", "path1", 2);
			index.add("seathe", "path2", 3);
			
			System.out.println(index);
			ArrayList<SearchResult> result = index.partialSearch( new String[]{"the"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertNotEquals(result, expected);
		
		}
		
		
	}
}
