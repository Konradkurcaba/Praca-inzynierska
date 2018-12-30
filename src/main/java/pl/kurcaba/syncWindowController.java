package pl.kurcaba;

import java.sql.SQLException;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import Synchronization.DatabaseSupervisor;
import Synchronization.SyncFileData;
import Synchronization.SyncPair;
import Synchronization.Synchronizer;
import Threads.DeleteSyncInfoService;
import Threads.GetSyncInfo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class SyncWindowController {
	
	public void init() throws SQLException
	{
		initTable();
		getSyncInfo();
	}
	
	@FXML
	private TableView<SyncPair> syncTable;

	private void getSyncInfo() throws SQLException
	{
		GetSyncInfo syncInfoService = new GetSyncInfo();
		syncInfoService.setOnSucceeded(event ->{
			ObservableList<SyncPair> items = FXCollections.observableArrayList((syncInfoService.getValue().entrySet().stream()
					.map(entry ->{
						return new SyncPair(entry.getKey(),entry.getValue());
					})
					.collect(Collectors.toList())));
			
			 syncTable.setItems(items);
		});
		syncInfoService.start();
	}
	
	private void initTable()
	{
		 TableColumn<SyncPair,String> sourceFileColumn = new TableColumn<SyncPair,String>("Plik Ÿrod³owy");
		 sourceFileColumn.setCellValueFactory(new PropertyValueFactory("sourceFileName"));
		 TableColumn<SyncPair,String> targetFileColumn = new TableColumn<SyncPair,String>("Plik docelowy");
		 targetFileColumn.setCellValueFactory(new PropertyValueFactory("targetFileName"));

		 initTableColumn(sourceFileColumn);
		 initTableColumn(targetFileColumn);
		 
		 syncTable.getColumns().addAll(sourceFileColumn,targetFileColumn);
	}
	
	private void initTableColumn(TableColumn tableColumn)
	{
		tableColumn.setCellFactory(lv -> new TableCell<SyncPair,String>()
		{
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if(empty || item == null)
				{
					setText("");
					setContextMenu(null);
				}
				else
				{
					setText(item);
					setContextMenu(createContextMenu());
				}
			}
		});
	}
	private ContextMenu createContextMenu()
	{
		ContextMenu menu = new ContextMenu();
		MenuItem deleteItem = new MenuItem("Usun");
		deleteItem.setOnAction(event ->{
			DeleteSyncInfoService deleteSyncInfo = new DeleteSyncInfoService(syncTable.getSelectionModel().getSelectedItem().getSourceFile()
					, syncTable.getSelectionModel().getSelectedItem().getTargetFile());
			deleteSyncInfo.setOnSucceeded(successEvent -> {
				try {
					getSyncInfo();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			deleteSyncInfo.start();
		});
		
		menu.getItems().add(deleteItem);
		return menu;
		
	}
	
}
