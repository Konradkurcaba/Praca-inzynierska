package Threads;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

public class NewFolderService extends Service<ObservableList<ObjectMetaDataIf>> {

	
	private final SupportersBundle supportersBundle;
	private final FileServer fileServer;
	private final String folderName;
	
	
	public NewFolderService(SupportersBundle aSupportersBundle, FileServer aFileServer,String aFolderName) {
		supportersBundle = aSupportersBundle;
		fileServer = aFileServer;
		folderName = aFolderName;
	}
	
	
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>(){

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
				switch(fileServer)
				{
				case Local:
					supportersBundle.getLocalFileSupporter().createFolder(folderName);
					return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
				case Amazon:
					supportersBundle.getAmazonS3Supporter().createFolder(folderName);
					return supportersBundle.getAmazonS3Supporter().getFilesFromCurrentDir();
				case Google:
					supportersBundle.getGoogleDriveSupporter().createFolder(folderName);
					return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
				default: throw new IllegalArgumentException("Bad file server, expected Local,Amazon or Google");
				}
			}};
	}

}
