package Synchronization;

import java.io.File;
import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import AmazonS3.AmazonS3ObjectMetadata;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.SupportersBundle;

public class Synchronizer {
	
	private Map<SyncFileData,SyncFileData> filesToAddToSync = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private Map<SyncFileData,SyncFileData> filesToDeleteFromSync = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private final SupportersBundle supportersBundle;
	private Thread syncThread;
	private boolean isSyncOn;
	
	public Synchronizer(SupportersBundle aSupportersBundle)  {
		supportersBundle = aSupportersBundle;
	}
	
	public void addFilesToSynchronize(ObjectMetaDataIf aFileToSynchronize,ObjectMetaDataIf aSynchronizeTargetFile) throws SQLException
	{
		synchronized(filesToAddToSync)
		{
			SyncFileData sourceFile = createSyncData(aFileToSynchronize);
			SyncFileData targetFile = createSyncData(aSynchronizeTargetFile);
			
			filesToAddToSync.put(sourceFile,targetFile);
		}
	}
	
	public void removeFilesFromSync(ObjectMetaDataIf aFileToSynchronize,ObjectMetaDataIf aSynchronizeTargetFile)
	{
		SyncFileData sourceFile = createSyncData(aFileToSynchronize);
		SyncFileData targetFile = createSyncData(aSynchronizeTargetFile);
		synchronized(filesToDeleteFromSync)
		{
			filesToDeleteFromSync.put(sourceFile,targetFile);
		}
	}
	
	
	public void startCyclicSynch()
	{
		BackgroundSync backgroundSync = new BackgroundSync(supportersBundle,filesToAddToSync,filesToDeleteFromSync);
		syncThread = new Thread(backgroundSync);
		syncThread.start();
		isSyncOn = true;
	}
	
	public void stopCyclicSynch()
	{
		if(isSyncOn)
		{
			syncThread.interrupt();
			isSyncOn = false;
		}
	}
	
	public void updateFileKey(String aOldKey,String aNewKey) throws SQLException
	{
		DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
		dbSupervisor.updateFileKey(aOldKey, aNewKey);
	}
	
	public Map<SyncFileData, SyncFileData> getSyncInfo() throws SQLException
	{
		BackgroundSync syncDatabase = new BackgroundSync(supportersBundle,filesToAddToSync,filesToDeleteFromSync);
		return syncDatabase.syncDatabase();
		
	}
	public boolean isSyncOn()
	{
		return isSyncOn;
	}
	
	private SyncFileData createSyncData(ObjectMetaDataIf obj) {
		SyncFileData file;
		if(obj.getFileServer() == FileServer.Amazon) file = new S3SyncFileData(obj
				,supportersBundle.getAmazonS3Supporter().getAccountName());
		else if(obj.getFileServer() == FileServer.Google)
		{
		file = new SyncFileData(obj,supportersBundle.getGoogleDriveSupporter().getAccountName());
		}else file = new SyncFileData(obj,null);
		return file;
	}
}
