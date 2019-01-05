package GoogleDrive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleDriveLogInSupporter {
	
	private static final String APP_NAME = "Cloud Files Client ";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_PATH = "credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String TOKENS_PATH = "tokens";
    

	public Drive getDriveService(String aAccountAlias) throws GeneralSecurityException, IOException
	{
	     NetHttpTransport HttpTransport = GoogleNetHttpTransport.newTrustedTransport();
		 Drive driveService = new Drive.Builder(HttpTransport, JSON_FACTORY, getCredentials(HttpTransport,aAccountAlias))
	                .setApplicationName(APP_NAME)
	                .build();
		 return driveService;
	}
	
	private Credential getCredentials(NetHttpTransport aHttpTransport,String aAccountAlias) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(CREDENTIALS_PATH);
        GoogleClientSecrets googleClientSecrets = null;
        googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
        
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                aHttpTransport, JSON_FACTORY, googleClientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, new LocalServerReceiver()).authorize(aAccountAlias);
    }
}
