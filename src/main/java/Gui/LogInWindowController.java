package Gui;


import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.kurcaba.ApplicationConfig;


public class LogInWindowController {

	@FXML
	private Text driveStatus;
	
	@FXML
	private Text s3Status;
	
	@FXML
	private ComboBox driveCombo;
	
	@FXML
	private ComboBox s3Combo;
	
	ObservableList<String> driveAccounts;
	ObservableList<String> s3Accounts;
	
	public void init(ApplicationConfig aAppConfig)
	{
		driveAccounts = FXCollections.observableArrayList(aAppConfig.getDriveAccounts());
		s3Accounts = FXCollections.observableArrayList(aAppConfig.getS3Accounts());
		initComboBoxes();
	}
	
	private void initComboBoxes()
	{
		driveCombo.getItems().setAll(driveAccounts);
		driveCombo.getItems().add("Nowe Konto");
		s3Combo.getItems().setAll(s3Accounts);
		s3Combo.getItems().add("Nowe Konto");
		
		driveCombo.setOnAction(event ->{
			if(driveCombo.getSelectionModel().getSelectedItem().equals("Nowe Konto"))
			{
				
				try {
					String newAccountAliast = showInputWindow("Nowe Konto","Podaj nazwê nowego konta");
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				
			}
		});
	}
	
	private String showInputWindow(String aWindowTitle, String aMessage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/InputWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();
		Stage inputWindow = new Stage();
		inputWindow.initModality(Modality.WINDOW_MODAL);
		inputWindow.initOwner(s3Combo.getScene().getWindow());
		inputWindow.setTitle(aWindowTitle);
		inputWindow.setScene(new Scene(root));
		InputWindowController inputWindowController = loader.getController();
		inputWindowController.init(aMessage);
		inputWindow.showAndWait();
		return(inputWindowController.getTextFieldValue());
	}
}
