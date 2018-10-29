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
import Threads.GoogleDriveDownloadService;
import Threads.LocalFileExploreService;
import Threads.LocalObjectClickService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;

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
	
	
	GoogleDriveSupporter driveSupporter = new GoogleDriveSupporter();
	AmazonS3Supporter s3Supporter = new AmazonS3Supporter();
	LocalFileSupporter localSupporter = new LocalFileSupporter();
	
	
	public void initComponents()
	{
		initListView();
		initComboBoxes();
	}
	
	private void initListView()
	{
		
		filesListViewL.getSelectionModel().selectedItemProperty().addListener((event) -> {
			
			ObjectMetaDataIf selectedFileMetaData = filesListViewL.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
			lastModifiedTimeTextViewL.setText(selectedFileMetaData.getLastModifiedDate());
			
		});
		filesListViewR.getSelectionModel().selectedItemProperty().addListener((event) ->{
			
			ObjectMetaDataIf selectedFileMetaData = filesListViewR.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldR.setText(selectedFileMetaData.getSize());
			lastModifiedTimeTextViewR.setText(selectedFileMetaData.getLastModifiedDate());
		});
		
		filesListViewL.setOnMouseClicked(mouseEvent -> {
			listViewClicked(mouseEvent,filesListViewL,filesServerComboL);
		});
		filesListViewR.setOnMouseClicked(mouseEvent ->{
			listViewClicked(mouseEvent, filesListViewR,filesServerComboR);
		});
	}
	
	private void initComboBoxes()
	{
		filesServerComboL.getItems().addAll(FileServer.Google,FileServer.Amazon,FileServer.Local);
		filesServerComboR.getItems().addAll(FileServer.Google,FileServer.Amazon,FileServer.Local);
		
		filesServerComboL.getSelectionModel().selectedItemProperty().addListener( (event, oldValue, newValue ) -> {
			comboBoxSelected(event,oldValue,newValue,filesListViewL);
		});
		filesServerComboR.getSelectionModel().selectedItemProperty().addListener( (event, oldValue, newValue ) -> {
			comboBoxSelected(event,oldValue,newValue,filesListViewR);
		});
	}
	private void comboBoxSelected(ObservableValue event,Object aOldValue,Object aNewValue,ListView aListView)
	{
		FileServer oldValue = (FileServer) aOldValue;
		FileServer newValue =(FileServer) aNewValue;
		if(oldValue != newValue)
		{
			if(newValue == FileServer.Google)
			{
				GoogleDriveDownloadService downloadService = new GoogleDriveDownloadService(driveSupporter);
				downloadService.setOnSucceeded((Event) -> {
						aListView.setItems(downloadService.getValue());
					});
				
				downloadService.start();
			}
			if(newValue == FileServer.Amazon)
			{
				AmazonS3DownloadBucketsService downloadService = new AmazonS3DownloadBucketsService(s3Supporter);
				downloadService.setOnSucceeded( Event -> {
					aListView.setItems(downloadService.getValue());
				});
				downloadService.start();
				
			}
			if(newValue == FileServer.Local)
			{
				LocalFileExploreService exploreService = new LocalFileExploreService(localSupporter);
				exploreService.setOnSucceeded(Event -> {
					aListView.setItems(exploreService.getValue());
				});
				exploreService.start();
			}
		}
	}
	
	private void listViewClicked(MouseEvent aMouseEvent,ListView<ObjectMetaDataIf> aClickedListView,ComboBox aConnectedComboBox)
	{
		boolean isDoubleClick = aMouseEvent.getClickCount() == 2;
		boolean isSomethinkSelected = aClickedListView.getSelectionModel().getSelectedItem() != null; 
		if(isDoubleClick && isSomethinkSelected )
		{
			if(aConnectedComboBox.getSelectionModel().getSelectedItem() == FileServer.Amazon)
			{
				ObjectMetaDataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem(); 
				AmazonObjectClickService s3ClickService = new AmazonObjectClickService(s3Supporter, clickedObject );
				s3ClickService.setOnSucceeded( event -> 
				{
					aClickedListView.setItems(s3ClickService.getValue());
				});
				s3ClickService.start();
			}
			if(aConnectedComboBox.getSelectionModel().getSelectedItem() == FileServer.Local)
			{
				ObjectMetaDataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem();
				LocalObjectClickService localClickService = new LocalObjectClickService(localSupporter, clickedObject);
				localClickService.setOnSucceeded(event ->{
					aClickedListView.setItems(localClickService.getValue());
				});
				localClickService.start();
			}
		}
			
	}
	
}
