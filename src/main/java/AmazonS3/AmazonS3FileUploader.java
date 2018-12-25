package AmazonS3;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

public class AmazonS3FileUploader {

	
	public ObjectMetadata uploadFile(File aFile,AmazonS3 aS3Client, String aBucketName, String aKey) {
		PutObjectResult result = aS3Client.putObject(aBucketName,aKey, aFile);
		return result.getMetadata();
	}

}
