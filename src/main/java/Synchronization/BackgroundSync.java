package Synchronization;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import Local.LocalFileMetadata;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

public class BackgroundSync implements Runnable {

	private final SupportersBundle supportersBundle;
	private final Map<SyncFileData,SyncFileData> filesToAddToSync;
	private final Map<SyncFileData,SyncFileData> filesToDeleteFromSync;
	private final int period = 30000;
	
	public BackgroundSync(SupportersBundle aBundle,Map<SyncFileData,SyncFileData> aFilesMap, Map<SyncFileData,SyncFileData> aDelFilesMap ) {
		supportersBundle = aBundle;
		filesToAddToSync = aFilesMap;
		filesToDeleteFromSync = aDelFilesMap;
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted())
		{
			try {
				synchTask();
				Thread.sleep(period);
			} catch (SQLException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void doSynchronize(Map<SyncFileData,SyncFileData> aFilesToSynchonize) throws IOException, SQLException
	{
		
		for(Map.Entry<SyncFileData, SyncFileData> entry : aFilesToSynchonize.entrySet())
		{
			synchronize(entry.getKey(), entry.getValue());
		}
	}
	
	private void synchronize(SyncFileData aFileToSynchronize,SyncFileData aSynchronizeTargetFile) throws IOException, SQLException
	{
		SyncFileData actualFileMetadata = getActualFileData(aFileToSynchronize);
		if(actualFileMetadata == null)
		{
			DatabaseSupervisor supervisor = new DatabaseSupervisor();
			supervisor.removeSyncData(aFileToSynchronize, aSynchronizeTargetFile);
		}else
		{
			if(!actualFileMetadata.equals(aFileToSynchronize))
			{
				SyncFileDownloader downloader = new SyncFileDownloader();
				File newVersionFile = downloader.downloadFile(aFileToSynchronize,supportersBundle);
				SyncFileUpdater updater = new SyncFileUpdater();
				updater.upload(newVersionFile, aSynchronizeTargetFile, supportersBundle);
				DatabaseSupervisor databaseSupervisor = new DatabaseSupervisor();
				databaseSupervisor.removeSyncData(aFileToSynchronize, aSynchronizeTargetFile);
				
				SyncFileData newFileSyncData = getActualFileData(aFileToSynchronize);
				SyncFileData newTargetSyncData = getActualFileData(aSynchronizeTargetFile);
				
				databaseSupervisor.saveSyncData(newFileSyncData, newTargetSyncData);
				databaseSupervisor.closeConnection();
			}
		}
	}	
	
	private SyncFileData getActualFileData(SyncFileData aFileToUpdate) throws IOException
	{
		switch(aFileToUpdate.getFileServer())
		{
		case Amazon:
			S3SyncFileData s3FileData = (S3SyncFileData) aFileToUpdate;
			ObjectMetaDataIf metadata = supportersBundle.getAmazonS3Supporter().getAmazons3ObjMetadata(s3FileData.getKey()
					,s3FileData.getBucketName());
			if(metadata != null)
			{
				return new S3SyncFileData(metadata);
			}else return null;
			
		case Google:
			ObjectMetaDataIf googleFileMetadata = (supportersBundle.getGoogleDriveSupporter()
					.getFileMetadata(aFileToUpdate.getFileId()));
			if(googleFileMetadata != null) return new SyncFileData();
			else return null;
		case Local:
			ObjectMetaDataIf actualMetadata = supportersBundle.getLocalFileSupporter()
					.getLocalWrappedFile(aFileToUpdate.getFileId());
			if (actualMetadata != null)
			{
				return new SyncFileData(actualMetadata);
			}else return null;
			default: throw new IllegalArgumentException("Not supported server");	
		}
	}
	
	public synchronized Map<SyncFileData,SyncFileData> syncDatabase() throws SQLException
	{
		Map<SyncFileData,SyncFileData> filesToAdd = new HashMap();
		Map<SyncFileData,SyncFileData> filesToDelete = new HashMap();
		
		synchronized(filesToAddToSync)
		{
			filesToAdd.putAll(filesToAddToSync);
			filesToAddToSync.clear();
		}
		synchronized(filesToDeleteFromSync)
		{
			filesToDelete.putAll(filesToDeleteFromSync);
			filesToDeleteFromSync.clear();
		}
		DatabaseSupervisor databaseSupervisor = new DatabaseSupervisor();
		
		for(Map.Entry<SyncFileData, SyncFileData> pair : filesToAdd.entrySet() )
		{
			databaseSupervisor.saveSyncData(pair.getKey(), pair.getValue());
		}
		
		for(Map.Entry<SyncFileData, SyncFileData> pair : filesToDelete.entrySet() )
		{
			databaseSupervisor.removeSyncData(pair.getKey(), pair.getValue());
		}
		
		Map<SyncFileData,SyncFileData> filesToSynchonize = databaseSupervisor.getSyncMap();
		databaseSupervisor.closeConnection();
		return filesToSynchonize;
	}
	
	
	private void synchTask() throws SQLException, IOException
	{
		doSynchronize(syncDatabase());
	}
	
}
