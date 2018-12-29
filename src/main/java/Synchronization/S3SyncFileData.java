package Synchronization;

import com.amazonaws.services.s3.model.ObjectMetadata;

import AmazonS3.AmazonS3ObjectMetadata;
import AmazonS3.AmazonS3SummaryMetadata;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;

public class S3SyncFileData extends SyncFileData {

	private String bucketName;
	private String region;
	
	public S3SyncFileData(ObjectMetaDataIf aFile)
	{
		super();
		setFileId(aFile.getName());
		setSize(aFile.getSize());
		setLastModifyDate(aFile.getLastModifiedDate());
		setFileServer(FileServer.Amazon);
		
		if(aFile instanceof AmazonS3ObjectMetadata)
		{
			AmazonS3ObjectMetadata file = (AmazonS3ObjectMetadata) aFile;
			bucketName = file.getBucketName();
			region = file.getRegion();
		}
		
		if(aFile instanceof AmazonS3SummaryMetadata)
		{
			AmazonS3SummaryMetadata file = (AmazonS3SummaryMetadata) aFile;
			bucketName = file.getBucketName();
			region = file.getRegion();
		}
	}
	
	public S3SyncFileData(String aKey,String aSize,String aModifyDate,String aBucketName,String aRegion)
	{
		super(aKey,aSize,aModifyDate,"Amazon");
		bucketName = aBucketName;
		region = aRegion;
	}
	
	public String getBucketName()
	{
		return bucketName;
	}
	
	public String getKey()
	{
		return getFileId();
	}
	
	public String getRegion()
	{
		return region;
	}
	
}
