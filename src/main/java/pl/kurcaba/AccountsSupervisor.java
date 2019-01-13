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
	private String currentDriveAccount;
	private boolean isS3LoggedIn = false;
	private AmazonAccountInfo currentAmazonAccount;
	
	public AccountsSupervisor(SupportersBundle aSupportersBundle) {
		supportersBundle = aSupportersBundle;
	}
	
	public boolean changeDriveAccount(String aAccountAlias)
	{
		GoogleDriveLogInSupporter driveLogInSupporter = new GoogleDriveLogInSupporter();
		try
		{
			Drive driveService = driveLogInSupporter.getDriveService(aAccountAlias);
			supportersBundle.getGoogleDriveSupporter().changeAccount(driveService,aAccountAlias);
			isDriveLoggedIn = true;
			currentDriveAccount = aAccountAlias;
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
			AmazonS3 amazonS3 = s3LoginSupporter.getAmazonS3Client(newAmazonAccountInfo);
			try {
				amazonS3.listBuckets();
				isS3LoggedIn = true;
			}catch(Exception ex) {
				isS3LoggedIn = false;
				return isS3LoggedIn;
			}
			supportersBundle.getAmazonS3Supporter().ChangeAccount(amazonS3,newAmazonAccountInfo.getAccountName());
			currentAmazonAccount = newAmazonAccountInfo;
			return isS3LoggedIn;
		}catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public String getCurrentDriveAccount()
	{
		return currentDriveAccount;
	}
	
	public AmazonAccountInfo getCurrentS3Account()
	{
		return currentAmazonAccount;
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
