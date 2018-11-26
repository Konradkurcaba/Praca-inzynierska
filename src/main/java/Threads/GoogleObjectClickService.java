package Threads;

import com.google.api.services.drive.Drive;

import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import GoogleDrive.GoogleFileType;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.PreviousContainer;


public class GoogleObjectClickService extends Service<ObservableList<ObjectMetaDataIf>> {

	private final GoogleDriveSupporter driveSupporter;
	private final ObjectMetaDataIf clickedObject;
	
	public GoogleObjectClickService(GoogleDriveSupporter aDriveSupporter,ObjectMetaDataIf aClickedObject) {
		driveSupporter = aDriveSupporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>() {

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
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
