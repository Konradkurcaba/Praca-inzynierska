package Threads;

import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

public class ChangeNameService extends Service<ObservableList<ObjectMetaDataIf>>{

	private final SupportersBundle supportersBundle;
	private final ObjectMetaDataIf objectToChange;
	private final String newName;
	
	public ChangeNameService(SupportersBundle aSupportersBundle,ObjectMetaDataIf aObject,String aNewName) {
		supportersBundle = aSupportersBundle;
		objectToChange = aObject;
		newName = aNewName;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>() {

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
				switch(objectToChange.getFileServer())
				{
					case Amazon:
						
					case Google:
						supportersBundle.getGoogleDriveSupporter().changeName((GoogleFileMetadata)objectToChange,newName);
						return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
						
					case Local:
						supportersBundle.getLocalFileSupporter().changeName((LocalFileMetadata)objectToChange, newName);
						return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
					default: throw new IllegalArgumentException("Google, Amazon or Local Server expected");
				}
			}
		};
	}

}
