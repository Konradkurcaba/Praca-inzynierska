package AmazonS3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import Synchronization.S3SyncFileData;
import Synchronization.SyncFileData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.PreviousContainer;

public final class AmazonS3Supporter {

	
	private AmazonS3 s3Client;
	
	AmazonS3BucketMetadata currentBucket;
	String currentPrefix;
	
	public AmazonS3Supporter() {
		getClient();
	}
	
	public ObservableList<ObjectMetaDataIf> getBucketsMetadata()
	{
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		List<Bucket> buckets = s3Downloader.getAllBucketsList(s3Client);
		AmazonS3Converter s3Converter = new AmazonS3Converter();
		List<AmazonS3BucketMetadata> bucketsMetadata = s3Converter.convertBuckets(buckets);
		currentBucket = null;
		return FXCollections.observableArrayList(bucketsMetadata);
	}
	
	public ObservableList<ObjectMetaDataIf> listBucketFiles(AmazonS3BucketMetadata aBucket)
	{
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		ListObjectsV2Result listResult = s3Downloader.getFilesFromBucket(s3Client, aBucket.getName(),"");
		
		AmazonS3Converter s3Converter = new AmazonS3Converter();
		List<ObjectMetaDataIf> convertedList = s3Converter.convertFileList(listResult,"");
		PreviousContainer previousContainer = new PreviousContainer(FileServer.Amazon);
		convertedList.add(0,previousContainer);
		currentBucket = aBucket;
		currentPrefix = "";
		return FXCollections.observableArrayList(convertedList);
	}
	
	public ObservableList<ObjectMetaDataIf> listFiles(String aPrefix)
	{
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		ListObjectsV2Result listResult = s3Downloader.getFilesFromBucket(s3Client,currentBucket.getName()
				, aPrefix);
		AmazonS3Converter s3Converter = new AmazonS3Converter();
		List<ObjectMetaDataIf> convertedList = s3Converter.convertFileList(listResult, aPrefix);
		convertedList.add(0,new PreviousContainer(FileServer.Amazon));
		currentPrefix = aPrefix;
		return FXCollections.observableArrayList(convertedList);
	}
	
	public ObservableList getFilesFromPreviousContainer()
	{
		if(currentPrefix.equals("")) return getBucketsMetadata();
		else
		{
			currentPrefix = prepareBackPrefix(currentPrefix);
			return listFiles(currentPrefix);
		}
	}
	
	public File getAmazonS3Object(AmazonS3SummaryMetadata aObjectMetadata, File aTargetDirectory) throws IOException
	{
		S3ObjectSummary orginalSummary = aObjectMetadata.getOrginalObject();
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		File file = s3Downloader.DownloadObject(s3Client, orginalSummary.getBucketName(), orginalSummary.getKey(),aTargetDirectory);
		return file;
	}
	
	public File getAmazonS3Object(String aKey, String aBucketName, File aTargetDirectory) throws IOException
	{
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		File file = s3Downloader.DownloadObject(s3Client, aBucketName, aKey, aTargetDirectory);
		return file;
	}
	
	public S3SyncFileData getAmazons3ObjMetadata(String aKey,String aBucketName)
	{
		AmazonS3FileDownloader s3Downloader = new AmazonS3FileDownloader();
		S3SyncFileData downloadedMetadata = new S3SyncFileData(s3Downloader.getFileMetadata(s3Client, aBucketName, aKey),aBucketName);
		return downloadedMetadata;
	}
	
	public AmazonS3ObjectMetadata uploadFileToCurrentDir(File aFileToUpload)
	{
		AmazonS3FileUploader amazonUploader = new AmazonS3FileUploader();
		ObjectMetadata uploadedFile = amazonUploader.uploadFile(aFileToUpload, s3Client, currentBucket.getName(), currentPrefix + aFileToUpload.getName());
		return new AmazonS3ObjectMetadata(uploadedFile);
	}
	public void uploadFile(String aKey,String aBucketName,File aFile)
	{
		AmazonS3FileUploader amazonUploader = new AmazonS3FileUploader();
		amazonUploader.uploadFile(aFile, s3Client, aBucketName, aKey);
	}
	
	public void deleteObject(AmazonS3SummaryMetadata aObjectMetadata)
	{
		AmazonS3FileDeleting amazonFileDeleting = new AmazonS3FileDeleting();
		
		if (aObjectMetadata.isDirectory())
		{
			amazonFileDeleting.deleteObjectsByPrefix(s3Client, currentBucket.getName(), aObjectMetadata.getName());
		}
		else
		{
			amazonFileDeleting.deleteObject(s3Client, currentBucket.getName(), aObjectMetadata.getOrginalObject().getKey());
		}
	}
	
	public ObservableList<ObjectMetaDataIf> getFilesFromCurrentDir()
	{
		if(currentBucket != null) return listFiles(currentPrefix);
		else
		{
			return getBucketsMetadata();
		}
	}
	
	public void createFolder(String aFolderName)
	{
		AmazonS3FolderCreator folderCreator = new AmazonS3FolderCreator();
		folderCreator.createFolder(s3Client, currentBucket.getName(),currentPrefix + aFolderName + "/");
	}
	
	public void changeName(AmazonS3SummaryMetadata aMetadata,String newName)
	{
		AmazonS3NameChanger nameChanger = new AmazonS3NameChanger();
		nameChanger.changeName(s3Client, aMetadata, currentBucket.getName(), currentPrefix, newName);
	}
	
	private String prepareBackPrefix(String aCurrentPrefix)
	{
		
		if(aCurrentPrefix.equals("")) return "";
		
		aCurrentPrefix = aCurrentPrefix.substring(0,aCurrentPrefix.lastIndexOf('/'))
				.substring(0,aCurrentPrefix.lastIndexOf('/'));
		
		aCurrentPrefix = aCurrentPrefix.substring(0,aCurrentPrefix.lastIndexOf('/')+1);
		boolean previousIsRoot = !aCurrentPrefix.contains("/");
		if(previousIsRoot) aCurrentPrefix = "";
		
		return aCurrentPrefix;
	}
	
	private void getClient()
	{
		AmazonS3LogInSupporter loginSupporter = new AmazonS3LogInSupporter();
		s3Client = loginSupporter.getAmazonS3Client();
	}
}
