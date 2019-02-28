package pl.kurcaba;

import java.util.Date;

public class PreviousContainer implements ObjectMetadataIf<ObjectMetadataIf> {

	private final FileServer fileServer;
	
	public PreviousContainer(FileServer aFileServer) {
		fileServer = aFileServer;
	}
	
	@Override
	public String getName() {
		return "";
	}

	@Override
	public String getSize() {
		return "";
	}

	@Override
	public String getLastModifiedDate() {
		return "";
	}

	@Override
	public ObjectMetadataIf getOrginalObject() {
		return this;
	}
	
	@Override
	public String toString()
	{
		return "...";
	}
	@Override
	public FileServer getFileServer() {
		return fileServer;
	}
	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public String getOrginalId() {
		return "...";
	}

	@Override
	public boolean isDirectory() {
		return true;
	}
	
	

	
}
