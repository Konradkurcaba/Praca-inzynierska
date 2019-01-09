package AmazonS3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;

public class AmazonS3LogInSupporter {

	public AmazonS3 getAmazonS3Client(String AccessKey,String SecretKey)
	{
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(AccessKey, SecretKey);
		AmazonS3 amazonS3 = AmazonS3Client.builder()
				.withRegion(Regions.AP_NORTHEAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.build();
		return amazonS3;
	}
}
