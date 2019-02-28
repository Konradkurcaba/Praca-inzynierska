package Threads;

import java.sql.SQLException;
import java.util.Map;

import Synchronization.DatabaseSupervisor;
import Synchronization.SyncFileData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DeleteSyncInfoService extends Service {

	private SyncFileData sourceFile;
	private SyncFileData targetFile;
	
	
	public DeleteSyncInfoService(SyncFileData aSourceFile,SyncFileData aTargetFile) {
		sourceFile = aSourceFile;
		targetFile = aTargetFile;
	}
	
	
	@Override
	protected Task createTask() {
		return new Task(){
			@Override
			protected Boolean call() throws Exception {
				DatabaseSupervisor dbSupervisor = null;
				try {
					dbSupervisor = new DatabaseSupervisor();
					dbSupervisor.removeSyncData(sourceFile, targetFile);
					dbSupervisor.closeConnection();
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		};
		
	}
	
}
