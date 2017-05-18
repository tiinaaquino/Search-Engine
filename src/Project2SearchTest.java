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
			ArrayList<SearchResult> result = index.exactSearch( new String[]{"van", "car", "automobile"});
			System.out.println(result);
			
			ArrayList<SearchResult> expected = new ArrayList<SearchResult>();
			expected.add(new SearchResult("", 1, 5));
			expected.add(new SearchResult("path1", 1, 8));
			System.out.println(expected);
			
			Assert.assertEquals(result, expected);
		}
		
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
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
			
		}
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
		
	}
}
