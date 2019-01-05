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
	
	public void changeDefaultDriveAccount(String aDriveAlias) throws SQLException
	{
		defaultDriveAccount = aDriveAlias;
		if(!driveAccounts.contains(aDriveAlias))
		{
			DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
			dbSupervisor.putGoogleAlias(aDriveAlias);
			dbSupervisor.closeConnection();
		}
	}
	public String getCurrentS3Account()
	{
		return defaultS3Account;
	}

	
}
