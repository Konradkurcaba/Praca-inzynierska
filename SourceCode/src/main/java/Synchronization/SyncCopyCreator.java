package Synchronization;

import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.HelpersBundle;

public class SyncCopyCreator {

	
	public void createSyncCopy(ObjectMetadataIf aSource,FileServer aTargetServer,HelpersBundle aSupporters)
	{
		switch(aTargetServer)
		{
		case AmazonS3:
			
			
			break;
		
		case GoogleDrive:
			break;
			
		case Komputer:
			break;
		
		}
	}
	
	
	
}
