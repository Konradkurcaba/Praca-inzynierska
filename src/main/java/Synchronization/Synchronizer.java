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
			SyncFileData sourceFile;
			SyncFileData targetFile;
			if(aFileToSynchronize.getFileServer() == FileServer.Amazon) sourceFile = new S3SyncFileData(aFileToSynchronize);
			else sourceFile = new SyncFileData(aFileToSynchronize);
			if(aSynchronizeTargetFile.getFileServer() == FileServer.Amazon) targetFile = new S3SyncFileData(aSynchronizeTargetFile);
			else targetFile = new SyncFileData(aFileToSynchronize);
			filesToAddToSync.put(sourceFile,targetFile);
		}
	}
	
	public void removeFilesFromSync(ObjectMetaDataIf aFileToSynchronize,ObjectMetaDataIf aSynchronizeTargetFile)
	{
		synchronized(filesToDeleteFromSync)
		{
			filesToDeleteFromSync.put(new SyncFileData(aFileToSynchronize),new SyncFileData(aSynchronizeTargetFile));
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
	
	public boolean isSyncOn()
	{
		return isSyncOn;
	}
}
