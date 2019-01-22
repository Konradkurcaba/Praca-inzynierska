package Gui;


import java.io.IOException;
import java.sql.SQLException;

import com.amazonaws.regions.Regions;

import AmazonS3.AmazonAccountInfo;
import Threads.ChangeAmazonAccountService;
import Threads.ChangeDriveService;
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
import pl.kurcaba.ObjectMetadataIf;
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
		initComboBoxes();
		refreshStatus();
	}
	
	private void refreshStatus() {
		if(accountsSupervisor.isDriveLoggedIn())
		{
			driveStatus.setFill(Paint.valueOf(TextColor.Green.getColor()));
			driveStatus.setText("Zalogowano");
			driveCombo.getSelectionModel().select(accountsSupervisor.getCurrentDriveAccount());
		}else
		{
			driveStatus.setText("Niezalogowany");
			driveStatus.setFill(Paint.valueOf(TextColor.Red.getColor()));
			driveCombo.getSelectionModel().clearSelection();
		}
		if(accountsSupervisor.isS3LoggedIn())
		{
			s3Status.setFill(Paint.valueOf(TextColor.Green.getColor()));
			s3Status.setText("Zalogowano");
			s3Combo.getSelectionModel().select(accountsSupervisor.getCurrentS3Account());
		}else
		{
			s3Status.setText("Niezalogowany");
			s3Status.setFill(Paint.valueOf(TextColor.Red.getColor()));
			s3Combo.getSelectionModel().clearSelection();
		}
	}
	

	private void initComboBoxes()
	{
		driveCombo.getItems().setAll(applicationConfig.getDriveAccounts());
		driveCombo.getItems().add("Nowe Konto...");
		if(accountsSupervisor.isDriveLoggedIn())
		{
			driveCombo.getSelectionModel().select(accountsSupervisor.getCurrentDriveAccount());
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
					if(newAccountAlias != null)
					{
						ChangeDriveService createNewDriveAccount = new ChangeDriveService(applicationConfig
								, newAccountAlias, accountsSupervisor);
						createNewDriveAccount.setOnSucceeded(successEvent ->{
							driveCombo.getItems().add(0, newAccountAlias);
							driveCombo.getSelectionModel().select(newAccountAlias);	
							refreshStatus();
						});
						createNewDriveAccount.start();
					}else
					{
						refreshStatus();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				ChangeDriveService getGoogleDriveService = new ChangeDriveService(applicationConfig
						,selectedAccount,accountsSupervisor);
				getGoogleDriveService.setOnSucceeded(successEvent -> {
					refreshStatus();
					});
				getGoogleDriveService.start();
			}
		});
		
		if(accountsSupervisor.isS3LoggedIn())
		{
			s3Combo.getSelectionModel().select(accountsSupervisor.getCurrentS3Account());
		}
		s3Combo.getItems().setAll(applicationConfig.getS3Accounts());
		s3Combo.getItems().add("Nowe Konto...");
		s3Combo.setOnAction(action -> {
			if(s3Combo.getSelectionModel().getSelectedItem()!= null)
			{
				changeAmazonAccount();
			}
		});
	}
	
	private void changeAmazonAccount()
	{
		s3Status.setText("Logowanie...");
		s3Status.setFill(Paint.valueOf(TextColor.Grey.getColor()));
		String selectedAccount = s3Combo.getSelectionModel().getSelectedItem().toString();
		if(selectedAccount.equals("Nowe Konto..."))
		{
			try
			{
				AmazonAccountInfo newAmazonAccountInfo = showAmazonWindow();
				if(newAmazonAccountInfo != null)
				{
					ChangeAmazonAccountService createAmazonAccount = new ChangeAmazonAccountService(applicationConfig
							, newAmazonAccountInfo, accountsSupervisor);
					createAmazonAccount.setOnSucceeded(event ->{
						s3Combo.getItems().add(0,newAmazonAccountInfo);
						s3Combo.getSelectionModel().select(newAmazonAccountInfo);
						refreshStatus();
					});
					createAmazonAccount.setOnFailed(event -> refreshStatus());
					createAmazonAccount.start();
				}
				else refreshStatus();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}else
		{
			ChangeAmazonAccountService changeAmazonAccount = new ChangeAmazonAccountService(applicationConfig
					,(AmazonAccountInfo) s3Combo.getSelectionModel().getSelectedItem(), accountsSupervisor);
			changeAmazonAccount.setOnSucceeded(event ->{
				refreshStatus();
			});
			changeAmazonAccount.start();
		}
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
	
	private AmazonAccountInfo showAmazonWindow() throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/amazonAccountWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();
		Stage amazonWindow = new Stage();
		amazonWindow.initModality(Modality.WINDOW_MODAL);
		amazonWindow.initOwner(s3Combo.getScene().getWindow());
		amazonWindow.setTitle("Nowe konto");
		amazonWindow.setScene(new Scene(root));
		AmazonNewAccountController amazonWindowController = loader.getController();
		amazonWindowController.init();
		amazonWindow.showAndWait();
		if(amazonWindowController.wasOkButtonPressed())
		{
			return new AmazonAccountInfo(amazonWindowController.getAccountName(), amazonWindowController.getAccesKey()
					, amazonWindowController.getSecretKey(),amazonWindowController.getSelectedRegion());
		}else return null;
	}
	
	
	
}
