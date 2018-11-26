package GoogleDrive;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


public class GoogleDriveFileDownloader {
	
	public List<File> getFilesList(Drive aService,String fileId,boolean isBackToPreviousContainer) throws IOException
	{
		String query;
		if(isBackToPreviousContainer)
			{
			
			query = "'"+ fileId + "'=parents";
			
			Files.Get request = aService.files().get(query)
					.setFields("files(id,name,size,modifiedTime,parents,mimeType)");
			File files = request.execute();
			
			}
		else query = "'" + fileId +"' in parents";
		List<File> resultFileList = new ArrayList<File>();
		Files.List request = aService.files().list()
				.setQ(query)
				.setFields("files(id,name,size,modifiedTime,parents,mimeType)");
		do
		{
			try
			{
				FileList files = request.execute();
				resultFileList.addAll(files.getFiles());
			}catch(IOException aEx)
			{
				aEx.printStackTrace();
			}
		}while(request.getPageToken() != null && request.getPageToken().length() > 0);
		
		return resultFileList;
	}
	
	public java.io.File downloadFile(GoogleFileMetadata aFileMetadata, String aTargetPath,Drive aService) throws IOException
	{
		String fileId = aFileMetadata.getOrginalObject().getId();
		String fileName = aFileMetadata.getName();
		java.io.File downloadedFile = new java.io.File(aTargetPath + "\\" + fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(downloadedFile);
		aService.files().get(fileId).executeMediaAndDownloadTo(fileOutputStream);
		fileOutputStream.close();
		return downloadedFile;
	}
	
	
	
}
