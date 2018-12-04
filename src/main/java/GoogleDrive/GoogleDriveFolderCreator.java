package GoogleDrive;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class GoogleDriveFolderCreator {

	public void createFolder(Drive aDriveService,String aFolderName) throws IOException
	{
		File fileMetadata = new File();
		fileMetadata.setName(aFolderName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");	
		
		File file = aDriveService.files().create(fileMetadata)
			    .setFields("id")
			    .execute();
	}
	
	
}
