package pl.kurcaba;

import java.util.Date;

public interface FileMetaDataIf<T> {

	String getName();
	String getSize();
	Date getLastModifiedDate();
	T getOrginalFile();
	
}