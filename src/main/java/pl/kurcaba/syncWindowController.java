package pl.kurcaba;

import java.sql.SQLException;
import java.util.Map;

import Synchronization.DatabaseSupervisor;
import Synchronization.SyncFileData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;

public class syncWindowController {

	
	@FXML
	private TableView<SyncFileData> syncTable;

	public void getSyncInfo() throws SQLException
	{
		DatabaseSupervisor supervisor = new DatabaseSupervisor();
		Map<SyncFileData,SyncFileData> syncMap = supervisor.getSyncMap();
		
		ObservableMap<SyncFileData> data = FXCollections.observableArrayList(syncMap);
		
		for(Map.Entry<SyncFileData, SyncFileData> entry : aFilesToSynchonize.entrySet())
		{
			syncTable.getColumns().
		}
		
	
	}
	
	
	
	
	
}
