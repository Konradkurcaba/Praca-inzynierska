package AmazonS3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.ObjectMetaDataIf;

public class AmazonS3Converter {

	
	public List<AmazonS3BucketMetadata> convertBuckets(List<Bucket> aBucketsLists)
	{
		List<AmazonS3BucketMetadata> buckets = aBucketsLists.stream().map(bucket -> {
			return new AmazonS3BucketMetadata(bucket);
		}).collect(Collectors.toList());
		
		return buckets;
	}
	
	public List<ObjectMetaDataIf> convertFileList(ListObjectsV2Result aFilesList,String aPrefix,String aBucketName
			,String aRegion)
	{
		List<ObjectMetaDataIf> convertedList = new ArrayList();
		
		for(S3ObjectSummary objectSummary : aFilesList.getObjectSummaries())
		{
			
			String key = objectSummary.getKey();
			String nameWithoutPrefix = key.replace(aPrefix, "");
			boolean isNextLevelDirectory = nameWithoutPrefix.contains("/") 
					&& (nameWithoutPrefix.lastIndexOf('/') == nameWithoutPrefix.indexOf('/') 
					&& nameWithoutPrefix.lastIndexOf('/') == nameWithoutPrefix.length()-1);
			boolean isFileInThisDirectory = !nameWithoutPrefix.contains("/")
					&& nameWithoutPrefix.length() >0;
			
			if(isNextLevelDirectory || isFileInThisDirectory)
			{
				AmazonS3SummaryMetadata objectMetadata = new AmazonS3SummaryMetadata(objectSummary,aBucketName,aRegion);
				convertedList.add(objectMetadata);
			}

		}
		return convertedList;
	}
	
	public String deletePrefix(String aName)
	{
		String[] splittedName = aName.split("/");
		return splittedName[splittedName.length-1];
	}
	
	
	
	
}

