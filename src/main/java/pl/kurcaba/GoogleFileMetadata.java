package pl.kurcaba;

import java.util.Date;

import com.google.api.services.drive.model.File;

public class GoogleFileMetadata implements FileMetaDataIf<File> {

	public GoogleFileMetadata(File aOriginalFile)
	{
		orginalFile = aOriginalFile;
		name = orginalFile.getName();
		if(orginalFile.getSize() != null)
		{
			size = String.valueOf(orginalFile.getSize() / 1000) + " KB";
		}else
		{
			size = "0 KB";
		}
		lastModifiedDate = new Date(orginalFile.getModifiedTime().getValue());
	}
	
	
	private String name;
	private String size;
	private File orginalFile;
	private Date lastModifiedDate;
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSize() {
		return size;
	}

	@Override
	public File getOrginalFile() {
		return orginalFile;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public Date getLastModifiedDate()
	{
		return lastModifiedDate;
	}
	


}
