package Gui;


import java.io.IOException;
import java.sql.SQLException;

import Threads.CreateNewDriveAccount;
import Threads.GetGoogleDriveService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.ApplicationConfig;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.TextColor;
import javafx.scene.control.ListCell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class AccountsWindowController {

	@FXML
	private Text driveStatus;
	
	@FXML
	private Text s3Status;
	
	@FXML
	private ComboBox driveCombo;
	
	@FXML
	private ComboBox s3Combo;
	
	private ApplicationConfig applicationConfig;
	private AccountsSupervisor accountsSupervisor;
	
	
	public void init(ApplicationConfig aAppConfig,AccountsSupervisor aAccountsSupervisor)
	{
		applicationConfig = aAppConfig;
		accountsSupervisor = aAccountsSupervisor;
		//s3Accounts = FXCollections.observableArrayList(aAppConfig.getS3Accounts());
		initComboBoxes();
		refreshStatus();
	}
	
	private void refreshStatus() {
		if(accountsSupervisor.isDriveLoggedIn())
		{
			driveStatus.setFill(Paint.valueOf(TextColor.Green.getColor()));
			driveStatus.setText("Zalogowano");
		}else
		{
			driveStatus.setText("Niezalogowany");
			driveStatus.setFill(Paint.valueOf(TextColor.Red.getColor()));
		}
	}
	

	private void initComboBoxes()
	{
		driveCombo.getItems().setAll(applicationConfig.getDriveAccounts());
		driveCombo.getItems().add("Nowe Konto...");
		if(accountsSupervisor.isDriveLoggedIn())
		{
			driveCombo.getSelectionModel().select(accountsSupervisor.getCurrentAccount());
		}
		//s3Combo.getItems().setAll(s3Accounts);
		//s3Combo.getItems().add("Nowe Konto");
		driveCombo.setOnAction(event ->{
			
			driveStatus.setText("Logowanie...");
			driveStatus.setFill(Paint.valueOf(TextColor.Grey.getColor()));
			String selectedAccount = (String)driveCombo.getSelectionModel().getSelectedItem();
			if(selectedAccount.equals("Nowe Konto..."))
			{
				try {
					String newAccountAlias = showInputWindow("Nowe Konto","Podaj nazwê nowego konta");
					CreateNewDriveAccount createNewDriveAccount = new CreateNewDriveAccount(applicationConfig, newAccountAlias, accountsSupervisor);
					createNewDriveAccount.setOnSucceeded(successEvent ->{
						driveCombo.getItems().add(0, newAccountAlias);
						driveCombo.getSelectionModel().select(newAccountAlias);	
						refreshStatus();
					});
					createNewDriveAccount.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				GetGoogleDriveService getGoogleDriveService = new GetGoogleDriveService(applicationConfig
						,selectedAccount,accountsSupervisor);
				getGoogleDriveService.setOnSucceeded(successEvent -> {
					refreshStatus();
					});
				getGoogleDriveService.start();
			}
		});
		
		driveCombo.setCellFactory(lv -> new ListCell<String>() {
			@Override
			protected void updateItem(String aCellValue,boolean isEmpty) {
				super.updateItem(aCellValue,isEmpty);
				if (isEmpty || aCellValue == null) {
					setText("");
					setContextMenu(null);
				} else {
					setText(aCellValue);
					ContextMenu contextMenu = new ContextMenu();
					MenuItem deleteItem = new MenuItem("Usuñ");
					deleteItem.setOnAction(event ->{
						try {
							applicationConfig.deleteDriveAccount(aCellValue);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
					contextMenu.getItems().add(deleteItem);
					setContextMenu(contextMenu);
				} 
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
