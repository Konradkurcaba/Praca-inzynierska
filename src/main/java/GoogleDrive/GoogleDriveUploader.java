package GoogleDrive;

import java.io.IOException;
import java.util.Collections;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class GoogleDriveUploader {

	public void uploadFile(java.io.File aFileToUpload, Drive aService,String aDestFolderId) throws IOException
	{
		File fileMetadata = new File()
				.setName(aFileToUpload.getName())
				.setParents(Collections.singletonList(aDestFolderId));
		String fileExtension[] = aFileToUpload.getName().split("\\.");
		FileContent mediaContent = 	new FileContent(null, aFileToUpload);
		aService.files().create(fileMetadata,mediaContent).setFields("id").execute();
	}
	
}
