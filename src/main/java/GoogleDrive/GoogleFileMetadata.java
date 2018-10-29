package GoogleDrive;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.api.services.drive.model.File;

import pl.kurcaba.ObjectMetaDataIf;

public class GoogleFileMetadata implements ObjectMetaDataIf<File> {

	public GoogleFileMetadata(File aOriginalFile)
	{
		orginalFile = aOriginalFile;
		name = orginalFile.getName();
		if(orginalFile.getSize() != null)
		{
			size = String.valueOf(orginalFile.getSize() / 1000) + " KB";
		}else
		{
			size = "Google Drive File";
		}
		lastModifiedDate = new Date(orginalFile.getModifiedTime().getValue());
	}
	
	private final String name;
	private final String size;
	private final File orginalFile;
	private final Date lastModifiedDate;
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSize() {
		return size;
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
	


}
