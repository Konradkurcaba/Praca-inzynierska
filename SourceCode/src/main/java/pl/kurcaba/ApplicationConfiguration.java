package pl.kurcaba;

import AmazonS3.AmazonAccountInfo;

public class ApplicationConfiguration {

	private String defaultGoogleAccount;
	private AmazonAccountInfo amazonAccount;
	
	public ApplicationConfiguration(String aDefaultGoogleAccount,AmazonAccountInfo aAmazonAccountInf)
	{
		
		defaultGoogleAccount = aDefaultGoogleAccount;
		amazonAccount = aAmazonAccountInf;
	}
	
	public String getDefaultGoogleAccount() {
		return defaultGoogleAccount;
	}

	public AmazonAccountInfo getAmazonAccount()
	{
		return amazonAccount;
	}
	
}
