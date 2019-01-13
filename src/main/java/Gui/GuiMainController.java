package Gui;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountException;

import com.amazonaws.services.s3.model.analytics.StorageClassAnalysis;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Change;

import AmazonS3.AmazonAccountInfo;
import AmazonS3.AmazonS3Converter;
import AmazonS3.AmazonS3Supporter;
import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import Local.LocalFileSupporter;
import Synchronization.Synchronizer;
import Threads.AmazonObjectClickService;
import Threads.AmazonS3DownloadBucketsService;
import Threads.ChangeNameService;
import Threads.CheckIfFilesAreSyncTarget;
import Threads.CopyService;
import Threads.DeleteService;
import Threads.GoogleDriveDownloadService;
import Threads.GoogleObjectClickService;
import Threads.LocalFileExploreService;
import Threads.LocalObjectClickService;
import Threads.NewFolderService;
import Threads.RefreshService;
import Threads.UpdateSyncKeyService;
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
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.HelpersBundle;
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
	private ComboBox<FileServer> filesServerComboL;
	@FXML
	private ComboBox<FileServer> filesServerComboR;
	@FXML 
	private CheckMenuItem syncSwitch;
	@FXML
	private MenuItem syncMenu;
	@FXML
	private MenuItem accountsMenuItem;

	ApplicationConfig config;
	AccountsSupervisor accountsSupervisor;
	HelpersBundle supportersBundle;
	Synchronizer synchronizer;
	
	public void initComponents() throws IOException, SQLException {
		initFields();
		initListView();
		initComboBoxes();
		initMenu();
	}
	
	private void initFields() throws SQLException {
		config = new ApplicationConfig();
		supportersBundle = new HelpersBundle();
		accountsSupervisor = new AccountsSupervisor(supportersBundle);
		String googleAccountAlias = config.getDefaultDriveAccount();
		if(googleAccountAlias != null)
		{
			accountsSupervisor.changeDriveAccount(config.getDefaultDriveAccount());
		}
		AmazonAccountInfo amazonDefaultAccount = config.getCurrentS3Account();
		if(amazonDefaultAccount != null)
		{
			accountsSupervisor.changeAmazonAccount(amazonDefaultAccount);
		}
		synchronizer = new Synchronizer(supportersBundle,accountsSupervisor);
		
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
		accountsMenuItem.setOnAction(event -> {
			try
			{
				showAccountsWindow();
				clearGui();
				
			}catch(IOException ex)
			{
				ex.printStackTrace();
			}
		});
	}
	
	private void clearGui() {
		filesListViewL.getItems().clear();
		filesListViewR.getItems().clear();
		selectedFileSizeTextFieldL.clear();
		selectedFileSizeTextFieldL.clear();
		lastModifiedTimeTextViewL.clear();
		lastModifiedTimeTextViewR.clear();		
	}

	private void showAccountsWindow() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/AccountsWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();
		Stage accountsWindow = new Stage();
		accountsWindow.initModality(Modality.WINDOW_MODAL);
		accountsWindow.initOwner(filesListViewL.getScene().getWindow());
		accountsWindow.setTitle("Twoje konta");
		accountsWindow.setScene(new Scene(root));
		AccountsWindowController accountsWindowController = loader.getController();
		accountsWindowController.init(config,accountsSupervisor);
		accountsWindow.showAndWait();
		comboBoxesRefreshItems();
	}

	private void initListView() {

		filesListViewL.getSelectionModel().selectedItemProperty().addListener((event) -> {

			ObjectMetaDataIf selectedFileMetaData = filesListViewL.getSelectionModel().getSelectedItem();
			if(selectedFileMetaData != null)
			{
				selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
				lastModifiedTimeTextViewL.setText(selectedFileMetaData.getLastModifiedDate());
			}	

		});
		filesListViewR.getSelectionModel().selectedItemProperty().addListener((event) -> {

			ObjectMetaDataIf selectedFileMetaData = filesListViewR.getSelectionModel().getSelectedItem();
			if(selectedFileMetaData != null)
			{
				selectedFileSizeTextFieldR.setText(selectedFileMetaData.getSize());
				lastModifiedTimeTextViewR.setText(selectedFileMetaData.getLastModifiedDate());
			}
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
			mouseDroppedOnListView(filesListViewR, filesListViewL);
		});
		
		filesListViewL.setOnDragDropped(mouseEvent -> {
			mouseDroppedOnListView(filesListViewL, filesListViewR);
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
	
		comboBoxesRefreshItems();

		filesServerComboL.getSelectionModel().selectedItemProperty().addListener((event, oldValue, newValue) -> {
			comboBoxSelected(event, oldValue, newValue, filesListViewL);
			initListView();
		});
		filesServerComboR.getSelectionModel().selectedItemProperty().addListener((event, oldValue, newValue) -> {
			comboBoxSelected(event, oldValue, newValue, filesListViewR);
			initListView();
		});
	}
	
	private void comboBoxesRefreshItems()
	{	
		
		filesServerComboL.getItems().clear();
		filesServerComboR.getItems().clear();
		
		if(accountsSupervisor.isDriveLoggedIn())
		{
			filesServerComboL.getItems().add(FileServer.Google);
			filesServerComboR.getItems().add(FileServer.Google);
		}
		if(accountsSupervisor.isS3LoggedIn())
		{
			filesServerComboL.getItems().add(FileServer.Amazon);
			filesServerComboR.getItems().add(FileServer.Amazon);
		}
		
		filesServerComboL.getItems().add(FileServer.Local);
		filesServerComboR.getItems().add(FileServer.Local);
		
		
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
			,FileServer targetServer, ListView<ObjectMetaDataIf> aDestListView ) {
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
					
					ObjectMetaDataIf objectToCopy = aSourceListView.getSelectionModel().getSelectedItem();
					
					Optional<ObjectMetaDataIf> existingObj = itemIsOnList(aDestListView.getItems(),objectToCopy, targetServer);
					
					if(existingObj.isPresent())
					{
						
						boolean userDecision = showYesNoWindow("Plik ju¿ istnieje, czy chcesz go zamieniæ ?");
						if(userDecision)
						{
							
							CheckIfFilesAreSyncTarget checkFileExist = new CheckIfFilesAreSyncTarget(existingObj.get(),
									objectToCopy,supportersBundle);
							checkFileExist.setOnSucceeded(success ->{
								if(checkFileExist.getValue() != true)
								{
									DeleteService deleteService = new DeleteService(supportersBundle, existingObj.get());
									deleteService.setOnSucceeded(deletedEvent -> {
										CopyService copyService = new CopyService(supportersBundle, aCellValue, targetServer);
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
									deleteService.start();
								}
								else
								{
									try {
										showMessageWindow("Po³¹czenie nie mo¿e zostaæ utworzone, plik jest juz celem synchronizacji","B³¹d");
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
							checkFileExist.start();
						}
					}
					else
					{
						CopyService copyService = new CopyService(supportersBundle, aCellValue, targetServer);
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
					}
				});
				contextMenu.getItems().add(syncSource);
			}
		}
		return contextMenu;
	}
	
	private void showMessageWindow(String aMessage,String aTitle) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessageWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();
		Stage okWindow = new Stage();
		okWindow.initModality(Modality.WINDOW_MODAL);
		okWindow.initOwner(filesListViewL.getScene().getWindow());
		okWindow.setTitle(aTitle);
		okWindow.setScene(new Scene(root));
		MessageWindowController inputWindowController = loader.getController();
		inputWindowController.init(aMessage);
		okWindow.showAndWait();
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

	private boolean showYesNoWindow(String displayText) 
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/yesNoWindow.fxml"));
			loader.load();
			Parent root = loader.getRoot();
			Stage yesNoWindow = new Stage();
			yesNoWindow.initModality(Modality.WINDOW_MODAL);
			yesNoWindow.initOwner(filesListViewL.getScene().getWindow());
			yesNoWindow.setTitle("Potwierdzenie");
			yesNoWindow.setScene(new Scene(root));
			YesNoWindowController windowController = loader.getController();
			windowController.init(displayText);
			yesNoWindow.showAndWait();
			return windowController.isOkClicked();
			
		}catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private Optional<ObjectMetaDataIf> itemIsOnList(ObservableList<ObjectMetaDataIf> aList,ObjectMetaDataIf aObj,FileServer aTargetServer)
	{
		String ObjName;
		if(aObj.getFileServer() == FileServer.Amazon) ObjName = getName(aObj,FileServer.Amazon);
		else ObjName = aObj.getName();
		
		Optional<ObjectMetaDataIf> existingObject = aList.stream()
		
		.filter(item ->{
			if(getName(item,aTargetServer).equals(ObjName)) return true;
			else return false;
		})
		.findAny();
		return existingObject;
	}
	
	private String getName(ObjectMetaDataIf aObj,FileServer aServer)
	{
		if(aServer == FileServer.Amazon)
		{
			AmazonS3Converter conveter = new AmazonS3Converter();
			return conveter.deletePrefix(aObj.getName());
		}else return aObj.getName();
	}
	
	private FileServer connectedFileServer(ListView aList)
	{
		if(aList == filesListViewL) return filesServerComboL.getSelectionModel().getSelectedItem();
		else return filesServerComboR.getSelectionModel().getSelectedItem();
	}
	
	private void mouseDroppedOnListView(ListView<ObjectMetaDataIf> aTargetList,ListView<ObjectMetaDataIf> aSourceList)
	{
		FileServer targetServer = connectedFileServer(aTargetList);
		ObjectMetaDataIf objectToCopy = aSourceList.getSelectionModel().getSelectedItem();
		Optional<ObjectMetaDataIf> existingObj = itemIsOnList(aTargetList.getItems(),objectToCopy,targetServer);
		
		if(existingObj.isPresent())
		{
			boolean userDecision = showYesNoWindow("Plik ju¿ istnieje, czy chcesz go zamieniæ ?");
			if(userDecision)
			{
				DeleteService deleteService = new DeleteService(supportersBundle, existingObj.get());
				deleteService.setOnSucceeded(deletedEvent -> {
					final CopyService copyService = new CopyService(supportersBundle,objectToCopy, targetServer);
					copyService.setOnSucceeded(event -> {
						ObjectMetaDataIf copiedFile = copyService.getValue();
						RefreshService refreshService = new RefreshService(supportersBundle,copiedFile);
						refreshService.setOnSucceeded(refreshEvent ->{
							aTargetList.setItems(refreshService.getValue());
						});
						if(copiedFile.getFileServer() == FileServer.Google)
						{
							UpdateSyncKeyService updateService = new UpdateSyncKeyService(synchronizer,existingObj.get().getOrginalId()
									,copiedFile.getOrginalId());
							updateService.start();
						}
						refreshService.start();
					});
					copyService.start();
				});
				deleteService.start();
			}
		}
		else
		{
			final CopyService copyService = new CopyService(supportersBundle,objectToCopy, targetServer);
			copyService.setOnSucceeded(event -> {
				ObjectMetaDataIf copiedFile = copyService.getValue();
				RefreshService refreshService = new RefreshService(supportersBundle,copiedFile);
				refreshService.setOnSucceeded(refreshEvent ->{
					aTargetList.setItems(refreshService.getValue());
				});
				refreshService.start();
			});
			copyService.start();
		}
	}
}
