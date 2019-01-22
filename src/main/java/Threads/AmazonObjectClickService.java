package Threads;

import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3SummaryMetadata;
import AmazonS3.AmazonS3Helper;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.PreviousContainer;

public class AmazonObjectClickService extends Service<ObservableList<ObjectMetadataIf>> {

	
	private final AmazonS3Helper s3Supporter;
	private final ObjectMetadataIf clickedObject;
	
	public AmazonObjectClickService(AmazonS3Helper aS3Supporter, ObjectMetadataIf aClickedObject) {
		s3Supporter = aS3Supporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		
		return new Task<ObservableList<ObjectMetadataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				if(clickedObject instanceof AmazonS3BucketMetadata)
				{
					ObservableList<ObjectMetadataIf> files = s3Supporter.listBucketFiles((AmazonS3BucketMetadata)clickedObject);
					return files;
				}
				else if(clickedObject instanceof PreviousContainer )
				{
					ObservableList<ObjectMetadataIf> files = s3Supporter.getFilesFromPreviousContainer();
					return files;
				}else if(clickedObject instanceof AmazonS3SummaryMetadata)
				{
					AmazonS3SummaryMetadata s3ObjectMetadata = (AmazonS3SummaryMetadata) clickedObject;
					if (s3ObjectMetadata.isDirectory())
					{
						ObservableList<ObjectMetadataIf> files = s3Supporter.listFiles(s3ObjectMetadata.getName());
						return files;
					}
					else throw new IllegalArgumentException("Given object is not a Container");
					
				}
				else throw new IllegalArgumentException("Passed argument cannot be recognized");
			}
		};
	}
}
