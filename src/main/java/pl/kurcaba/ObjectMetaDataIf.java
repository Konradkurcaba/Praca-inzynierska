package pl.kurcaba;

import java.util.Date;
import javafx.beans.property.StringProperty;

public interface ObjectMetaDataIf<T> {

	String getName();
	String getSize();
	String getLastModifiedDate();
	String getOrginalId();
	T getOrginalObject();
	FileServer getFileServer(); 
	boolean isRoot();
	
}