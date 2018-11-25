package Local;

import java.io.File;
import java.text.SimpleDateFormat;

import pl.kurcaba.ObjectMetaDataIf;

public class LocalFileMetadata implements ObjectMetaDataIf<File>  {

	private String name;
	private final String size;
	private final Long lastModifiedDate;
	private final File orginalObject;
	private boolean isRoot;
	private FileType fileType;
	
	
	public LocalFileMetadata(File aOrginalObject) {
		orginalObject = aOrginalObject;
		if(orginalObject.getName().length() != 0)
		{
			name = orginalObject.getName();
			isRoot = false;
		}else
		{
			name = orginalObject.toString(); 
			isRoot = true;
		}
		
		if(orginalObject.isDirectory())
		{
			size = "Directory";
		}
		else size = String.valueOf(orginalObject.length() / 1024) + " KB";
		lastModifiedDate = orginalObject.lastModified();
		fileType = fileType.normal;
	}
	
	@Override
	public String getName() {
		if(fileType == FileType.normal)
		{
			return name;
		}else
		{
			return "...";
		}
	}
	

	@Override
	public String getSize() {
		return size;
	}

	@Override
	public String getLastModifiedDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormat.format(lastModifiedDate);
	}

	@Override
	public File getOrginalObject() {
		return orginalObject;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean aRoot)
	{
		isRoot = aRoot;
	}

	public void setFileType(FileType aNewType)
	{
		fileType = aNewType;
	}
	public FileType getFileType()
	{
		return fileType;
	}

	@Override
	public String toString()
	{
		return getName();
		
	}
}
