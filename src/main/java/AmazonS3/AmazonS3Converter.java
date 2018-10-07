package AmazonS3;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.model.Bucket;

public class AmazonS3Converter {

	
	public List<AmazonS3BucketMetadata> convertBuckets(List<Bucket> aBucketsLists)
	{
		List<AmazonS3BucketMetadata> buckets = aBucketsLists.stream().map(bucket -> {
			return new AmazonS3BucketMetadata(bucket);
		}).collect(Collectors.toList());
		
		return buckets;
	}
	
	
	
	
}

