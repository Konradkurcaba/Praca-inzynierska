package pl.kurcaba;

import java.io.IOException;
import javafx.application.Application;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import GoogleDrive.GoogleDriveFileDownloader;
import GoogleDrive.GoogleDriveSupporter;
import GoogleDrive.GoogleFileConverter;
import GoogleDrive.GoogleFileMetadata;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
public class Main extends Application {

    
   
	public static void main(String... args) throws GeneralSecurityException, IOException
	{
        launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		  
		  FXMLLoader loader = new FXMLLoader(getClass().getResource("/GuiFilesList.fxml"));
		  loader.load();
		  Parent root = loader.getRoot();
		  primaryStage.setTitle("Cloud files Client");
		  primaryStage.setScene(new Scene(root));
		  GuiFilesListViewController controller = loader.getController();
		  controller.initComponents();
		  primaryStage.show();
		  
		
	}
}
