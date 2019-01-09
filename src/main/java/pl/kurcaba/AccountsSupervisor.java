package pl.kurcaba;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.amazonaws.services.s3.AmazonS3;
import com.google.api.services.drive.Drive;

import AmazonS3.AmazonAccountInfo;
import AmazonS3.AmazonS3LogInSupporter;
import GoogleDrive.GoogleDriveLogInSupporter;


public class AccountsSupervisor {

	private SupportersBundle supportersBundle;
	private boolean isDriveLoggedIn = false;
	private String currentAccount;
	private boolean isS3LoggedIn = false;
	
	public AccountsSupervisor(SupportersBundle aSupportersBundle) {
		supportersBundle = aSupportersBundle;
	}
	
	public boolean changeDriveAccount(String aAccountAlias)
	{
		GoogleDriveLogInSupporter driveLogInSupporter = new GoogleDriveLogInSupporter();
		try
		{
			Drive driveService = driveLogInSupporter.getDriveService(aAccountAlias);
			supportersBundle.getGoogleDriveSupporter().changeAccount(driveService);
			isDriveLoggedIn = true;
			currentAccount = aAccountAlias;
			return true;
		}catch (IOException |GeneralSecurityException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean changeAmazonAccount(AmazonAccountInfo newAmazonAccountInfo) 
	{
		try
		{
			AmazonS3LogInSupporter s3LoginSupporter = new AmazonS3LogInSupporter();
			AmazonS3 amazonS3 = s3LoginSupporter.getAmazonS3Client("AKIAJSG7SVOKKHDEZUQQ","/7ytnfrrmInuuf742KEtCyUkI1/I4G1SwBrVm2N9");
			supportersBundle.getAmazonS3Supporter().ChangeAccount(amazonS3);
			isS3LoggedIn = true;
			return true;
		}catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public String getCurrentAccount()
	{
		return currentAccount;
	}
	public boolean isDriveLoggedIn()
	{
		return isDriveLoggedIn;
	}
	
	public boolean isS3LoggedIn()
	{
		return isS3LoggedIn;
	}

	
	
	
}
