package Threads;

import AmazonS3.AmazonS3SummaryMetadata;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;

public class ChangeNameService extends Service<ObservableList<ObjectMetadataIf>>{

	private final HelpersBundle supportersBundle;
	private final ObjectMetadataIf objectToChange;
	private final String newName;
	
	public ChangeNameService(HelpersBundle aSupportersBundle,ObjectMetadataIf aObject,String aNewName) {
		supportersBundle = aSupportersBundle;
		objectToChange = aObject;
		newName = aNewName;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>() {

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				switch(objectToChange.getFileServer())
				{
					case AmazonS3:
						supportersBundle.getAmazonS3Supporter().changeName((AmazonS3SummaryMetadata) objectToChange, newName );
						return supportersBundle.getAmazonS3Supporter().getFilesFromCurrentDir();
					case GoogleDrive:
						supportersBundle.getGoogleDriveSupporter().changeName((GoogleFileMetadata)objectToChange,newName);
						return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
					case Komputer:
						supportersBundle.getLocalFileSupporter().changeName((LocalFileMetadata)objectToChange, newName);
						return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
					default: throw new IllegalArgumentException("Google, Amazon or Local Server expected");
				}
			}
		};
	}

}
