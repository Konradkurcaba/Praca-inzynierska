package pl.kurcaba;

import AmazonS3.AmazonAccountInfo;

public class ApplicationConfiguration {

	private String defaultGoogleAccount;
	private boolean isSyncOn;
	private AmazonAccountInfo amazonAccount;
	
	public ApplicationConfiguration(boolean aIsSyncOn,String aDefaultGoogleAccount,AmazonAccountInfo aAmazonAccountInf)
	{
		
		defaultGoogleAccount = aDefaultGoogleAccount;
		isSyncOn = aIsSyncOn;
		amazonAccount = aAmazonAccountInf;
	}
	
	public String getDefaultGoogleAccount() {
		return defaultGoogleAccount;
	}

	public boolean isSyncOn() {
		return isSyncOn;
	}
	public AmazonAccountInfo getAmazonAccount()
	{
		return amazonAccount;
	}
	
}
