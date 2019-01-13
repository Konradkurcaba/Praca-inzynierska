package Gui;

import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import com.amazonaws.regions.Regions;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

public class AmazonNewAccountController {

	@FXML
	private TextField accountNameField;

	@FXML
	private TextField accessKeyField;

	@FXML
	private TextField secretKeyField;
	
	@FXML					
	private Button okButton;
	
	@FXML
	private ComboBox<Regions> regionCombo;
	
	private boolean wasOkButtonPressed = false;
	private String accountName;
	private String accesKey;
	private String secretKey;
	private Regions selectedRegion;
	
	public void init()
	{
		regionCombo.setItems(FXCollections.observableArrayList(Regions.values()));
		
		regionCombo.setOnAction(event ->{
			selectedRegion = regionCombo.getSelectionModel().getSelectedItem();
		});
		
		okButton.setOnAction(event ->{
			wasOkButtonPressed = true;
			accountName = accountNameField.getText();
			accesKey = accessKeyField.getText();
			secretKey = secretKeyField.getText();
			Stage thisStage = (Stage) okButton.getScene().getWindow();
			thisStage.close();
		});
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getAccesKey() {
		return accesKey;
	}
	
	public Regions getSelectedRegion()
	{
		return selectedRegion;
	}
	
	public boolean wasOkButtonPressed()
	{
		return wasOkButtonPressed;
	}
}
