import org.junit.Test;

import AmazonS3.AmazonS3Supporter;

public class AmazonS3SupporterTest {

	
	
	@Test
	public void amazonS3SupporterTest()
	{
		AmazonS3Supporter sup = new AmazonS3Supporter();
		sup.getAmazons3ObjMetadata("nowy.txt", "aws-bucket-5");
	}
	
	
	
}
