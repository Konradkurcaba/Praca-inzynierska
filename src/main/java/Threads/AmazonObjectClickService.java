package Threads;

import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3Supporter;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonObjectClickService extends Service<ObservableList<ObjectMetaDataIf>> {

	
	private final AmazonS3Supporter s3Supporter;
	private final ObjectMetaDataIf clickedObject;
	
	public AmazonObjectClickService(AmazonS3Supporter aS3Supporter, ObjectMetaDataIf aClickedObject) {
		s3Supporter = aS3Supporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		
		return new Task<ObservableList<ObjectMetaDataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
				if(clickedObject instanceof AmazonS3BucketMetadata)
				{
					ObservableList<ObjectMetaDataIf> files = s3Supporter.listBucketFiles(clickedObject.getName());
					return files;
				}
				else return null;
			}
		};
	}
}
