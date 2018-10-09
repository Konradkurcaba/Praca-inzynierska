package AmazonS3;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3Supporter {

	
	private boolean isLoggedIn = false;
	private AmazonS3 s3Client;
	
	private void getClient()
	{
		AmazonS3LogInSupporter loginSupporter = new AmazonS3LogInSupporter();
		s3Client = loginSupporter.getAmazonS3Client();
		isLoggedIn = true;
	}
	
	
	public ObservableList<AmazonS3BucketMetadata> getBucketsMetadata()
	{
		if(!isLoggedIn)
		{
			getClient();
		}
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		List<Bucket> buckets = s3Downloader.getAllBucketsList(s3Client);
		AmazonS3Converter s3Converter = new AmazonS3Converter();
		List<AmazonS3BucketMetadata> bucketsMetadata = s3Converter.convertBuckets(buckets);
		return FXCollections.observableArrayList(bucketsMetadata);
	}
	
	public ObservableList<ObjectMetaDataIf> listBucketFiles(String bucketName)
	{
		if(!isLoggedIn)
		{
			getClient();
		}
		ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(bucketName);
		ListObjectsV2Result listResult;
		
		listResult = s3Client.listObjectsV2(listRequest);
		for(S3ObjectSummary objectSummary : listResult.getObjectSummaries())
		{
			System.out.println(objectSummary.getKey());
		}
		return null;
	}
	
	
}
