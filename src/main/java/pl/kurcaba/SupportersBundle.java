package pl.kurcaba;

import AmazonS3.AmazonS3Supporter;
import GoogleDrive.GoogleDriveSupporter;
import Local.LocalFileSupporter;

public class SupportersBundle {

	private final AmazonS3Supporter s3Supporter = new AmazonS3Supporter();
	private final GoogleDriveSupporter driveSupporter = new GoogleDriveSupporter();
	private final LocalFileSupporter localSupporter = new LocalFileSupporter();
	
	public AmazonS3Supporter getAmazonS3Supporter() {
		return s3Supporter;
	}
	
	public GoogleDriveSupporter getGoogleDriveSupporter()
	{
		return driveSupporter;
	}
	
	public LocalFileSupporter getLocalFileSupporter()
	{
		return localSupporter;
	}
}
