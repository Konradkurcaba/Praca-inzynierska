package Threads;

import AmazonS3.AmazonS3ObjectMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;


public class RefreshService extends Service<ObservableList<ObjectMetaDataIf>> {

	
	final private SupportersBundle supportersBundle;
	final private FileServer server;
	
	public RefreshService(SupportersBundle aBundle,ObjectMetaDataIf aObjectMetadata) {
		supportersBundle = aBundle;
		server = aObjectMetadata.getFileServer();
	}
	
	
	
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
				switch(server)
				{
					case Amazon: return supportersBundle.getAmazonS3Supporter().getFilesFromCurrentDir();
					case Local: return supportersBundle.getLocalFileSupporter().getFilesFromCurrentDir();
					case Google: return supportersBundle.getGoogleDriveSupporter().getFilesFromCurrentDir();
					default: throw new IllegalArgumentException("Bad FileServer");
				}
			}
		};
	}
	
	
	
	

}
