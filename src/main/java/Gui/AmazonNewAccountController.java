package Gui;

import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AmazonNewAccountController {

	@FXML
	private TextField accountNameField;

	@FXML
	private TextField accessKeyField;

	@FXML
	private TextField secretKeyField;
	
	@FXML
	private Button okButton;
	
	private boolean wasOkButtonPressed = false;
	private String accountName;
	private String accesKey;
	private String secretKey;
	
	public void init()
	{
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
	
	public boolean wasOkButtonPressed()
	{
		return wasOkButtonPressed;
	}
}
