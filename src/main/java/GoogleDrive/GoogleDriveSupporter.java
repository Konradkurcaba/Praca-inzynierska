package GoogleDrive;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import Synchronization.S3SyncFileData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.PreviousContainer;

public final class GoogleDriveSupporter {

	private Drive driveService;
	private String currentDirectoryId;
	private String rootFolderId;
	
	public void changeAccount(Drive aNewService) throws IOException
	{
		driveService = aNewService;
		GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
		rootFolderId = downloader.getRootId(driveService);
		currentDirectoryId = null;
	}

	public ObservableList<ObjectMetaDataIf> getFilesList(String aParentId) throws IOException
	{
		GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
		List<File> files = downloader.getFilesList(driveService, aParentId);
		currentDirectoryId = aParentId;
		GoogleFileConverter converter = new GoogleFileConverter();
		boolean isRootFolder = (aParentId.equals(rootFolderId) || aParentId.equals("root"));
		return createObservableList(converter.convert(files), isRootFolder);
	}

	public ObservableList<ObjectMetaDataIf> backToPreviousContainer() throws IOException{
		GoogleDriveFileDownloader downloader = new GoogleDriveFileDownloader();
		currentDirectoryId = downloader.getFilesParentId(driveService, currentDirectoryId);
		List<File> files = downloader.getFilesList(driveService, currentDirectoryId);
		GoogleFileConverter converter = new GoogleFileConverter();
		boolean isRootFolder = currentDirectoryId.equals(rootFolderId);
		return createObservableList(converter.convert(files), isRootFolder);
	}

	public GoogleFileMetadata uploadFile(java.io.File aFile) throws IOException {
		GoogleDriveUploader uploader = new GoogleDriveUploader();
		File uploadedFile = uploader.uploadFile(aFile, driveService, currentDirectoryId);
		return getFileMetadata(uploadedFile.getId());
	}
	
	public void updateFile(java.io.File aFile,String aFileId) throws IOException 
	{
		GoogleDriveUploader driveUploader = new GoogleDriveUploader();
		driveUploader.updateFile(aFile, aFileId, driveService);
	}

	public java.io.File downloadFile(GoogleFileMetadata aMetadata, Path targetDirectory) throws IOException {
		GoogleDriveFileDownloader googleFileDownloader = new GoogleDriveFileDownloader();
		String fileId = aMetadata.getOrginalObject().getId();
		return googleFileDownloader.downloadFile(fileId, targetDirectory.toString(), driveService);
	}
	
	public java.io.File downloadFile(String aId, java.io.File targetDirectory) throws IOException {
		GoogleDriveFileDownloader googleFileDownloader = new GoogleDriveFileDownloader();
		return googleFileDownloader.downloadFile(aId, targetDirectory.toString(), driveService);
	}

	public ObservableList<ObjectMetaDataIf> getFilesFromCurrentDir() throws IOException, GeneralSecurityException {
		return getFilesList(currentDirectoryId);
	}

	public void deleteObject(GoogleFileMetadata aFileMetadata) throws IOException, GeneralSecurityException {
		GoogleDriveFileDeleting fileDeleting = new GoogleDriveFileDeleting();
		fileDeleting.deleteObject(driveService, aFileMetadata.getOrginalObject().getId());
	}
	
	public void deleteObject(String aId) throws IOException {
		GoogleDriveFileDeleting fileDeleting = new GoogleDriveFileDeleting();
		fileDeleting.deleteObject(driveService, aId);
	}
	
	public void createFolder(String aFolderName) throws IOException
	{
		GoogleDriveFolderCreator folderCreator = new GoogleDriveFolderCreator();
		folderCreator.createFolder(driveService, aFolderName, currentDirectoryId);
	}
	
	public void changeName(GoogleFileMetadata aMetadata,String aNewName) throws IOException
	{
		GoogleDriveNameChanger nameChanger = new GoogleDriveNameChanger();
		nameChanger.changeName(driveService, aMetadata.getOrginalObject().getId(), aNewName);
	}
	
	public GoogleFileMetadata getFileMetadata(String aFileId) throws IOException
	{
		try
		{
			GoogleDriveFileDownloader driveDownloader = new GoogleDriveFileDownloader();
			return new GoogleFileMetadata(driveDownloader.getFileMetadata(driveService, aFileId));
		}catch (Exception ex)
		{
			return null;
		}
	}
	private ObservableList<ObjectMetaDataIf> createObservableList(List<ObjectMetaDataIf> aListToConvert,
			boolean isRootDirectory) {
		if (!isRootDirectory)
			aListToConvert.add(0, new PreviousContainer(FileServer.Google));
		return FXCollections.observableArrayList(aListToConvert);
	}
}
