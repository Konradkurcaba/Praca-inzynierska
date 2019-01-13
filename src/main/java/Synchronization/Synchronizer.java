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
import pl.kurcaba.AccountsSupervisor;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.HelpersBundle;

public class Synchronizer {
	
	private Map<SyncFileData,SyncFileData> filesToAddToSync = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private Map<SyncFileData,SyncFileData> filesToDeleteFromSync = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private final HelpersBundle helpersBundle;
	private final AccountsSupervisor accountsSupervisor;
	private Thread syncThread;
	private boolean isSyncOn;
	
	public Synchronizer(HelpersBundle aSupportersBundle,AccountsSupervisor aAccountsSupervisor)  {
		accountsSupervisor = aAccountsSupervisor;
		helpersBundle = aSupportersBundle;
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
		BackgroundSync backgroundSync = new BackgroundSync(helpersBundle,filesToAddToSync,filesToDeleteFromSync
				,accountsSupervisor);
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
		BackgroundSync syncDatabase = new BackgroundSync(helpersBundle,filesToAddToSync,filesToDeleteFromSync
				,accountsSupervisor);
		return syncDatabase.syncDatabase();
		
	}
	public boolean isSyncOn()
	{
		return isSyncOn;
	}
	
	private SyncFileData createSyncData(ObjectMetaDataIf obj) {
		SyncFileData file;
		if(obj.getFileServer() == FileServer.Amazon) file = new S3SyncFileData(obj
				,helpersBundle.getAmazonS3Supporter().getAccountName());
		else if(obj.getFileServer() == FileServer.Google)
		{
		file = new SyncFileData(obj,helpersBundle.getGoogleDriveSupporter().getAccountName());
		}else file = new SyncFileData(obj,null);
		return file;
	}
}
