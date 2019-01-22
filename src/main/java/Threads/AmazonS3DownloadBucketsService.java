package Threads;


import com.amazonaws.services.s3.AmazonS3Client;

import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3Helper;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;

public class AmazonS3DownloadBucketsService extends Service<ObservableList<ObjectMetadataIf>>{

	AmazonS3Helper amazonS3Supporter;
	
	
	public AmazonS3DownloadBucketsService(AmazonS3Helper aAmazonS3Supporter) {
		amazonS3Supporter = aAmazonS3Supporter;
	}


	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		
		return new Task<ObservableList<ObjectMetadataIf>>() {

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				return amazonS3Supporter.getBucketsMetadata();
			}
		};
	}
	
	
	
	
	
	
}
