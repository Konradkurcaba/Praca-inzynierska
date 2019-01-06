package pl.kurcaba;

import java.sql.SQLException;
import java.util.List;

import Synchronization.DatabaseSupervisor;

public final class ApplicationConfig {

	public static String WORKING_DIRECTORY = "C:\\working";
	
	private final List<String> driveAccounts;
	private String defaultDriveAccount;
	private final List<String> s3Accounts;
	private String defaultS3Account;
	
	public ApplicationConfig() throws SQLException {
		DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
		driveAccounts = dbSupervisor.getDriveAccounts();
		s3Accounts = dbSupervisor.getS3Accounts();
		dbSupervisor.closeConnection();
	}
	
	public void changeDefaultDriveAccount(String aDriveAccount) throws SQLException
	{
		defaultDriveAccount = aDriveAccount;
		if(!driveAccounts.contains(aDriveAccount))
		{
			driveAccounts.add(aDriveAccount);
			DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
			dbSupervisor.putGoogleAccount(aDriveAccount);
			dbSupervisor.closeConnection();
		}else
		{
			DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
			dbSupervisor.updateDefaultDriveAccount(aDriveAccount);
			dbSupervisor.closeConnection();
		}
	}
	public void deleteDriveAccount(String aAccount) throws SQLException
	{
		driveAccounts.remove(aAccount);
		DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
		dbSupervisor.deleteGoogleAccount(aAccount);
		dbSupervisor.closeConnection();
	}
	public String getCurrentS3Account()
	{
		return defaultS3Account;
	}

	public List<String> getDriveAccounts() {
		return driveAccounts;
	}

	public List<String> getS3Accounts() {
		return s3Accounts;
	}
	
	public String getDefaultDriveAccount()
	{
		return defaultDriveAccount;
	}
	
}
