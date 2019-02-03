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
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;

public class CheckIfFilesAreSyncTarget extends Service<Boolean> {


	ObjectMetadataIf sourceFile;
	ObjectMetadataIf targetFile;
	HelpersBundle supportersBundle;
	
	public CheckIfFilesAreSyncTarget(ObjectMetadataIf aSourceFile,ObjectMetadataIf aTargetFile,HelpersBundle aHelpersBundle) {
		sourceFile = aSourceFile;
		targetFile = aTargetFile;
		supportersBundle = aHelpersBundle;
	}
	
	@Override
	protected Task createTask() {
		return new Task(){
			@Override
			protected Boolean call() throws Exception {
				SyncFileData sourceSyncFile = createSyncFileData(sourceFile);
				SyncFileData targetSyncFile = createSyncFileData(targetFile);
				DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
				if(dbSupervisor.checkWhetherFileIsSyncTarget(sourceSyncFile)) return true;
				if(dbSupervisor.checkWhetherFileIsSyncTarget(targetSyncFile)) return true;
				return false;
			}
		};
	}
	
	private SyncFileData createSyncFileData(ObjectMetadataIf aObj)
	{
		SyncFileData file = null;
		if(aObj.getFileServer() == FileServer.AmazonS3)
		{
			file = new S3SyncFileData(aObj
				,supportersBundle.getAmazonS3Supporter().getAccountName());
		}
		else if(aObj.getFileServer() == FileServer.GoogleDrive)
		{
			file = new SyncFileData(aObj,supportersBundle.getGoogleDriveSupporter().getAccountName());
		}else {
			file = new SyncFileData(aObj,null);
		}
		return file;
	}
}