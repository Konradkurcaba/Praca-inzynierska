package AmazonS3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AmazonS3FolderCreator {

	public void createFolder(AmazonS3 aS3Service,String aBucketName,String aFolderName)
	{
		ObjectMetadata folderMetadata = new ObjectMetadata();
		InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
		
		PutObjectRequest request = new PutObjectRequest(aBucketName, aFolderName, emptyStream,folderMetadata);
		
		aS3Service.putObject(request);
	}
	
}
