package pl.kurcaba;

import java.util.Date;
import javafx.beans.property.StringProperty;

public interface ObjectMetaDataIf<T> {

	String getName();
	String getSize();
	String getLastModifiedDate();
	T getOrginalObject();
	FileServer getFileServer(); 
	
}