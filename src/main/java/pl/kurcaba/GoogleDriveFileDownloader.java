package pl.kurcaba;

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
	
	public List<File> getAllFilesList(Drive aService) throws IOException
	{
		List<File> resultFileList = new ArrayList<File>();
		Files.List request = aService.files().list().setFields("files(id,name,size,modifiedTime)");
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
	
	public void downloadFile(String aFileId, String aTargetPath,Drive aService) throws IOException
	{
		FileOutputStream fileOutputStream = new FileOutputStream(aTargetPath);
		aService.files().get(aFileId).executeMediaAndDownloadTo(fileOutputStream);
		fileOutputStream.close();
	}
	
	
	
}
