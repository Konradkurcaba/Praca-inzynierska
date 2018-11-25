package GoogleDrive;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GoogleDriveSupporter {

	
    private boolean isLoggedIn = false;
    private Drive driveService;
    
    
    public ObservableList<GoogleFileMetadata> getFilesList() throws IOException, GeneralSecurityException
    {
    	if(!isLoggedIn) 
    	{
    		getService();
    	}
    	GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
        List<File> files = downloader.getAllFilesList(driveService);
        GoogleFileConverter converter = new GoogleFileConverter();
        return FXCollections.observableArrayList(converter.convert(files));
    }
    
    private void getService() throws GeneralSecurityException, IOException
    {
    	GoogleDriveLogInSupporter driveLogInSupporter = new GoogleDriveLogInSupporter();
    	driveService = driveLogInSupporter.getDriveService();
    	isLoggedIn = true;
    }
	
    public ObservableList<GoogleFileMetadata> uploadFile(java.io.File aFile) throws GeneralSecurityException, IOException
    {
    	if(!isLoggedIn) 
    	{
    		getService();
    	}
    	GoogleDriveUploader uploader = new GoogleDriveUploader();
    	uploader.uploadFile(aFile, driveService);
    	return getFilesList();
    }
    
    public java.io.File downloadFile(GoogleFileMetadata aMetadata, Path targetDirectory) throws IOException
    {
    	GoogleDriveFileDownloader googleFileDownloader = new GoogleDriveFileDownloader();
    	String fileId = aMetadata.getOrginalObject().getId();
    	return googleFileDownloader.downloadFile(aMetadata, targetDirectory.toString(), driveService);
    }
    
//    public void uploadFile(java.io.File aFileToUpload,)
//    {
//    	
//    }
}
