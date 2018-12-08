package AmazonS3;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.s3.model.Bucket;

import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3BucketMetadata implements ObjectMetaDataIf<Bucket> {

	private final String name;
	private final String size;
	private final Date creationDate;
	private final Bucket bucket;
	private final FileServer fileServer = FileServer.Amazon;
	
	public AmazonS3BucketMetadata(Bucket aOrginalObject) {
		bucket = aOrginalObject;
		name = bucket.getName();
		creationDate = bucket.getCreationDate();
		size = "Amazon S3 bucket";
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSize() {
		return size;
	}

	@Override
	public String getLastModifiedDate() {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormat.format(creationDate);
		
	}

	@Override
	public Bucket getOrginalObject() {
		return bucket;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public FileServer getFileServer() {
		return fileServer;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	
}
