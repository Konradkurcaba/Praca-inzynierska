package Synchronization;

import java.io.File;
import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

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
			SyncFileDownloader downloader = new SyncFileDownloader();
			File newVersionFile = downloader.downloadFile(aFileToSynchronize,supportersBundle);
			SyncFileUpdater updater = new SyncFileUpdater();
			updater.upload(newVersionFile, aSynchronizeTargetFile, supportersBundle);
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
					.getLocalWrappedFile(aFileToUpdate.getFileId()));
			default: throw new IllegalArgumentException("Not supported server");	
		}
	}
	
	
	
}
