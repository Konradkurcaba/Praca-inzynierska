package Synchronization;

import java.io.File;

import pl.kurcaba.SupportersBundle;

public class S3SyncFileUpdater {

	
	public void upload(File aFileToUpload,SyncFileData aFileToUpdate,SupportersBundle aBundle)
	{
		switch(aFileToUpade.getFileServer())
		{
		case Amazon:
			aBundle.getAmazonS3Supporter().uploadFileToCurrentDir(aFileToUpload);
			break;
		case Google:
			aBundle.getGoogleDriveSupporter().updateFile(aFileToUpload, aFileToUpdate.getFileId());
			break;
		case Local:
			aBundle.getLocalFileSupporter().
		}
		
		
	}
	
	
	
	
}
