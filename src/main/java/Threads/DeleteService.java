package Threads;

import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3Client;
import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3Supporter;
import Local.LocalFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;


public class DeleteService extends Service<ObservableList<ObjectMetaDataIf>>{

	
	private final SupportersBundle supportersBundle;
	private final ObjectMetaDataIf objectToDelete;
	
	public DeleteService(SupportersBundle aSupportersBundle, ObjectMetaDataIf aObjectToDelete) {
		supportersBundle = aSupportersBundle;
		objectToDelete = aObjectToDelete;
	}
	
	
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws IOException {
				if(objectToDelete instanceof LocalFileMetadata)
				{
					supportersBundle.getLocalFileSupporter().deleteFile((LocalFileMetadata)objectToDelete);
					return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
				}else throw new IllegalArgumentException("Cannot recognize object");
			}
			
		};
	}

}
