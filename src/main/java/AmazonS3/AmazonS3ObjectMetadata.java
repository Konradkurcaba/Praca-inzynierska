package AmazonS3;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3ObjectMetadata implements ObjectMetaDataIf<ObjectMetadata> {

	
	private final String name;
	private final String size;
	private final Date lastModifiedDate;
	private final boolean isDirectory;
	private final FileServer fileServer = FileServer.Amazon;
	private final ObjectMetadata objectMetadata;
	
	
	public AmazonS3ObjectMetadata(ObjectMetadata aMetadata)
	{
		name = aMetadata.getSSEAwsKmsKeyId();
		size = String.valueOf(aMetadata.getContentLength());
		lastModifiedDate = aMetadata.getLastModified();
		objectMetadata = aMetadata;
		isDirectory = false;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSize() {
		
		if(isDirectory)
		{
			return "Directory";
		}else
		{
			return size;
		}
	}

	@Override
	public String getLastModifiedDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormat.format(lastModifiedDate);
	}

	@Override
	public ObjectMetadata getOrginalObject() {
		return objectMetadata;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean isDirectory()
	{
		return isDirectory;
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
		return name;
	}

}
