package Gui;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class YesNoWindowController {

	@FXML
	private Text mainText;
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	
	private boolean isOkClicked = false;
	
	public void init(String displayText)
	{
		mainText.setText(displayText);
		okButton.setOnAction(click ->{
			isOkClicked = true;
			Stage thisStage = (Stage) okButton.getScene().getWindow();
			thisStage.close();
		});
		cancelButton.setOnAction(click ->{
			Stage thisStage = (Stage) cancelButton.getScene().getWindow();
			thisStage.close();
		});
	}
	
	public boolean isOkClicked()
	{
		return isOkClicked;
	}
}
