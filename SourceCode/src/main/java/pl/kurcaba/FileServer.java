package pl.kurcaba;

public enum FileServer {
	GoogleDrive("GoogleDrive"),AmazonS3("AmazonS3"),Komputer("Komputer");
	
	private final String type;
	
	FileServer(String aServer)
	{
		type = aServer;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
