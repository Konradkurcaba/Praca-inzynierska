package Local;

import java.io.File;
import java.text.SimpleDateFormat;

import pl.kurcaba.ObjectMetaDataIf;

public class LocalFileMetadata implements ObjectMetaDataIf<File>  {

	private String name;
	private final String size;
	private final Long lastModifiedDate;
	private final File orginalObject;
	
	public LocalFileMetadata(File aOrginalObject) {
		orginalObject = aOrginalObject;
		if(orginalObject.getName().length() != 0)
		{
			name = orginalObject.getName();
		}else
		{
			name = orginalObject.toString(); 
		}
		
		if(orginalObject.isDirectory())
		{
			size = "Directory";
		}
		else size = String.valueOf(orginalObject.length() / 1024) + " KB";
		lastModifiedDate = orginalObject.lastModified();
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String aName) {
		name = aName;
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
	
	@Override
	public String toString()
	{
		return name;
		
	}
}
