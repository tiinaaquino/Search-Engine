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
		
//		@Test
		
		
		
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
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
//		@Test
		
		
		
		
	}
}
