package AmazonS3;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;

public class AmazonS3FileUploader {

	
	
	public void uploadFile(File aFile,AmazonS3 aS3Client,String aBucketName)
	{
		aS3Client.putObject(aBucketName,aFile.getName(),aFile);
	}
	
	
	
	
}
