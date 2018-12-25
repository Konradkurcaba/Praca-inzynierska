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
	
	private Map<SyncFileData,SyncFileData> filesToAddToSync = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private Map<SyncFileData,SyncFileData> filesToDeleteFromSync = Collections.synchronizedMap(new HashMap<SyncFileData,SyncFileData>());
	private final SupportersBundle supportersBundle;
	private Thread syncThread;
	
	public Synchronizer(SupportersBundle aSupportersBundle)  {
		supportersBundle = aSupportersBundle;
	}
	
	public void addFilesToSynchronize(ObjectMetaDataIf aFileToSynchronize,ObjectMetaDataIf aSynchronizeTargetFile) throws SQLException
	{
		synchronized(filesToAddToSync)
		{
			filesToAddToSync.put(new SyncFileData(aFileToSynchronize),new SyncFileData(aSynchronizeTargetFile));
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
	}
	
	public void stopCyclicSynch()
	{
		syncThread.interrupt();
	}
}
