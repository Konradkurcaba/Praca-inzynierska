package Threads;

import java.sql.SQLException;
import java.util.Map;

import Synchronization.DatabaseSupervisor;
import Synchronization.SyncFileData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetSyncInfo extends Service< Map<SyncFileData,SyncFileData>> {

	@Override
	protected Task<Map<SyncFileData, SyncFileData>> createTask() {
		return new Task<Map<SyncFileData, SyncFileData>>(){
			@Override
			protected Map<SyncFileData, SyncFileData> call() throws Exception {
				DatabaseSupervisor dbSupervisor = null;
				try {
					dbSupervisor = new DatabaseSupervisor();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return dbSupervisor.getSyncMap();
			}
		};
		
	}

	
	
	
	
}
