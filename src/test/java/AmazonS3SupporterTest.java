import org.junit.Test;

import AmazonS3.AmazonS3Helper;

public class AmazonS3SupporterTest {

	
	
	@Test
	public void amazonS3SupporterTest()
	{
		AmazonS3Helper sup = new AmazonS3Helper();
		sup.getAmazons3ObjMetadata("nowy.txt", "aws-bucket-5");
	}
	
	
	
}
