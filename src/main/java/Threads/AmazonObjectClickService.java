package Threads;

import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3Supporter;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonObjectClickService extends Service {

	
	private final AmazonS3Supporter s3Supporter;
	private final ObjectMetaDataIf clickedObject;
	
	public AmazonObjectClickService(AmazonS3Supporter aS3Supporter, ObjectMetaDataIf aClickedObject) {
		s3Supporter = aS3Supporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task createTask() {
		
		if(clickedObject instanceof AmazonS3BucketMetadata)
		{
			s3Supporter.listBucketFiles(clickedObject.getName());
		}
		
		
		
		return null;
	}

}
