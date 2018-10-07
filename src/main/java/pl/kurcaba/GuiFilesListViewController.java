package pl.kurcaba;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import AmazonS3.AmazonS3DownloadService;
import AmazonS3.AmazonS3Supporter;
import GoogleDrive.GoogleDriveDownloadService;
import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileMetadata;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;


public class GuiFilesListViewController<T> {
	
	
	@FXML
	private ListView filesListViewL;
	@FXML
	private ListView filesListViewR;
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
	
	private ObservableList<ObjectMetaDataIf<T>> filesList;
	
	public void initComponents()
	{
		initListView();
		initComboBoxes();
	}
	
	private void initListView()
	{
		
		filesListViewL.getSelectionModel().selectedItemProperty().addListener((event) -> {
			
			ObjectMetaDataIf<T> selectedFileMetaData = (ObjectMetaDataIf<T>) filesListViewL.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			lastModifiedTimeTextViewL.setText(dateFormat.format(selectedFileMetaData.getLastModifiedDate()));
			
		});
		filesListViewR.getSelectionModel().selectedItemProperty().addListener((event) ->{
			
			ObjectMetaDataIf<T> selectedFileMetaData = (ObjectMetaDataIf<T>) filesListViewR.getSelectionModel().getSelectedItem();
			selectedFileSizeTextFieldR.setText(selectedFileMetaData.getSize());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			lastModifiedTimeTextViewR.setText(dateFormat.format(selectedFileMetaData.getLastModifiedDate()));
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
	
}
