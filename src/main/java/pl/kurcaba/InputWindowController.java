package pl.kurcaba;

import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class InputWindowController {

	@FXML
	private Text titleText;
	@FXML
	private TextField textField;
	@FXML	
	private Button okButton; 
	
	private String textFromField;
	
	
	public void init(String aTitleText) {
		titleText.setText(aTitleText);
		
		okButton.setOnMouseClicked(event ->{
			textFromField = textField.getText();
			Stage thisStage = (Stage) okButton.getScene().getWindow();
			thisStage.close();
		});
	}
	
	public String getTextFieldValue()
	{
		return textFromField;
	}
	
	
}
