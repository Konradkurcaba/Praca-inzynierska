package AmazonS3;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3ObjectMetadata implements ObjectMetaDataIf<S3ObjectSummary> {
	
	private final String name;
	private final String size;
	private final Date lastModifiedDate;
	private final S3ObjectSummary s3Object;
	private final boolean isDirectory;
	private final FileServer fileServer = FileServer.Amazon;
	
	public AmazonS3ObjectMetadata(S3ObjectSummary aS3Object) {
		
		s3Object = aS3Object;
		name = s3Object.getKey();
		size = String.valueOf(s3Object.getSize() / 1024) + " KB";
		lastModifiedDate = s3Object.getLastModified();
		if(s3Object.getSize() == 0) isDirectory = true;
		else isDirectory = false;
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
	public S3ObjectSummary getOrginalObject() {
		return s3Object;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
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
