package Threads;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;

public class NewFolderService extends Service<ObservableList<ObjectMetadataIf>> {

	
	private final HelpersBundle supportersBundle;
	private final FileServer fileServer;
	private final String folderName;
	
	
	public NewFolderService(HelpersBundle aSupportersBundle, FileServer aFileServer,String aFolderName) {
		supportersBundle = aSupportersBundle;
		fileServer = aFileServer;
		folderName = aFolderName;
	}
	
	
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>(){

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				switch(fileServer)
				{
				case Komputer:
					supportersBundle.getLocalFileSupporter().createFolder(folderName);
					return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
				case AmazonS3:
					supportersBundle.getAmazonS3Supporter().createFolder(folderName);
					return supportersBundle.getAmazonS3Supporter().getFilesFromCurrentDir();
				case GoogleDrive:
					supportersBundle.getGoogleDriveSupporter().createFolder(folderName);
					return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
				default: throw new IllegalArgumentException("Bad file server, expected Local,Amazon or Google");
				}
			}};
	}

}
