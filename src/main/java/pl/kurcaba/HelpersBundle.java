package pl.kurcaba;

import AmazonS3.AmazonS3Helper;
import GoogleDrive.GoogleDriveHelper;
import Local.LocalFileHelper;

public class HelpersBundle {

	private final AmazonS3Helper s3Supporter = new AmazonS3Helper();
	private final GoogleDriveHelper driveSupporter = new GoogleDriveHelper();
	private final LocalFileHelper localSupporter = new LocalFileHelper();
	
	public AmazonS3Helper getAmazonS3Supporter() {
		return s3Supporter;
	}
	
	public GoogleDriveHelper getGoogleDriveSupporter()
	{
		return driveSupporter;
	}
	
	public LocalFileHelper getLocalFileSupporter()
	{
		return localSupporter;
	}
}
