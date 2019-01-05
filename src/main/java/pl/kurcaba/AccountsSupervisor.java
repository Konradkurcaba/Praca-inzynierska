package pl.kurcaba;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.drive.Drive;

import GoogleDrive.GoogleDriveLogInSupporter;


public class AccountsSupervisor {

	private SupportersBundle supportersBundle;
	private boolean isDriveLoggedIn;
	
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
			return true;
		}catch (IOException |GeneralSecurityException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	
	
}
