package AmazonS3;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

public class AmazonS3FileDownloader {

	
	
	public List<Bucket> getAllBucketsList(AmazonS3 aS3Client)
	{
		List<Bucket> buckets = aS3Client.listBuckets();
		return buckets;
	}
	
	
	
}
