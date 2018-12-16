package Synchronization;

import com.amazonaws.services.s3.model.ObjectMetadata;

import pl.kurcaba.FileServer;

public class S3SyncFileData extends SyncFileData {

	private String bucketName;
	
	public S3SyncFileData(ObjectMetadata aFile)
	{
		setFileId(aFile.getSSEAwsKmsKeyId());
		setSize(String.valueOf(aFile.getContentLength()));
		setLastModifyDate(aFile.getLastModified().toString());
		setFileServer(FileServer.Amazon);
		bucketName = aFile.getB
	}
	
	public String getBucketName()
	{
		return bucketName;
	}
}
