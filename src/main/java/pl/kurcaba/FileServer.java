package pl.kurcaba;

public enum FileServer {
	Google("Google"),Amazon("Amazon"),Local("Local");
	
	private final String type;
	
	FileServer(String aServer)
	{
		type = aServer;
	}
}
