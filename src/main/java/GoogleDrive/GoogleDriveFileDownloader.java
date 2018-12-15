package GoogleDrive;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveFileDownloader {

	public java.io.File downloadFile(GoogleFileMetadata aFileMetadata, String aTargetPath, Drive aService)
			throws IOException {
		String fileId = aFileMetadata.getOrginalObject().getId();
		String fileName = aFileMetadata.getName();
		java.io.File downloadedFile = new java.io.File(aTargetPath + "\\" + fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(downloadedFile);
		aService.files().get(fileId).executeMediaAndDownloadTo(fileOutputStream);
		fileOutputStream.close();
		return downloadedFile;
	}

	public File getFileMetadata(Drive aService,String aFileId) throws IOException
	{
		Files.Get request = aService.files().get(aFileId);
		return request.execute();
	}
	
	public String getFilesParentId(Drive aService, String aFileId) throws IOException {

		Files.Get request = aService.files().get(aFileId).setFields("parents");
		File files = request.execute();
		files.getParents();
		Optional<String> fileParentId = files.getParents().stream().findFirst();
		if (fileParentId.isPresent())
			return fileParentId.get();
		else
			throw new IOException("Cannot find file parent Id");
	}

	public List<File> getFilesList(Drive aService, String aParentId) throws IOException {
		String query = "'" + aParentId + "' in parents";
		List<File> resultFileList = new ArrayList<File>();
		Files.List request = aService.files().list().setQ(query)
				.setFields("files(id,name,size,modifiedTime,parents,mimeType)");
		do {
			try {
				FileList files = request.execute();
				resultFileList.addAll(files.getFiles());
			} catch (IOException aEx) {
				aEx.printStackTrace();
			}
		} while (request.getPageToken() != null && request.getPageToken().length() > 0);
		return resultFileList;
	}
	
	public String getRootId(Drive aService) throws IOException
	{
		Files.Get request = aService.files().get("root");
		File file = request.execute();
		return file.getId();
	}

}
