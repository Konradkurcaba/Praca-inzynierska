package Gui;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MessageWindowController {
	
	@FXML
	private Text mainText;
	
	@FXML
	private Button okButton;
	
	public void init(String text)
	{
		mainText.setText(text);
		
		okButton.setOnAction(click ->{
			Stage thisStage = (Stage) okButton.getScene().getWindow();
			thisStage.close();
		});
	}
	
	
	
}
