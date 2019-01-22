package Threads;

import com.google.api.services.drive.Drive;

import GoogleDrive.GoogleDriveHelper;
import GoogleDrive.GoogleFileMetadata;
import GoogleDrive.GoogleFileType;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.PreviousContainer;


public class GoogleObjectClickService extends Service<ObservableList<ObjectMetadataIf>> {

	private final GoogleDriveHelper driveSupporter;
	private final ObjectMetadataIf clickedObject;
	
	public GoogleObjectClickService(GoogleDriveHelper aDriveSupporter,ObjectMetadataIf aClickedObject) {
		driveSupporter = aDriveSupporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>() {

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				if(clickedObject instanceof GoogleFileMetadata)
				{
					GoogleFileMetadata googleFileMetadata = (GoogleFileMetadata) clickedObject;
					if(googleFileMetadata.getFileType() == GoogleFileType.Folder)
					{
						return driveSupporter.getFilesList(googleFileMetadata.getOrginalObject().getId());
					}
				}
				else if(clickedObject instanceof PreviousContainer)
				{
					return driveSupporter.backToPreviousContainer();
				}
				throw new IllegalArgumentException("It isn't a folder");
			}
		};
	}

}
