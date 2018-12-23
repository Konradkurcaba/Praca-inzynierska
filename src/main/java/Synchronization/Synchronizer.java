package Synchronization;

import java.io.File;
import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

public class Synchronizer {
	
	private Map<SyncFileData,SyncFileData> filesToAddToSynchronize = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private final SupportersBundle supportersBundle;

	
	public Synchronizer(SupportersBundle aSupportersBundle)  {
		supportersBundle = aSupportersBundle;
	}
	
	public void addFilesToSynchronize(ObjectMetaDataIf aFileToSynchronize,ObjectMetaDataIf aSynchronizeTargetFile) throws SQLException
	{
		synchronized(filesToAddToSynchronize)
		{
			filesToAddToSynchronize.put(new SyncFileData(aFileToSynchronize),new SyncFileData(aSynchronizeTargetFile));
		}
	}
	
	public void doSynchronize(Map<SyncFileData,SyncFileData> aFilesToSynchonize) throws IOException
	{
		
		for(Map.Entry<SyncFileData, SyncFileData> entry : filesToAddToSynchronize.entrySet())
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
			S3SyncFileData s3FileData = (S3SyncFileData) aFileToUpdate;
			return supportersBundle.getAmazonS3Supporter().getAmazons3ObjMetadata(s3FileData.getKey()
					,s3FileData.getBucketName());
		case Google:
			return new SyncFileData(supportersBundle.getGoogleDriveSupporter()
					.getFileMetadata(aFileToUpdate.getFileId()));
		case Local:
			return new SyncFileData(supportersBundle.getLocalFileSupporter()
					.getLocalWrappedFile(aFileToUpdate.getFileId()));
			default: throw new IllegalArgumentException("Not supported server");	
		}
	}
	private void synchronizationThread() throws SQLException, IOException
	{
		Map<SyncFileData,SyncFileData> filesToAdd = new HashMap();
		synchronized(filesToAddToSynchronize)
		{
			filesToAdd.putAll(filesToAddToSynchronize);
			filesToAddToSynchronize.clear();
		}
		DatabaseSupervisor databaseSupervisor = new DatabaseSupervisor();
		
		for(Map.Entry<SyncFileData, SyncFileData> pair : filesToAdd.entrySet() )
		{
			databaseSupervisor.saveSyncData(pair.getKey(), pair.getValue());
		}
		
		Map<SyncFileData,SyncFileData> filesToSynchonize = databaseSupervisor.getSyncMap();
		doSynchronize(filesToSynchonize);
		
		
		
	}
	
	
}
