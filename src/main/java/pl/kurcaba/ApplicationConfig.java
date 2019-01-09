package pl.kurcaba;

import java.sql.SQLException;
import java.util.List;

import AmazonS3.AmazonAccountInfo;
import Synchronization.DatabaseSupervisor;

public final class ApplicationConfig {

	public static String WORKING_DIRECTORY = "C:\\working";
	
	private final List<String> driveAccounts;
	private String defaultDriveAccount;
	private final List<AmazonAccountInfo> s3Accounts;
	private AmazonAccountInfo defaultS3Account;
	
	public ApplicationConfig() throws SQLException {
		DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
		driveAccounts = dbSupervisor.getDriveAccounts();
		s3Accounts = dbSupervisor.getS3Accounts();
		ApplicationConfiguration config = dbSupervisor.getAppConfig();
		defaultDriveAccount = config.getDefaultGoogleAccount();
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
	
	public void changeDefaults3Account(AmazonAccountInfo newAmazonAccountInfo) {
		defaultS3Account = newAmazonAccountInfo;
		if(!s3Accounts.contains(newAmazonAccountInfo))
		{
			s3Accounts.add(newAmazonAccountInfo);
		}
	}
	
	public void deleteDriveAccount(String aAccount) throws SQLException
	{
		driveAccounts.remove(aAccount);
		DatabaseSupervisor dbSupervisor = new DatabaseSupervisor();
		dbSupervisor.deleteGoogleAccount(aAccount);
		dbSupervisor.closeConnection();
	}
	public AmazonAccountInfo getCurrentS3Account()
	{
		return defaultS3Account;
	}

	public List<String> getDriveAccounts() {
		return driveAccounts;
	}

	public List<AmazonAccountInfo> getS3Accounts() {
		return s3Accounts;
	}
	
	public String getDefaultDriveAccount()
	{
		return defaultDriveAccount;
	}
}
