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
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
public class Main extends Application {

	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    
    
    private static List filesMeta;
	public static void main(String... args) throws GeneralSecurityException, IOException
	{
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		 Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                .setApplicationName(APPLICATION_NAME)
	                .build();
	  
		 
		GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
        List<File> files = downloader.getAllFilesList(service);
        
        GoogleFileConverter converter = new GoogleFileConverter();
        filesMeta = converter.convert(files); 
        
        launch();
        
        
        
//        downloader.downloadFile("1kWZm8ryvXG0iIJjSOYwrp-YxcrDPIW0P", "java.png", service);
//        
//        FileUploader uploader = new FileUploader();
//        uploader.uploadFile(new java.io.File("java2.png"), service);
        
	}
	
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = ClassLoader.getSystemResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = null;
        try
        {
        	clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        }catch(IOException aEx)
        {
        	aEx.printStackTrace();
        }
        
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("testAccount3");
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		  
		  FXMLLoader loader  = new FXMLLoader(getClass().getResource("GuiFilesList.fxml"));
		  loader.load();
		  Parent root = loader.getRoot();
		  primaryStage.setTitle("Cloud files Client");
		  primaryStage.setScene(new Scene(root));
		  GuiFilesListViewController<GoogleFileMetadata> controller = loader.getController();
		  controller.initData(filesMeta);
		  
		  primaryStage.show();
		  
		
	}
}
