package pl.kurcaba;


import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;


public class MainAWS {

	
	
	 
	public static void main(String... args)
	{
		
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		List<Bucket> buckets = s3.listBuckets();
		System.out.println("Your Amazon S3 buckets are:");
		for (Bucket b : buckets) {
		    System.out.println("* " + b.getName());
		}
		
		
		
	}
	
	
}
