package pl.kurcaba;

import java.io.IOException;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class FileUploader {

	
	
	
	public void uploadFile(java.io.File aFileToUpload, Drive aService) throws IOException
	{
		File fileMetadata = new File().setName(aFileToUpload.getName());
		String fileExtension[] = aFileToUpload.getName().split("\\.");
		FileContent mediaContent = 	new FileContent(fileExtension[fileExtension.length-1], aFileToUpload);
		aService.files().create(fileMetadata,mediaContent).setFields("id").execute();
		
	}
	
	
	
}
