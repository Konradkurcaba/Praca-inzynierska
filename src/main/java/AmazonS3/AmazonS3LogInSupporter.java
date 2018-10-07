package AmazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AmazonS3LogInSupporter {

	
	
	public AmazonS3 getAmazonS3Client()
	{
		AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		return s3;
	}
	
	
}
