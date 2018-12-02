package pl.kurcaba;

import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import AmazonS3.AmazonS3Supporter;
import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import Local.LocalFileSupporter;
import Threads.AmazonObjectClickService;
import Threads.AmazonS3DownloadBucketsService;
import Threads.CopyService;
import Threads.DeleteService;
import Threads.GoogleDriveDownloadService;
import Threads.GoogleObjectClickService;
import Threads.LocalFileExploreService;
import Threads.LocalObjectClickService;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
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

public class GuiFilesListViewController {

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

	SupportersBundle supportersBundle = new SupportersBundle();

	public void initComponents() {
		initListView();
		initComboBoxes();
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

		filesListViewR.setOnDragOver(mouseEvent -> {

			if (mouseEvent.getGestureSource() != filesListViewR && mouseEvent.getDragboard().hasString()) {
				mouseEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			mouseEvent.consume();

		});

		filesListViewR.setOnDragDropped(mouseEvent -> {
			FileServer targetServer = (FileServer) filesServerComboR.getSelectionModel().getSelectedItem();

			final CopyService copyService = new CopyService(supportersBundle,
					filesListViewL.getSelectionModel().getSelectedItem(), targetServer);

			copyService.setOnSucceeded(event -> {
				filesListViewR.setItems(copyService.getValue());
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
					setContextMenu(buildContextMenu(item,getListView()));
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

	private ContextMenu buildContextMenu(ObjectMetaDataIf aCellValue,ListView<ObjectMetaDataIf> aSourceListView) {
		final ContextMenu contextMenu = new ContextMenu();

		MenuItem menuItem = new MenuItem("Usuñ");
		menuItem.setOnAction(event -> {
			DeleteService deleteService = new DeleteService(supportersBundle, aCellValue );
			deleteService.setOnSucceeded(succesEvent ->{
				aSourceListView.setItems(deleteService.getValue());
			});
			deleteService.start();
			
		});
		contextMenu.getItems().add(menuItem);

		return contextMenu;
	}

}
