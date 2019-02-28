package GoogleDrive;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;

public class GoogleDriveNameChanger {

	public void changeName(Drive aDriveService,String aId,String aNewName) throws IOException
	{
		File newMetadata = new File();
		newMetadata.setName(aNewName);
		Files.Update request = aDriveService.files().update(aId,newMetadata);
		request.execute();
	}
	
	
}
