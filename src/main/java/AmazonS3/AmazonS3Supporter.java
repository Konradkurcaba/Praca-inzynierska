package AmazonS3;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.PreviousContainer;

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
	
	public ObservableList<ObjectMetaDataIf> listBucketFiles(AmazonS3BucketMetadata aBucket)
	{
		if(!isLoggedIn)
		{
			getClient();
		}
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		ListObjectsV2Result listResult = s3Downloader.getFilesFromBucket(s3Client, aBucket.getName());
		
		AmazonS3Converter s3Converter = new AmazonS3Converter();
		List<ObjectMetaDataIf> convertedList = s3Converter.convertFileList(listResult);
		PreviousContainer previousContainer = new PreviousContainer(aBucket);
		convertedList.add(0,previousContainer);
		return FXCollections.observableArrayList(convertedList);
	}
	
	public ObservableList getFilesFromPreviousContainer(PreviousContainer aContainer)
	{
		if(aContainer.getOrginalObject() instanceof AmazonS3BucketMetadata)
		{
			return getBucketsMetadata();
		}
		return null;
	}
	
	
}
