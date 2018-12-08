package pl.kurcaba;

import java.util.Date;

public class PreviousContainer implements ObjectMetaDataIf<ObjectMetaDataIf> {

	
	final ObjectMetaDataIf previousContainer;
	
	public PreviousContainer(ObjectMetaDataIf aPreviousContainer) {
		previousContainer = aPreviousContainer;
	}
	public PreviousContainer()
	{
		previousContainer = null;
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
	public ObjectMetaDataIf getOrginalObject() {
		return previousContainer;
	}
	
	@Override
	public String toString()
	{
		return "...";
	}
	@Override
	public FileServer getFileServer() {
		return previousContainer.getFileServer();
	}
	@Override
	public boolean isRoot() {
		return false;
	}
	
	

	
}
