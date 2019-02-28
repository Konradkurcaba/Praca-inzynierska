package GoogleDrive;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.api.services.drive.model.File;

import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetadataIf;

public class GoogleFileMetadata implements ObjectMetadataIf<File> {

	private final String name;
	private final String size;
	private final String id;
	private final File orginalFile;
	private final Date lastModifiedDate;
	private final GoogleFileType fileType;
	private final FileServer fileServer = FileServer.GoogleDrive;
	
	public GoogleFileMetadata(File aOriginalFile)
	{
		orginalFile = aOriginalFile;
		name = orginalFile.getName();
		id = aOriginalFile.getId();
		if(orginalFile.getSize() != null)
		{
			size = String.valueOf(orginalFile.getSize() / 1000) + " KB";
			fileType = GoogleFileType.File;
		}else
		{
			if(aOriginalFile.getMimeType().equals("application/vnd.google-apps.folder"))
			{
				fileType = GoogleFileType.Folder;
			}
			else fileType = GoogleFileType.Document;
			size = "0";
		}
		lastModifiedDate = new Date(orginalFile.getModifiedTime().getValue());
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSize() {
		if(fileType == GoogleFileType.Document) return "Dokument Google";
		else if(fileType == GoogleFileType.Folder) return "Folder";
		else return size;
	}

	@Override
	public File getOrginalObject() {
		return orginalFile;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public String getLastModifiedDate()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormat.format(lastModifiedDate);
	}
	
	public GoogleFileType getFileType()
	{
		return fileType;
	}

	@Override
	public FileServer getFileServer() {
		return fileServer;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public String getOrginalId() {
		return id;
	}


	@Override
	public boolean isDirectory() {
		if(fileType == GoogleFileType.Folder) return true;
		else return false;
	}
	



}
