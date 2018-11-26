package Threads;

import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;

public class GoogleDriveDownloadService extends Service<ObservableList<ObjectMetaDataIf>> {

	GoogleDriveSupporter driveSupporter;
	
	public GoogleDriveDownloadService(GoogleDriveSupporter aDriveSupporter) {
		driveSupporter = aDriveSupporter;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		
		return new Task()
		{

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
				return driveSupporter.getFilesList("root");
			}
	
		};
		
	}
	
}
