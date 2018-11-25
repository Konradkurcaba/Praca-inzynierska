package Threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.google.common.io.Files;

import AmazonS3.AmazonS3FileDownloader;
import AmazonS3.AmazonS3ObjectMetadata;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.Settings;
import pl.kurcaba.SupportersBundle;

public class CopyService extends Service {

	private final SupportersBundle bundle;
	private final FileServer targetServer;
	private final ObjectMetaDataIf objectToCopy;

	public CopyService(SupportersBundle aBundle, ObjectMetaDataIf aObject, FileServer aTargetServer) {
		bundle = aBundle;
		targetServer = aTargetServer;
		objectToCopy = aObject;
	}

	@Override
	protected Task createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				File downloadedfile = downloadFile();
				try {
					moveFile(downloadedfile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}

	private File downloadFile() throws IOException
	{	File downloadedFile = null;
		File targetDirectory = new File(Settings.WORKING_DIRECTORY);
		if(objectToCopy instanceof AmazonS3ObjectMetadata)
		{
			AmazonS3ObjectMetadata s3ObjectMetadata = (AmazonS3ObjectMetadata) objectToCopy;
			
			try {
				
				downloadedFile = bundle.getAmazonS3Supporter().getAmazonS3Object(s3ObjectMetadata,targetDirectory );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else if(objectToCopy instanceof GoogleFileMetadata)
		{
			GoogleFileMetadata googleFileMetadata = (GoogleFileMetadata) objectToCopy;
			
			try
			{
				downloadedFile = bundle.getGoogleDriveSupporter().downloadFile(googleFileMetadata
						,targetDirectory.toPath());
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(objectToCopy instanceof LocalFileMetadata)
		{
			LocalFileMetadata localFileMetadata = (LocalFileMetadata) objectToCopy;
			if (!localFileMetadata.getOrginalObject().isDirectory())
			{
				 File copyOfFile = new File(targetDirectory.toString() 
						 + "\\" + localFileMetadata.getName());
				 Files.copy(localFileMetadata.getOrginalObject(),copyOfFile);
				 downloadedFile = copyOfFile;
			}
		}
		return downloadedFile;
	}

	private void moveFile(File aFileToCopy) throws IOException {
		if (targetServer.equals(FileServer.Local)) {
			bundle.getLocalFileSupporter().moveFileToCurrentDirectory(aFileToCopy.toPath());
		}
	}

}
