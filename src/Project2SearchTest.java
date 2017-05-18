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
		private String[] words;
		private InvertedIndex index;
		
		@Before
		public void setup() {
			index = new InvertedIndex();
		}
	}
	
	public static class PartialTest {
		private String[] words;
		private InvertedIndex index;
		
		@Before
		public void setup() {
			index = new InvertedIndex();
		}
	}
}
