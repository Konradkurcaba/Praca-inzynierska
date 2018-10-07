package AmazonS3;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
	
	
	
}
