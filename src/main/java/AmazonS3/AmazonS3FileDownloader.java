package AmazonS3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.S3ClientCache;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.amazonaws.services.s3.model.HeadBucketRequest;

import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.ApplicationConfig;

public class AmazonS3FileDownloader {

	
	
	public List<Bucket> getAllBucketsList(AmazonS3 aS3Client)
	{
		List<Bucket> allBuckets = aS3Client.listBuckets();
		
		List<Bucket> bucketFromAccountRegion = allBuckets.stream()
		.filter(bucket ->{
		try {
			String accountRegion = aS3Client.getRegion().toString();
			String bucketRegion = aS3Client.getBucketLocation(bucket.getName());
			boolean isCurrentRegion = Regions.fromName(accountRegion) == Regions.fromName(bucketRegion);
			if(isCurrentRegion) return true;
			else return false;
			}catch(Exception aEx)
			{
				return false;	
			}
		})
		.collect(Collectors.toList());
		return bucketFromAccountRegion;
	}
	
	public ListObjectsV2Result getFilesFromBucket(AmazonS3 aS3Client,String bucketName,String aPrefix)
	{
		ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(aPrefix);
		ListObjectsV2Result listResult;
		
		listResult = aS3Client.listObjectsV2(listRequest);
		return listResult;
	}
	
	public ObjectMetadata getFileMetadata(AmazonS3 aS3Client,String aBucketName,String aKey)
	{
		return aS3Client.getObjectMetadata(aBucketName, aKey);
	}
	
	public File DownloadObject(AmazonS3 aS3Client,String aBucketName,String aKey,File aTargetDirectory) throws IOException
	{
		S3Object fullObject;
		fullObject = aS3Client.getObject(new GetObjectRequest(aBucketName,aKey));
		AmazonS3Converter S3Converter = new  AmazonS3Converter();
		String fileName = S3Converter.deletePrefix(aKey);
		File targetFile = new File(aTargetDirectory.getAbsolutePath() + "//" + fileName);
		OutputStream outputStream = new FileOutputStream(targetFile);
		IOUtils.copy(fullObject.getObjectContent(), outputStream);
		outputStream.close();
		return targetFile;
	}
	
}
