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
	private ListView filesListView;
	@FXML
	private TextField selectedFileSizeTextField;
	@FXML
	private TextField lastModifiedTimeTextView;
	private ObservableList<FileMetaDataIf<T>> filesList;
	
	public void initData(List<FileMetaDataIf<T>> aFileList)
	{
		filesList = FXCollections.observableList(aFileList);
		filesListView.setOnMouseClicked( event ->{
			itemSelectedAction();
		});
		
	}
	
	private void itemSelectedAction()
	{
		FileMetaDataIf<T> selectedFileMetaData = (FileMetaDataIf<T>) filesListView.getSelectionModel().getSelectedItem();
		selectedFileSizeTextField.setText(selectedFileMetaData.getSize());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		lastModifiedTimeTextView.setText(dateFormat.format(selectedFileMetaData.getLastModifiedDate()));
	}
	
	
	
}
