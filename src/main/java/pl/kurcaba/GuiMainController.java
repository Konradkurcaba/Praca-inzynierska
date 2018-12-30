package pl.kurcaba;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.amazonaws.services.s3.model.analytics.StorageClassAnalysis;
import com.google.api.services.drive.model.Change;

import AmazonS3.AmazonS3Supporter;
import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import Local.LocalFileSupporter;
import Synchronization.Synchronizer;
import Threads.AmazonObjectClickService;
import Threads.AmazonS3DownloadBucketsService;
import Threads.ChangeNameService;
import Threads.CopyService;
import Threads.DeleteService;
import Threads.GoogleDriveDownloadService;
import Threads.GoogleObjectClickService;
import Threads.LocalFileExploreService;
import Threads.LocalObjectClickService;
import Threads.NewFolderService;
import Threads.RefreshService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.CheckMenuItem;

public class GuiMainController {

	@FXML
	private ListView<ObjectMetaDataIf> filesListViewL;
	@FXML
	private ListView<ObjectMetaDataIf> filesListViewR;
	@FXML
	private TextField selectedFileSizeTextFieldL;
	@FXML
	private TextField lastModifiedTimeTextViewL;
	@FXML
	private TextField selectedFileSizeTextFieldR;
	@FXML
	private TextField lastModifiedTimeTextViewR;
	@FXML
	private ComboBox filesServerComboL;
	@FXML
	private ComboBox filesServerComboR;
	@FXML 
	private CheckMenuItem syncSwitch;
	@FXML
	private MenuItem syncMenu;

	SupportersBundle supportersBundle = new SupportersBundle();
	Synchronizer synchronizer = new Synchronizer(supportersBundle);

	public void initComponents() throws IOException {
		initListView();
		initComboBoxes();
		initMenu();
	}
	
	public void stopSync()
	{
		synchronizer.stopCyclicSynch();
	}
	
	public void startSync()
	{
		synchronizer.startCyclicSynch();
	}

	private void initMenu()
	{
		syncSwitch.setOnAction(event -> {
			if(syncSwitch.selectedProperty().getValue()) startSync();
			else stopSync();
		});
		syncMenu.setOnAction(event ->{
			try {
				showSyncWindow();
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
		});
	}
	
	
	
	private void initListView() {

		filesListViewL.getSelectionModel().selectedItemProperty().addListener((event) -> {

			ObjectMetaDataIf selectedFileMetaData = filesListViewL.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
			lastModifiedTimeTextViewL.setText(selectedFileMetaData.getLastModifiedDate());

		});
		filesListViewR.getSelectionModel().selectedItemProperty().addListener((event) -> {

			ObjectMetaDataIf selectedFileMetaData = filesListViewR.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldR.setText(selectedFileMetaData.getSize());
			lastModifiedTimeTextViewR.setText(selectedFileMetaData.getLastModifiedDate());
		});

		filesListViewL.setOnMouseClicked(mouseEvent -> {
			listViewClicked(mouseEvent, filesListViewL, filesServerComboL);
		});
		filesListViewR.setOnMouseClicked(mouseEvent -> {
			listViewClicked(mouseEvent, filesListViewR, filesServerComboR);
		});

		filesListViewL.setOnDragDetected(mouseEvent -> {

			Dragboard db = filesListViewL.startDragAndDrop(TransferMode.ANY);

			ClipboardContent content = new ClipboardContent();
			content.putString("copy");
			db.setContent(content);

			mouseEvent.consume();

		});
		
		filesListViewR.setOnDragDetected(mouseEvent -> {

			Dragboard db = filesListViewR.startDragAndDrop(TransferMode.ANY);

			ClipboardContent content = new ClipboardContent();
			content.putString("copy");
			db.setContent(content);

			mouseEvent.consume();

		});

		filesListViewR.setOnDragOver(mouseEvent -> {

			if (mouseEvent.getGestureSource() != filesListViewR && mouseEvent.getDragboard().hasString()) {
				mouseEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			mouseEvent.consume();

		});
		
		filesListViewL.setOnDragOver(mouseEvent -> {

			if (mouseEvent.getGestureSource() != filesListViewL && mouseEvent.getDragboard().hasString()) {
				mouseEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			mouseEvent.consume();

		});
		
		filesListViewR.setOnDragDropped(mouseEvent -> {
			FileServer targetServer = (FileServer) filesServerComboR.getSelectionModel().getSelectedItem();

			final CopyService copyService = new CopyService(supportersBundle,
					filesListViewL.getSelectionModel().getSelectedItem(), targetServer);

			copyService.setOnSucceeded(event -> {
				
				ObjectMetaDataIf copiedFile = copyService.getValue();
				RefreshService refreshService = new RefreshService(supportersBundle,copiedFile);
				refreshService.setOnSucceeded(refreshEvent ->{
					filesListViewR.setItems(refreshService.getValue());
				});
				refreshService.start();
			});
			copyService.start();

		});
		
		filesListViewL.setOnDragDropped(mouseEvent -> {
			FileServer targetServer = (FileServer) filesServerComboL.getSelectionModel().getSelectedItem();

			final CopyService copyService = new CopyService(supportersBundle,
					filesListViewR.getSelectionModel().getSelectedItem(), targetServer);

			copyService.setOnSucceeded(event -> {
				
				ObjectMetaDataIf copiedFile = copyService.getValue();
				RefreshService refreshService = new RefreshService(supportersBundle,copiedFile);
				refreshService.setOnSucceeded(refreshEvent ->{
					filesListViewL.setItems(refreshService.getValue());
				});
				refreshService.start();
			});
			copyService.start();
		});

		filesListViewR.setCellFactory(lv -> new ListCell<ObjectMetaDataIf>() {

			@Override
			protected void updateItem(ObjectMetaDataIf item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText("");
					setContextMenu(null);
				} else {
					setText(item.toString());
					setContextMenu(buildContextMenu(item,filesListViewR
							,(FileServer)filesServerComboL.getSelectionModel().getSelectedItem(),filesListViewL));
				}
			}
		});
		
		filesListViewL.setCellFactory(lv -> new ListCell<ObjectMetaDataIf>() {

			@Override
			protected void updateItem(ObjectMetaDataIf item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText("");
					setContextMenu(null);
				} else {
					setText(item.toString());
					setContextMenu(buildContextMenu(item,filesListViewL
							,(FileServer)filesServerComboR.getSelectionModel().getSelectedItem(),filesListViewR));
				}
			}
		});

	}

	private void initComboBoxes() {
		filesServerComboL.getItems().addAll(FileServer.Google, FileServer.Amazon, FileServer.Local);
		filesServerComboR.getItems().addAll(FileServer.Google, FileServer.Amazon, FileServer.Local);

		filesServerComboL.getSelectionModel().selectedItemProperty().addListener((event, oldValue, newValue) -> {
			comboBoxSelected(event, oldValue, newValue, filesListViewL);
		});
		filesServerComboR.getSelectionModel().selectedItemProperty().addListener((event, oldValue, newValue) -> {
			comboBoxSelected(event, oldValue, newValue, filesListViewR);
		});
	}

	private void comboBoxSelected(ObservableValue event, Object aOldValue, Object aNewValue, ListView aListView) {
		FileServer oldValue = (FileServer) aOldValue;
		FileServer newValue = (FileServer) aNewValue;
		if (oldValue != newValue) {
			if (newValue == FileServer.Google) {
				GoogleDriveDownloadService downloadService = new GoogleDriveDownloadService(
						supportersBundle.getGoogleDriveSupporter());
				downloadService.setOnSucceeded((Event) -> {
					aListView.setItems(downloadService.getValue());
				});

				downloadService.start();
			}
			if (newValue == FileServer.Amazon) {
				AmazonS3DownloadBucketsService downloadService = new AmazonS3DownloadBucketsService(
						supportersBundle.getAmazonS3Supporter());
				downloadService.setOnSucceeded(Event -> {
					aListView.setItems(downloadService.getValue());
				});
				downloadService.start();

			}
			if (newValue == FileServer.Local) {
				LocalFileExploreService exploreService = new LocalFileExploreService(
						supportersBundle.getLocalFileSupporter());
				exploreService.setOnSucceeded(Event -> {
					aListView.setItems(exploreService.getValue());
				});
				exploreService.start();
			}
		}
	}

	private void listViewClicked(MouseEvent aMouseEvent, ListView<ObjectMetaDataIf> aClickedListView,
			ComboBox aConnectedComboBox) {
		boolean isDoubleClick = aMouseEvent.getClickCount() == 2;
		boolean isSomethinkSelected = aClickedListView.getSelectionModel().getSelectedItem() != null;
		if (isDoubleClick && isSomethinkSelected) {
			if (aConnectedComboBox.getSelectionModel().getSelectedItem().equals(FileServer.Amazon)) {
				ObjectMetaDataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				AmazonObjectClickService s3ClickService = new AmazonObjectClickService(
						supportersBundle.getAmazonS3Supporter(), clickedObject);
				s3ClickService.setOnSucceeded(event -> {
					aClickedListView.setItems(s3ClickService.getValue());
				});
				s3ClickService.start();
			} else if (aConnectedComboBox.getSelectionModel().getSelectedItem().equals(FileServer.Local)) {
				ObjectMetaDataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				LocalObjectClickService localClickService = new LocalObjectClickService(
						supportersBundle.getLocalFileSupporter(), clickedObject);
				localClickService.setOnSucceeded(event -> {
					aClickedListView.setItems(localClickService.getValue());
				});
				localClickService.start();
			} else if (aConnectedComboBox.getSelectionModel().getSelectedItem().equals(FileServer.Google)) {
				ObjectMetaDataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				GoogleObjectClickService localClickService = new GoogleObjectClickService(
						supportersBundle.getGoogleDriveSupporter(), clickedObject);
				localClickService.setOnSucceeded(event -> {
					aClickedListView.setItems(localClickService.getValue());
				});
				localClickService.start();
			}
		}

	}

	private ContextMenu buildContextMenu(ObjectMetaDataIf aCellValue,ListView<ObjectMetaDataIf> aSourceListView
			,FileServer secondServer, ListView<ObjectMetaDataIf> aDestListView ) {
		final ContextMenu contextMenu = new ContextMenu();

		
		MenuItem refreshItem = new MenuItem("Odœwie¿");
		refreshItem.setOnAction(event ->
		{
			RefreshService refreshService = new RefreshService(supportersBundle, aCellValue );
			refreshService.setOnSucceeded(succesEvent ->{
				aSourceListView.setItems(refreshService.getValue());
			});
			refreshService.start();
		});
		
		
		contextMenu.getItems().add(refreshItem);

		if(!aCellValue.isRoot())
		{
			MenuItem deleteItem = new MenuItem("Usuñ");
			deleteItem.setOnAction(event -> {
				DeleteService deleteService = new DeleteService(supportersBundle, aCellValue );
				deleteService.setOnSucceeded(succesEvent ->{
					aSourceListView.setItems(deleteService.getValue());
				});
				deleteService.start();
			});
			
			contextMenu.getItems().add(deleteItem);
			
			MenuItem createNewFolderItem = new MenuItem("Stwórz nowy folder");
			createNewFolderItem.setOnAction(event ->{
				try 
				{
					String newFolderName = showInputWindow("Nowy folder","WprowadŸ nazwê nowego folderu");
					if (newFolderName != null && newFolderName.length() > 0)
					{
						NewFolderService newFolderService = new NewFolderService(supportersBundle, aCellValue.getFileServer()
								, newFolderName);
						newFolderService.setOnSucceeded(success ->{
							aSourceListView.setItems(newFolderService.getValue());
					});
					newFolderService.start();
				}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			contextMenu.getItems().add(createNewFolderItem);
			
			MenuItem changeNameItem = new MenuItem("Zmieñ nazwê");
			changeNameItem.setOnAction(event -> {
				try {
					String newName = showInputWindow("Zmiana nazwy", "WprowadŸ now¹ nazwê");
					if (newName != null && newName.length() > 0)
					{
						ChangeNameService changeNameService = new ChangeNameService(supportersBundle, aCellValue, newName);
						changeNameService.setOnSucceeded(success -> {
							aSourceListView.setItems(changeNameService.getValue());
						});
						changeNameService.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			contextMenu.getItems().add(changeNameItem);
			
			if(!aCellValue.isDirectory())
			{
				MenuItem syncSource = new MenuItem("Utwórz synchronizowan¹ kopiê pliku");
				syncSource.setOnAction(action -> {
					CopyService copyService = new CopyService(supportersBundle, aCellValue, secondServer);
					copyService.setOnSucceeded(event ->{
						try {
							synchronizer.addFilesToSynchronize(aCellValue,copyService.getValue());
							RefreshService refreshService = new RefreshService(supportersBundle, copyService.getValue());
							refreshService.setOnSucceeded(successEvent ->{
								aDestListView.setItems(refreshService.getValue());
							});
							refreshService.start();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
					copyService.start();
					
				});
				contextMenu.getItems().add(syncSource);
			}
		}
		return contextMenu;
	}
	
	private String showInputWindow(String aWindowTitle, String aMessage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/InputWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();
		Stage inputWindow = new Stage();
		inputWindow.initModality(Modality.WINDOW_MODAL);
		inputWindow.initOwner(filesListViewL.getScene().getWindow());
		inputWindow.setTitle(aWindowTitle);
		inputWindow.setScene(new Scene(root));
		InputWindowController inputWindowController = loader.getController();
		inputWindowController.init(aMessage);
		inputWindow.showAndWait();
		return(inputWindowController.getTextFieldValue());
	}
	
	private void showSyncWindow() throws IOException, SQLException
	{
		if(synchronizer.isSyncOn()) synchronizer.stopCyclicSynch();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/syncWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();
		Stage syncWindow = new Stage();
		syncWindow.initModality(Modality.WINDOW_MODAL);
		syncWindow.initOwner(filesListViewL.getScene().getWindow());
		syncWindow.setTitle("Panel synchronizacji");
		syncWindow.setScene(new Scene(root));
		SyncWindowController syncWindowController = loader.getController();
		syncWindowController.init(synchronizer);
		syncWindow.showAndWait();
		if(syncSwitch.selectedProperty().getValue()) startSync();
	}

}
