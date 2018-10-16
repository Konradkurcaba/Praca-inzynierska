package Threads;


import com.amazonaws.services.s3.AmazonS3Client;

import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3Supporter;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class AmazonS3DownloadBucketsService extends Service<ObservableList<AmazonS3BucketMetadata>>{

	AmazonS3Supporter amazonS3Supporter;
	
	
	public AmazonS3DownloadBucketsService(AmazonS3Supporter aAmazonS3Supporter) {
		amazonS3Supporter = aAmazonS3Supporter;
	}


	@Override
	protected Task<ObservableList<AmazonS3BucketMetadata>> createTask() {
		
		return new Task<ObservableList<AmazonS3BucketMetadata>>() {

			@Override
			protected ObservableList<AmazonS3BucketMetadata> call() throws Exception {
				return amazonS3Supporter.getBucketsMetadata();
			}
		};
	}
	
	
	
	
	
	
}
