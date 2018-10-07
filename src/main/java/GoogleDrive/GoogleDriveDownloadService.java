package GoogleDrive;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GoogleDriveDownloadService extends Service<ObservableList<GoogleFileMetadata>> {

	GoogleDriveSupporter driveSupporter;
	
	public GoogleDriveDownloadService(GoogleDriveSupporter aDriveSupporter) {
		driveSupporter = aDriveSupporter;
	}
	
	@Override
	protected Task<ObservableList<GoogleFileMetadata>> createTask() {
		
		return new Task()
		{

			@Override
			protected ObservableList<GoogleFileMetadata> call() throws Exception {
				return driveSupporter.getFilesList();
			}
	
		};
		
	}
	
}
