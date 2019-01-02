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

public class CheckWhetherFileIsSyncTarget extends Service<Boolean> {


	ObjectMetaDataIf file;
	
	public CheckWhetherFileIsSyncTarget(ObjectMetaDataIf aFile) {
		file = aFile;
	}
	
	@Override
	protected Task createTask() {
		return new Task(){
			@Override
			protected Boolean call() throws Exception {
				SyncFileData syncFileData;
				if(file.getFileServer() == FileServer.Amazon) syncFileData = new S3SyncFileData(file);
				else syncFileData = new SyncFileData(file);
				DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
				return dbSupervisor.checkWhetherFileIsSyncTarget(syncFileData);
			}
		};
	}
}