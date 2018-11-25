package AmazonS3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import pl.kurcaba.Settings;

public class AmazonS3FileDownloader {

	
	
	public List<Bucket> getAllBucketsList(AmazonS3 aS3Client)
	{
		List<Bucket> buckets = aS3Client.listBuckets();
		return buckets;
	}
	
	public ListObjectsV2Result getFilesFromBucket(AmazonS3 aS3Client,String bucketName,String aPrefix)
	{
		ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(aPrefix);
		ListObjectsV2Result listResult;
		
		listResult = aS3Client.listObjectsV2(listRequest);
		return listResult;
	}
	
	
	public File getObject(AmazonS3 aS3Client,String aBucketName,String aKey,File aTargetDirectory) throws IOException
	{
		S3Object fullObject;
		fullObject = aS3Client.getObject(new GetObjectRequest(aBucketName,aKey));
		File targetFile = new File(aTargetDirectory.getAbsolutePath() + "//" + aKey);
		OutputStream outputStream = new FileOutputStream(targetFile);
		IOUtils.copy(fullObject.getObjectContent(), outputStream);
		outputStream.close();
		return targetFile;
	}
	
	
}
