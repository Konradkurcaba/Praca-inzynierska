package GoogleDrive;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;

public class GoogleDriveFileDeleting {

	
	public void deleteObject(Drive aDriveService,String idToDelete) throws IOException
	{
		Files.Delete request = aDriveService.files().delete(idToDelete);
		request.execute();
	}
	
}
