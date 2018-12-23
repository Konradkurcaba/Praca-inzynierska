package Synchronization;

import com.amazonaws.services.s3.model.ObjectMetadata;

import pl.kurcaba.FileServer;

public class S3SyncFileData extends SyncFileData {

	private String bucketName;
	private String region;
	
	public S3SyncFileData(ObjectMetadata aFile,String aBucketName)
	{
		setFileId(aFile.getSSEAwsKmsKeyId());
		setSize(String.valueOf(aFile.getContentLength()));
		setLastModifyDate(aFile.getLastModified().toString());
		setFileServer(FileServer.Amazon);
		bucketName = aBucketName;
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
