package AmazonS3;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;

public class AmazonS3FileDownloader {

	
	
	public List<Bucket> getAllBucketsList(AmazonS3 aS3Client)
	{
		List<Bucket> buckets = aS3Client.listBuckets();
		return buckets;
	}
	
	public ListObjectsV2Result getFilesFromBucket(AmazonS3 aS3Client,String bucketName)
	{
		ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(bucketName);
		ListObjectsV2Result listResult;
		
		listResult = aS3Client.listObjectsV2(listRequest);
		return listResult;
	}
	
	
	
}
