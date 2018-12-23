package Synchronization;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import AmazonS3.AmazonS3ObjectMetadata;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.Settings;
import pl.kurcaba.SupportersBundle;

public class SyncFileDownloader {

	public File downloadFile(ObjectMetaDataIf aFileToDownload, SupportersBundle aBundle) throws IOException {
		
		File downloadedFile = null;
		File targetDirectory = new File(Settings.WORKING_DIRECTORY);
		if (aFileToDownload instanceof AmazonS3ObjectMetadata) {
			AmazonS3ObjectMetadata s3ObjectMetadata = (AmazonS3ObjectMetadata) aFileToDownload;

			try {
				downloadedFile = aBundle.getAmazonS3Supporter().getAmazonS3Object(s3ObjectMetadata, targetDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (aFileToDownload instanceof GoogleFileMetadata) {
			GoogleFileMetadata googleFileMetadata = (GoogleFileMetadata) aFileToDownload;

			try {
				downloadedFile = aBundle.getGoogleDriveSupporter().downloadFile(googleFileMetadata,
						targetDirectory.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (aFileToDownload instanceof LocalFileMetadata) {
			LocalFileMetadata localFileMetadata = (LocalFileMetadata) aFileToDownload;
			if (!localFileMetadata.getOrginalObject().isDirectory()) {
				File copyOfFile = new File(targetDirectory.toString() + "\\" + localFileMetadata.getName());
				Files.copy(localFileMetadata.getOrginalObject(), copyOfFile);
				downloadedFile = copyOfFile;
			}
		}
		return downloadedFile;
	}
	
	
	public File downloadFile(SyncFileData aFileToDownload,SupportersBundle aSupportersBundle) throws IOException
	{
		File downloadedFile = null;
		File targetDirectory = new File(Settings.WORKING_DIRECTORY);
		switch(aFileToDownload.getFileServer())
		{
		case Amazon:
			if(aFileToDownload instanceof S3SyncFileData)
			{
				S3SyncFileData s3File = (S3SyncFileData) aFileToDownload;
				downloadedFile = aSupportersBundle.getAmazonS3Supporter().getAmazonS3Object(s3File.getKey()
						, s3File.getBucketName(),targetDirectory);
			}
		break;
		case Local:
			downloadedFile = aSupportersBundle.getLocalFileSupporter().getLocalFile(aFileToDownload.getFileId());
			break;
		case Google:
			downloadedFile = aSupportersBundle.getGoogleDriveSupporter().downloadFile(aFileToDownload.getFileId(), targetDirectory);
			break;
		}
		return downloadedFile;
	}

}
