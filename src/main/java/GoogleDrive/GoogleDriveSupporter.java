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
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.PreviousContainer;

public class GoogleDriveSupporter {

	
    private boolean isLoggedIn = false;
    private Drive driveService;
    private String currentDirectoryId;
    
    public ObservableList<ObjectMetaDataIf> getFilesList(String aParentId) throws IOException, GeneralSecurityException
    {
    	if(!isLoggedIn) 
    	{
    		getService();
    	}
    	GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
        List<File> files = downloader.getFilesList(driveService,aParentId,false);
        currentDirectoryId = aParentId;
        GoogleFileConverter converter = new GoogleFileConverter();
        boolean isRootFolder;
        if(aParentId.equals("root")) isRootFolder = true;
        else isRootFolder = false;
        return createObservableList(converter.convert(files),isRootFolder);
    }
    
    public ObservableList<ObjectMetaDataIf> backToPreviousContainer() throws IOException, GeneralSecurityException
    {
    	if(!isLoggedIn) 
    	{
    		getService();
    	}
    	GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
        List<File> files = downloader.getFilesList(driveService,currentDirectoryId,true);
        GoogleFileConverter converter = new GoogleFileConverter();
        return createObservableList(converter.convert(files),true);

    }
    public ObservableList<ObjectMetaDataIf> uploadFile(java.io.File aFile) throws GeneralSecurityException, IOException
    {
    	if(!isLoggedIn) 
    	{
    		getService();
    	}
    	GoogleDriveUploader uploader = new GoogleDriveUploader();
    	uploader.uploadFile(aFile, driveService);
    	return getFilesList(currentDirectoryId);
    }
    public java.io.File downloadFile(GoogleFileMetadata aMetadata, Path targetDirectory) throws IOException
    {
    	GoogleDriveFileDownloader googleFileDownloader = new GoogleDriveFileDownloader();
    	String fileId = aMetadata.getOrginalObject().getId();
    	return googleFileDownloader.downloadFile(aMetadata, targetDirectory.toString(), driveService);
    }
    private ObservableList<ObjectMetaDataIf> createObservableList(List<ObjectMetaDataIf> aListToConvert,boolean isRootDirectory)
    {
    	 if(!isRootDirectory)aListToConvert.add(0,new PreviousContainer());
    	 return FXCollections.observableArrayList(aListToConvert);
    }
    private void getService() throws GeneralSecurityException, IOException
    {
    	GoogleDriveLogInSupporter driveLogInSupporter = new GoogleDriveLogInSupporter();
    	driveService = driveLogInSupporter.getDriveService();
    	isLoggedIn = true;
    }
}
