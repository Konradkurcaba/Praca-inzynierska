package pl.kurcaba;

public class ApplicationConfiguration {

	private String defaultGoogleAccount;
	private boolean isSyncOn;
	
	public ApplicationConfiguration(String aDefaultGoogleAccount,boolean aIsSyncOn)
	{
		defaultGoogleAccount = aDefaultGoogleAccount;
		isSyncOn = aIsSyncOn;
	}
	
	public String getDefaultGoogleAccount() {
		return defaultGoogleAccount;
	}

	public boolean isSyncOn() {
		return isSyncOn;
	}
	
	
}
