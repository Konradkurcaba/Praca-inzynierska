package Threads;

import GoogleDrive.GoogleDriveHelper;
import GoogleDrive.GoogleFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;

public class GoogleDriveDownloadService extends Service<ObservableList<ObjectMetadataIf>> {

	GoogleDriveHelper driveSupporter;
	
	public GoogleDriveDownloadService(GoogleDriveHelper aDriveSupporter) {
		driveSupporter = aDriveSupporter;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		
		return new Task()
		{

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				return driveSupporter.getFilesList("root");
			}
	
		};
		
	}
	
}
