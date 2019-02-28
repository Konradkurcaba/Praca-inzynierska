package Threads;

import AmazonS3.AmazonS3SummaryMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;


public class RefreshService extends Service<ObservableList<ObjectMetadataIf>> {

	
	final private HelpersBundle supportersBundle;
	final private FileServer server;
	
	public RefreshService(HelpersBundle aBundle,ObjectMetadataIf aObjectMetadata) {
		supportersBundle = aBundle;
		server = aObjectMetadata.getFileServer();
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				switch(server)
				{
					case AmazonS3: return supportersBundle.getAmazonS3Supporter().getFilesFromCurrentDir();
					case Komputer: return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
					case GoogleDrive: return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
					default: throw new IllegalArgumentException("Bad FileServer");
				}
			}
		};
	}
}
