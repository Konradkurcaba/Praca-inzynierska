package GoogleDrive;

import java.io.IOException;
import java.util.Collections;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class GoogleDriveFolderCreator {

	public void createFolder(Drive aDriveService,String aFolderName, String aCurrentDirectoryId) throws IOException
	{
		File fileMetadata = new File();
		fileMetadata.setParents(Collections.singletonList(aCurrentDirectoryId));
		fileMetadata.setName(aFolderName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");	
		
		File file = aDriveService.files().create(fileMetadata)
			    .setFields("id")
			    .execute();
	}
	
	
}
