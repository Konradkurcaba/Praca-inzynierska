package AmazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AmazonS3FileDeleting {

	public void deleteObject(AmazonS3 aClient,String aBucketName,String aKey)
	{
		aClient.deleteObject(aBucketName, aKey);
	}
	
	public void deleteObjectsByPrefix(AmazonS3 aClient,String aBucketName,String aPrefix)
	{
		ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(aBucketName).withPrefix(aPrefix);
		ListObjectsV2Result listResult;
		
		listResult = aClient.listObjectsV2(listRequest);
		
		for(S3ObjectSummary object: listResult.getObjectSummaries() )
		{
			deleteObject(aClient,aBucketName,object.getKey());
		}
		
	}
	
}
