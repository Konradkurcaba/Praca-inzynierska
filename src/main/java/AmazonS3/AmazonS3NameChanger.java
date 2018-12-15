package AmazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AmazonS3NameChanger {

	public void changeName(AmazonS3 aClient, AmazonS3ObjectMetadata aObjectMetadata, String aBucketName,
			String aPrefix,String aNewName) {
		if (aObjectMetadata.isDirectory()) {
			AmazonS3FileDownloader amazonDownloader = new AmazonS3FileDownloader();
			ListObjectsV2Result result = amazonDownloader.getFilesFromBucket(aClient, aBucketName,
					aObjectMetadata.getName());
			
			String newFolderName = aPrefix + aNewName + "/";
			String oldFolderName = aObjectMetadata.getOrginalObject().getKey();
			
			for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
				String newFullName = objectSummary.getKey().replaceFirst(oldFolderName, newFolderName);
				changeObjectName(aClient, aBucketName, objectSummary.getKey(), newFullName );
			}
		}else
		{
			changeObjectName(aClient, aBucketName, aObjectMetadata.getName(),aPrefix + aNewName);
		}
	}

	private void changeObjectName(AmazonS3 aClient, String aBucketName, String aOldName, String aNewName)
	{
		CopyObjectRequest copyRequest = new CopyObjectRequest(aBucketName, aOldName, aBucketName, aNewName);
		aClient.copyObject(copyRequest);
		
		DeleteObjectRequest deleteRequest = new DeleteObjectRequest(aBucketName, aOldName);
		aClient.deleteObject(deleteRequest);
	}
}
