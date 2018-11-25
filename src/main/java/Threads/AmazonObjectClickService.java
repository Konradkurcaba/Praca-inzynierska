package Threads;

import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3ObjectMetadata;
import AmazonS3.AmazonS3Supporter;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.PreviousContainer;

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
					ObservableList<ObjectMetaDataIf> files = s3Supporter.listBucketFiles((AmazonS3BucketMetadata)clickedObject);
					return files;
				}
				else if(clickedObject instanceof PreviousContainer )
				{
					ObservableList<ObjectMetaDataIf> files = s3Supporter.getFilesFromPreviousContainer();
					return files;
				}else if(clickedObject instanceof AmazonS3ObjectMetadata)
				{
					AmazonS3ObjectMetadata s3ObjectMetadata = (AmazonS3ObjectMetadata) clickedObject;
					if (s3ObjectMetadata.isDirectory())
					{
						ObservableList<ObjectMetaDataIf> files = s3Supporter.listFiles(s3ObjectMetadata.getName());
						return files;
					}
					else throw new IllegalArgumentException("Given object is not a Container");
					
				}
				else throw new IllegalArgumentException("Passed argument cannot be recognized");
			}
		};
	}
}
