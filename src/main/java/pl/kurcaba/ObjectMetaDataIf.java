package pl.kurcaba;

import java.util.Date;

public interface ObjectMetaDataIf<T> {

	String getName();
	String getSize();
	Date getLastModifiedDate();
	T getOrginalObject();
	
}