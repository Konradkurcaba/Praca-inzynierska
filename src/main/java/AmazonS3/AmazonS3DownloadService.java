package AmazonS3;


import com.amazonaws.services.s3.AmazonS3Client;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class AmazonS3DownloadService extends Service<ObservableList<AmazonS3BucketMetadata>>{

	AmazonS3Supporter amazonS3Supporter;
	
	
	public AmazonS3DownloadService(AmazonS3Supporter aAmazonS3Supporter) {
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
