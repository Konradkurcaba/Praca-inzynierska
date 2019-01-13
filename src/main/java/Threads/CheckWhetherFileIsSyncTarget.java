package Threads;

import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;

import Synchronization.DatabaseSupervisor;
import Synchronization.S3SyncFileData;
import Synchronization.SyncFileData;
import Synchronization.Synchronizer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

public class CheckWhetherFileIsSyncTarget extends Service<Boolean> {


	ObjectMetaDataIf fileToCheck;
	SupportersBundle supportersBundle;
	
	public CheckWhetherFileIsSyncTarget(ObjectMetaDataIf aFile,SupportersBundle aSupportersBundle) {
		fileToCheck = aFile;
	}
	
	@Override
	protected Task createTask() {
		return new Task(){
			@Override
			protected Boolean call() throws Exception {
				
				SyncFileData file;
				if(fileToCheck.getFileServer() == FileServer.Amazon) file = new S3SyncFileData(fileToCheck
						,supportersBundle.getAmazonS3Supporter().getAccountName());
				else if(fileToCheck.getFileServer() == FileServer.Google)
				{
				file = new SyncFileData(fileToCheck,supportersBundle.getGoogleDriveSupporter().getAccountName());
				}else file = new SyncFileData(fileToCheck,null);
				
				DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
				return dbSupervisor.checkWhetherFileIsSyncTarget(file);
			}
		};
	}
}