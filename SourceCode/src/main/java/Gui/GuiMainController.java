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
import AmazonS3.AmazonS3Helper;
import GoogleDrive.GoogleDriveHelper;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import Local.LocalFileHelper;
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
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;
import javafx.stage.Modality;
import javafx.scene.control.CheckMenuItem;

public class GuiMainController {

	@FXML
	private ListView<ObjectMetadataIf> filesListViewL;
	@FXML
	private ListView<ObjectMetadataIf> filesListViewR;
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
	@FXML 
	private ProgressIndicator progressIndicator;

	private ApplicationConfig config;
	private AccountsSupervisor accountsSupervisor;
	private HelpersBundle supportersBundle;
	private Synchronizer synchronizer;
	
	public void initComponents() throws IOException, SQLException {
		initFields();
		initListView();
		initComboBoxes();
		initMenu();
		unlockGui();
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
		if(synchronizer.isSyncOn()) synchronizer.stopCyclicSynch();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/AccountsWindow.fxml"));
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
		if(syncSwitch.selectedProperty().getValue()) startSync();
		comboBoxesRefreshItems();
	}

	private void initListView() {

		filesListViewL.getSelectionModel().selectedItemProperty().addListener((event) -> {

			ObjectMetadataIf selectedFileMetaData = filesListViewL.getSelectionModel().getSelectedItem();
			if(selectedFileMetaData != null)
			{
				if(selectedFileMetaData.getSize().equals("0 KB"))
				{
					selectedFileSizeTextFieldL.setText("1 KB");
				}else
				{
					selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
				}
				lastModifiedTimeTextViewL.setText(selectedFileMetaData.getLastModifiedDate());
			}	

		});
		filesListViewR.getSelectionModel().selectedItemProperty().addListener((event) -> {

			ObjectMetadataIf selectedFileMetaData = filesListViewR.getSelectionModel().getSelectedItem();
			if(selectedFileMetaData != null)
			{
				if(selectedFileMetaData.getSize().equals("0 KB"))
				{
					selectedFileSizeTextFieldR.setText("1 KB");
				}else
				{
					selectedFileSizeTextFieldR.setText(selectedFileMetaData.getSize());
				}
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

		filesListViewR.setCellFactory(lv -> new ListCell<ObjectMetadataIf>() {

			@Override
			protected void updateItem(ObjectMetadataIf item, boolean empty) {
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
		
		filesListViewL.setCellFactory(lv -> new ListCell<ObjectMetadataIf>() {

			@Override
			protected void updateItem(ObjectMetadataIf item, boolean empty) {
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
			if(oldValue != null) filesServerComboR.getItems().add(oldValue);
			filesServerComboR.getItems().remove(newValue);
			initListView();
		});
		filesServerComboR.getSelectionModel().selectedItemProperty().addListener((event, oldValue, newValue) -> {
			comboBoxSelected(event, oldValue, newValue, filesListViewR);
			if(oldValue != null) filesServerComboL.getItems().add(oldValue);
			filesServerComboL.getItems().remove(newValue);
			initListView();
		});
	}
	
	private void comboBoxesRefreshItems()
	{	
		
		filesServerComboL.getItems().clear();
		filesServerComboR.getItems().clear();
		filesServerComboL.getItems().clear();
		filesServerComboR.getItems().clear();
		
		if(accountsSupervisor.isDriveLoggedIn())
		{
			filesServerComboL.getItems().add(FileServer.GoogleDrive);
			filesServerComboR.getItems().add(FileServer.GoogleDrive);
		}
		if(accountsSupervisor.isS3LoggedIn())
		{
			filesServerComboL.getItems().add(FileServer.AmazonS3);
			filesServerComboR.getItems().add(FileServer.AmazonS3);
		}
		
		filesServerComboL.getItems().add(FileServer.Komputer);
		filesServerComboR.getItems().add(FileServer.Komputer);
	}

	private void comboBoxSelected(ObservableValue event, Object aOldValue, Object aNewValue, ListView aListView) {
		FileServer oldValue = (FileServer) aOldValue;
		FileServer newValue = (FileServer) aNewValue;
		if (oldValue != newValue) {
			if (newValue == FileServer.GoogleDrive) {
				GoogleDriveDownloadService downloadService = new GoogleDriveDownloadService(
						supportersBundle.getGoogleDriveSupporter());
				downloadService.setOnSucceeded((Event) -> {
					aListView.setItems(downloadService.getValue());
				});

				downloadService.start();
			}
			if (newValue == FileServer.AmazonS3) {
				AmazonS3DownloadBucketsService downloadService = new AmazonS3DownloadBucketsService(
						supportersBundle.getAmazonS3Supporter());
				downloadService.setOnSucceeded(Event -> {
					aListView.setItems(downloadService.getValue());
				});
				downloadService.start();

			}
			if (newValue == FileServer.Komputer) {
				LocalFileExploreService exploreService = new LocalFileExploreService(
						supportersBundle.getLocalFileSupporter());
				exploreService.setOnSucceeded(Event -> {
					aListView.setItems(exploreService.getValue());
				});
				exploreService.start();
			}
		}
	}

	private void listViewClicked(MouseEvent aMouseEvent, ListView<ObjectMetadataIf> aClickedListView,
			ComboBox aConnectedComboBox) {
		boolean isDoubleClick = aMouseEvent.getClickCount() == 2;
		boolean isSomethinkSelected = aClickedListView.getSelectionModel().getSelectedItem() != null;
		if (isDoubleClick && isSomethinkSelected) {
			if (aConnectedComboBox.getSelectionModel().getSelectedItem().equals(FileServer.AmazonS3)) {
				ObjectMetadataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				AmazonObjectClickService s3ClickService = new AmazonObjectClickService(
						supportersBundle.getAmazonS3Supporter(), clickedObject);
				s3ClickService.setOnSucceeded(event -> {
					aClickedListView.setItems(s3ClickService.getValue());
				});
				s3ClickService.start();
			} else if (aConnectedComboBox.getSelectionModel().getSelectedItem().equals(FileServer.Komputer)) {
				ObjectMetadataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				LocalObjectClickService localClickService = new LocalObjectClickService(
						supportersBundle.getLocalFileSupporter(), clickedObject);
				localClickService.setOnSucceeded(event -> {
					aClickedListView.setItems(localClickService.getValue());
				});
				localClickService.start();
			} else if (aConnectedComboBox.getSelectionModel().getSelectedItem().equals(FileServer.GoogleDrive)) {
				ObjectMetadataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				GoogleObjectClickService localClickService = new GoogleObjectClickService(
						supportersBundle.getGoogleDriveSupporter(), clickedObject);
				localClickService.setOnSucceeded(event -> {
					aClickedListView.setItems(localClickService.getValue());
				});
				localClickService.start();
			}
		}

	}

	private ContextMenu buildContextMenu(ObjectMetadataIf aCellValue,ListView<ObjectMetadataIf> aSourceListView
			,FileServer targetServer, ListView<ObjectMetadataIf> aDestListView ) {
		final ContextMenu contextMenu = new ContextMenu();

		
		MenuItem refreshItem = new MenuItem("Odœwie¿");
		refreshItem.setOnAction(event ->
		{
			RefreshService refreshService = new RefreshService(supportersBundle, aCellValue );
			refreshService.setOnSucceeded(succesEvent ->{
				aSourceListView.setItems(refreshService.getValue());
				unlockGui();
			});
			refreshService.setOnFailed(failed -> unlockGui());
			blockGui();
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
					unlockGui();
				});
				blockGui();
				deleteService.setOnFailed(failed -> unlockGui());
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
							unlockGui();
					});
					blockGui();
					newFolderService.setOnFailed(failed -> unlockGui());
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
							unlockGui();
						});
						changeNameService.setOnFailed(failed -> blockGui());
						blockGui();
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
					
					ObjectMetadataIf objectToCopy = aSourceListView.getSelectionModel().getSelectedItem();
					Optional<ObjectMetadataIf> existingObj = itemIsOnList(aDestListView.getItems(),objectToCopy, targetServer);
					
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
													unlockGui();
												});
												refreshService.setOnFailed(failed -> unlockGui());
												refreshService.start();
											} catch (SQLException e) {
												e.printStackTrace();
											}
										});
										copyService.setOnFailed(failed -> unlockGui());
										copyService.start();
									});
									deleteService.setOnFailed(failed -> unlockGui());
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
							blockGui();
							checkFileExist.setOnFailed(failed -> unlockGui());
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
									unlockGui();
								});
								refreshService.setOnFailed(failed -> unlockGui());
								refreshService.start();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						});
						blockGui();
						copyService.setOnFailed(failed -> unlockGui());
						copyService.start();
					}
				});
				contextMenu.getItems().add(syncSource);
			}
		}
		return contextMenu;
	}
	
	private void showMessageWindow(String aMessage,String aTitle) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MessageWindow.fxml"));
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/inputWindow.fxml"));
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/syncWindow.fxml"));
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/yesNoWindow.fxml"));
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
	
	private Optional<ObjectMetadataIf> itemIsOnList(ObservableList<ObjectMetadataIf> aList,ObjectMetadataIf aObj,FileServer aTargetServer)
	{
		String ObjName;
		if(aObj.getFileServer() == FileServer.AmazonS3) ObjName = getName(aObj,FileServer.AmazonS3);
		else ObjName = aObj.getName();
		
		Optional<ObjectMetadataIf> existingObject = aList.stream()
		
		.filter(item ->{
			if(getName(item,aTargetServer).equals(ObjName)) return true;
			else return false;
		})
		.findAny();
		return existingObject;
	}
	
	private String getName(ObjectMetadataIf aObj,FileServer aServer)
	{
		if(aServer == FileServer.AmazonS3)
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
	
	private void blockGui()
	{
		
		filesListViewL.setDisable(true);
		filesListViewR.setDisable(true);
		filesServerComboL.setDisable(true);
		filesServerComboR.setDisable(true);
		syncSwitch.setDisable(true);
		syncMenu.setDisable(true);
		accountsMenuItem.setDisable(true);
		selectedFileSizeTextFieldL.setDisable(true);
		selectedFileSizeTextFieldR.setDisable(true);
		lastModifiedTimeTextViewL.setDisable(true);
		lastModifiedTimeTextViewR.setDisable(true);
		progressIndicator.setVisible(true);
	}
	private void unlockGui()
	{
		
		filesListViewL.setDisable(false);
		filesListViewR.setDisable(false);
		filesServerComboL.setDisable(false);
		filesServerComboR.setDisable(false);
		syncSwitch.setDisable(false);
		syncMenu.setDisable(false);
		accountsMenuItem.setDisable(false);
		selectedFileSizeTextFieldL.setDisable(false);
		selectedFileSizeTextFieldR.setDisable(false);
		lastModifiedTimeTextViewL.setDisable(false);
		lastModifiedTimeTextViewR.setDisable(false);
		progressIndicator.setVisible(false);
	}
	
	private void mouseDroppedOnListView(ListView<ObjectMetadataIf> aTargetList,ListView<ObjectMetadataIf> aSourceList)
	{
		FileServer targetServer = connectedFileServer(aTargetList);
		ObjectMetadataIf objectToCopy = aSourceList.getSelectionModel().getSelectedItem();
		Optional<ObjectMetadataIf> existingObj = itemIsOnList(aTargetList.getItems(),objectToCopy,targetServer);
		
		if(existingObj.isPresent())
		{
			boolean userDecision = showYesNoWindow("Plik ju¿ istnieje, czy chcesz go zamieniæ ?");
			if(userDecision)
			{
				DeleteService deleteService = new DeleteService(supportersBundle, existingObj.get());
				deleteService.setOnSucceeded(deletedEvent -> {
					final CopyService copyService = new CopyService(supportersBundle,objectToCopy, targetServer);
					copyService.setOnSucceeded(event -> {
						ObjectMetadataIf copiedFile = copyService.getValue();
						RefreshService refreshService = new RefreshService(supportersBundle,copiedFile);
						refreshService.setOnSucceeded(refreshEvent ->{
							aTargetList.setItems(refreshService.getValue());
							unlockGui();
						});
						if(copiedFile.getFileServer() == FileServer.GoogleDrive)
						{
							UpdateSyncKeyService updateService = new UpdateSyncKeyService(synchronizer,existingObj.get().getOrginalId()
									,copiedFile.getOrginalId());
							updateService.start();
						}
						refreshService.setOnFailed(failed -> unlockGui());
						refreshService.start();
					});
					copyService.setOnFailed(failed -> unlockGui() );
					copyService.start();
				});
				deleteService.setOnFailed(failed -> unlockGui());
				blockGui();
				deleteService.start();
			}
		}
		else
		{
			final CopyService copyService = new CopyService(supportersBundle,objectToCopy, targetServer);
			copyService.setOnSucceeded(event -> {
				ObjectMetadataIf copiedFile = copyService.getValue();
				RefreshService refreshService = new RefreshService(supportersBundle,copiedFile);
				refreshService.setOnSucceeded(refreshEvent ->{
					aTargetList.setItems(refreshService.getValue());
					unlockGui();
				});
				refreshService.setOnFailed(failed -> unlockGui());
				refreshService.start();
			});
			copyService.setOnFailed(failed -> unlockGui());
			blockGui();
			copyService.start();
		}
	}
}
