package AmazonS3;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3FileMetadata implements ObjectMetaDataIf<S3ObjectSummary> {

	
	
	private final String name;
	private final String size;
	private final Date lastModifiedDate;
	private final S3ObjectSummary s3Object;
	
	
	public AmazonS3FileMetadata(S3ObjectSummary aS3Object) {
		
		s3Object = aS3Object;
		name = s3Object.getKey();
		size = String.valueOf(s3Object.getSize() / 1000) + " KB";
		lastModifiedDate = s3Object.getLastModified();
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

}
