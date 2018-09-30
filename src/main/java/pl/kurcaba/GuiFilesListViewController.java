package pl.kurcaba;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;


public class GuiFilesListViewController<T> {
	
	
	@FXML
	private ListView filesListViewL;
	@FXML
	private TextField selectedFileSizeTextFieldL;
	@FXML
	private TextField lastModifiedTimeTextViewL;
	@FXML 
	private ComboBox filesServerComboL;
	@FXML
	private ComboBox filesServerComboR;
	
	private ObservableList<FileMetaDataIf<T>> filesList;
	
	public void initData(List<FileMetaDataIf<T>> aFileList)
	{
		filesList = FXCollections.observableList(aFileList);
		filesListViewL.setItems(filesList);
		filesListViewL.setOnMouseClicked( event ->{
			itemSelectedAction();
		});
		
	}
	
	private void itemSelectedAction()
	{
		FileMetaDataIf<T> selectedFileMetaData = (FileMetaDataIf<T>) filesListViewL.getSelectionModel().getSelectedItem();
		selectedFileSizeTextFieldL.setText(selectedFileMetaData.getSize());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		lastModifiedTimeTextViewL.setText(dateFormat.format(selectedFileMetaData.getLastModifiedDate()));
	}
	
	
	
}
