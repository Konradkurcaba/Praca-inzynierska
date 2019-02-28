package Threads;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.amazonaws.services.s3.AmazonS3Client;
import AmazonS3.AmazonS3BucketMetadata;
import AmazonS3.AmazonS3SummaryMetadata;
import AmazonS3.AmazonS3Helper;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;


public class DeleteService extends Service<ObservableList<ObjectMetadataIf>>{

	
	private final HelpersBundle supportersBundle;
	private final ObjectMetadataIf objectToDelete;
	
	public DeleteService(HelpersBundle aSupportersBundle, ObjectMetadataIf aObjectToDelete) {
		supportersBundle = aSupportersBundle;
		objectToDelete = aObjectToDelete;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>()
		{
			@Override
			protected ObservableList<ObjectMetadataIf> call() throws IOException, GeneralSecurityException {
				if(objectToDelete instanceof LocalFileMetadata)
				{
					supportersBundle.getLocalFileSupporter().deleteFile((LocalFileMetadata)objectToDelete);
					return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
				}
				else if(objectToDelete instanceof GoogleFileMetadata)
				{
					supportersBundle.getGoogleDriveSupporter().deleteObject((GoogleFileMetadata)objectToDelete);
					return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
				}
				else if(objectToDelete instanceof AmazonS3SummaryMetadata)
				{
					supportersBundle.getAmazonS3Supporter().deleteObject((AmazonS3SummaryMetadata) objectToDelete);
					return supportersBundle.getAmazonS3Supporter().getFilesFromCurrentDir();
				}
					else throw new IllegalArgumentException("Cannot recognize object");
			}
			
		};
	}

}
