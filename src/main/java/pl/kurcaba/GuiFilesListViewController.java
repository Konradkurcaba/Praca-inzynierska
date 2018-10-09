package pl.kurcaba;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import AmazonS3.AmazonS3Supporter;
import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import Threads.AmazonObjectClickService;
import Threads.AmazonS3DownloadService;
import Threads.GoogleDriveDownloadService;
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
	
	
	public void initComponents()
	{
		initListView();
		initComboBoxes();
	}
	
	private void initListView()
	{
		
		filesListViewL.getSelectionModel().selectedItemProperty().addListener((event) -> {
			
			ObjectMetaDataIf selectedFileMetaData = (ObjectMetaDataIf) filesListViewL.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			lastModifiedTimeTextViewL.setText(dateFormat.format(selectedFileMetaData.getLastModifiedDate()));
			
		});
		filesListViewR.getSelectionModel().selectedItemProperty().addListener((event) ->{
			
			ObjectMetaDataIf selectedFileMetaData = (ObjectMetaDataIf) filesListViewR.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldR.setText(selectedFileMetaData.getSize());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			lastModifiedTimeTextViewR.setText(dateFormat.format(selectedFileMetaData.getLastModifiedDate()));
		});
		
		filesListViewL.setOnMouseClicked(MouseEvent -> {
			listViewClicked(MouseEvent,filesListViewL);
		});
	}
	
	private void initComboBoxes()
	{
		filesServerComboL.getItems().addAll(FileServer.Google,FileServer.Amazon);
		filesServerComboR.getItems().addAll(FileServer.Google,FileServer.Amazon);
		
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
				AmazonS3DownloadService downloadService = new AmazonS3DownloadService(s3Supporter);
				downloadService.setOnSucceeded( Event -> {
					aListView.setItems(downloadService.getValue());
				});
				downloadService.start();
				
			}
		}
	}
	
	private void listViewClicked(MouseEvent aMouseEvent,ListView<ObjectMetaDataIf> aClickedListView)
	{
		boolean isDoubleClick = aMouseEvent.getClickCount() == 2;
		if(isDoubleClick)
		{
			ObjectMetaDataIf clickedObject = aClickedListView.getSelectionModel().getSelectedItem(); 
			AmazonObjectClickService s3ClickService = new AmazonObjectClickService(s3Supporter, clickedObject );
			s3ClickService.start();
		}
			
	}
	
}
