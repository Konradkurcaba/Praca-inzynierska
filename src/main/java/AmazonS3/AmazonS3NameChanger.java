package AmazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

public class AmazonS3NameChanger {
	
	public void changeName(AmazonS3 aClient,AmazonS3ObjectMetadata aObjectMetadata,String aBucketName,String aNewName)
	{
		CopyObjectRequest copyRequest = new CopyObjectRequest(aBucketName
				, aObjectMetadata.getOrginalObject().getKey(), aBucketName, aNewName);
		
		aClient.copyObject(copyRequest);
		DeleteObjectRequest deleteRequest = new DeleteObjectRequest(aBucketName, aObjectMetadata.getOrginalObject().getKey());
		aClient.deleteObject(deleteRequest);
		
	}
}
