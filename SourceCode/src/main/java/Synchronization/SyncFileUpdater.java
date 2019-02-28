package Synchronization;

import java.io.File;
import java.io.IOException;

import pl.kurcaba.HelpersBundle;

public class SyncFileUpdater {

	
	public void upload(File aFileToUpload,SyncFileData aFileToUpdate,HelpersBundle aBundle) throws IOException
	{
		switch(aFileToUpdate.getFileServer())
		{
		case AmazonS3:
			S3SyncFileData s3Metadata = (S3SyncFileData) aFileToUpdate;
			aBundle.getAmazonS3Supporter().deleteObject(s3Metadata);
			aBundle.getAmazonS3Supporter().uploadFile(s3Metadata.getKey(), s3Metadata.getBucketName(), aFileToUpload);
			break;
		case GoogleDrive:
		//	aBundle.getLocalFileSupporter().deleteFile(aLocalFile);
			aBundle.getGoogleDriveSupporter().updateFile(aFileToUpload, aFileToUpdate.getFileId());
			break;
		case Komputer:
			aBundle.getLocalFileSupporter().deleteSyncFile(aFileToUpdate);
			aBundle.getLocalFileSupporter().moveFile(aFileToUpload,aFileToUpdate.getFileId());
			break;
		}
		
	}
}
