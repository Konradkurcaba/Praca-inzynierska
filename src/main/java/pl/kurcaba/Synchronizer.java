package pl.kurcaba;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Synchronizer {
	
	private Map<SyncFileData,SyncFileData> filesToSynchronize = Collections.synchronizedMap(
			new HashMap<SyncFileData, SyncFileData>());
	private final SupportersBundle supportersBundle;
	
	public Synchronizer(SupportersBundle aSupportersBundle) {
		supportersBundle = aSupportersBundle;
	}
	
	public void addFilesToSynchronize(ObjectMetaDataIf aFileToSynchronize,ObjectMetaDataIf aSynchronizeTargetFile)
	{
		filesToSynchronize.put(new SyncFileData(aFileToSynchronize),new SyncFileData(aSynchronizeTargetFile));
	}
	
	public void doSynchronize() throws IOException
	{
		for(Map.Entry<SyncFileData, SyncFileData> entry : filesToSynchronize.entrySet())
		{
			synchronize(entry.getValue(), entry.getKey());
		}
	}
	
	private void synchronize(SyncFileData aFileToSynchronize,SyncFileData aSynchronizeTargetFile) throws IOException
	{
		SyncFileData actualFileMetadata = getActualFileData(aFileToSynchronize);
		if(!actualFileMetadata.equals(aFileToSynchronize))
		{
			AnyFileDownloader anyFileDownloader = new AnyFileDownloader();
			File syncFile = anyFileDownloader.downloadFile(aFileToDownload, supportersBundle);
		}
		
	}
	
	private SyncFileData getActualFileData(SyncFileData aFileToUpdate) throws IOException
	{
		switch(aFileToUpdate.getFileServer())
		{
		case Amazon:
			return supportersBundle.getAmazonS3Supporter().getAmazons3ObjMetadata(aFileToUpdate.getFileId()
					,((S3SyncFileData)aFileToUpdate).getBucketName());
		case Google:
			return new SyncFileData(supportersBundle.getGoogleDriveSupporter()
					.getFileMetadata(aFileToUpdate.getFileId()));
		case Local:
			return new SyncFileData(supportersBundle.getLocalFileSupporter()
					.getLocalFile(aFileToUpdate.getFileId()));
			default: throw new IllegalArgumentException("Not supported server");	
		}
	}
	
	
	
	
	
	
	
}
