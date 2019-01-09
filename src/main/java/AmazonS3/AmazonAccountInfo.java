package AmazonS3;

public class AmazonAccountInfo {

	private final String accountName;
	private final String accessKey;
	private final String secretKey;
	private final String lastRegion;
	
	public AmazonAccountInfo(String aAccountName, String aAccessKey, String aSecretKey, String aLastRegion) {
		super();
		this.accountName = aAccountName;
		this.accessKey = aAccessKey;
		this.secretKey = aSecretKey;
		this.lastRegion = aLastRegion;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}
	
	public String getLastRegion()
	{
		return lastRegion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessKey == null) ? 0 : accessKey.hashCode());
		result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
		result = prime * result + ((secretKey == null) ? 0 : secretKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmazonAccountInfo other = (AmazonAccountInfo) obj;
		if (accessKey == null) {
			if (other.accessKey != null)
				return false;
		} else if (!accessKey.equals(other.accessKey))
			return false;
		if (accountName == null) {
			if (other.accountName != null)
				return false;
		} else if (!accountName.equals(other.accountName))
			return false;
		if (secretKey == null) {
			if (other.secretKey != null)
				return false;
		} else if (!secretKey.equals(other.secretKey))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return accountName;
	}
	
	
	
}
