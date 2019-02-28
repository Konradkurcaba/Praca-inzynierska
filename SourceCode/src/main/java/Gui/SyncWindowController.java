package Gui;

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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class SyncWindowController {
	
	Synchronizer synchronizer;
	
	public void init(Synchronizer aSynchronizer) throws SQLException
	{
		synchronizer = aSynchronizer;
		initTable();
		getSyncInfo();
	}
	
	@FXML
	private TableView<SyncPair> syncTable;
	
	@FXML
	private ProgressIndicator progres;

	private void getSyncInfo() throws SQLException
	{
		progres.setVisible(true);
		GetSyncInfo syncInfoService = new GetSyncInfo(synchronizer);
		syncInfoService.setOnSucceeded(event ->{
			ObservableList<SyncPair> items = FXCollections.observableArrayList((syncInfoService.getValue().entrySet().stream()
					.map(entry ->{
						return new SyncPair(entry.getKey(),entry.getValue());
					})
					.collect(Collectors.toList())));
			progres.setVisible(false);
			 syncTable.setItems(items);
		});
		syncInfoService.setOnFailed(event -> progres.setVisible(false));
		syncInfoService.start();
	}
	
	private void initTable()
	{
		 syncTable.setPlaceholder(new Label(""));
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
