package AmazonS3;

import java.util.Date;

import pl.kurcaba.ObjectMetaDataIf;

public class PreviousContainer implements ObjectMetaDataIf<ObjectMetaDataIf> {

	
	final ObjectMetaDataIf previousContainer;
	
	public PreviousContainer(ObjectMetaDataIf aPreviousContainer) {
		previousContainer = aPreviousContainer;
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

	
}
