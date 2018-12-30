package Synchronization;

import java.io.File;
import java.io.IOException;

import pl.kurcaba.SupportersBundle;

public class SyncFileUpdater {

	
	public void upload(File aFileToUpload,SyncFileData aFileToUpdate,SupportersBundle aBundle) throws IOException
	{
		switch(aFileToUpdate.getFileServer())
		{
		case Amazon:
			S3SyncFileData s3Metadata = (S3SyncFileData) aFileToUpdate;
			aBundle.getAmazonS3Supporter().deleteObject(s3Metadata);
			aBundle.getAmazonS3Supporter().uploadFile(s3Metadata.getKey(), s3Metadata.getBucketName(), aFileToUpload);
			break;
		case Google:
		//	aBundle.getLocalFileSupporter().deleteFile(aLocalFile);
			aBundle.getGoogleDriveSupporter().updateFile(aFileToUpload, aFileToUpdate.getFileId());
			break;
		case Local:
			aBundle.getLocalFileSupporter().deleteSyncFile(aFileToUpdate);
			aBundle.getLocalFileSupporter().moveFile(aFileToUpload,aFileToUpdate.getFileId());
			break;
		}
		
	}
}
