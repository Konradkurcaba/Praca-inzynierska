package Threads;

import java.sql.SQLException;
import java.util.Map;

import Synchronization.DatabaseSupervisor;
import Synchronization.SyncFileData;
import Synchronization.Synchronizer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateSyncKeyService extends Service< Map<SyncFileData,SyncFileData>> {

	private Synchronizer synchronizer;
	private String oldKey;
	private String newKey;
	
	public UpdateSyncKeyService(Synchronizer aSynchronizer,String aOldKey,String aNewKey) {
		synchronizer = aSynchronizer;
		oldKey = aOldKey;
		newKey = aNewKey;
		
	}
	
	@Override
	protected Task createTask() {
		return new Task(){
			@Override
			protected Object call() throws Exception {
				synchronizer.updateFileKey(oldKey, newKey);
				return true;
			}
		};
		
	}
}