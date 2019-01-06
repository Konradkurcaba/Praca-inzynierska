package pl.kurcaba;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.drive.Drive;

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
