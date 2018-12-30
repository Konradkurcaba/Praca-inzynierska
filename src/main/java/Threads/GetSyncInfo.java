package Threads;

import java.sql.SQLException;
import java.util.Map;

import Synchronization.DatabaseSupervisor;
import Synchronization.SyncFileData;
import Synchronization.Synchronizer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetSyncInfo extends Service< Map<SyncFileData,SyncFileData>> {

	Synchronizer synchronizer;
	
	public GetSyncInfo(Synchronizer aSynchronizer) {
		synchronizer = aSynchronizer;
	}
	
	@Override
	protected Task<Map<SyncFileData, SyncFileData>> createTask() {
		return new Task<Map<SyncFileData, SyncFileData>>(){
			@Override
			protected Map<SyncFileData, SyncFileData> call() throws Exception {
				return synchronizer.getSyncInfo();
			}
		};
		
	}

	
	
	
	
}
