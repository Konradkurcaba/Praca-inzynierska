package AmazonS3;

import java.util.Date;

import com.amazonaws.services.s3.model.Bucket;

import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3BucketMetadata implements ObjectMetaDataIf<Bucket> {

	public final String name;
	public final String size;
	public final Date creationDate;
	public final Bucket bucket;
	
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
	public Date getLastModifiedDate() {
		return creationDate;
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

	
}
