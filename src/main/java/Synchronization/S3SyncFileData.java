package Synchronization;

import com.amazonaws.services.s3.model.ObjectMetadata;

import AmazonS3.AmazonS3ObjectMetadata;
import AmazonS3.AmazonS3SummaryMetadata;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;

public class S3SyncFileData extends SyncFileData {

	private String bucketName;
	
	public S3SyncFileData(ObjectMetaDataIf aFile,String aAccountName)
	{
		super();
		setFileId(aFile.getName());
		setSize(aFile.getSize());
		setLastModifyDate(aFile.getLastModifiedDate());
		setFileServer(FileServer.Amazon);
		setFileName(aFile.getName());
		setAccountName(aAccountName);
		
		
		if(aFile instanceof AmazonS3ObjectMetadata)
		{
			AmazonS3ObjectMetadata file = (AmazonS3ObjectMetadata) aFile;
			bucketName = file.getBucketName();
		}
		
		if(aFile instanceof AmazonS3SummaryMetadata)
		{
			AmazonS3SummaryMetadata file = (AmazonS3SummaryMetadata) aFile;
			bucketName = file.getBucketName();
		}
	}
	
	public S3SyncFileData(String aKey,String aName,String aSize,String aModifyDate,String aBucketName,String aAccount)
	{
		super(aKey,aName,aSize,aModifyDate,FileServer.Amazon,aAccount);
		bucketName = aBucketName;
	}
	
	public String getBucketName()
	{
		return bucketName;
	}
	
	public String getKey()
	{
		return getFileId();
	}
}
