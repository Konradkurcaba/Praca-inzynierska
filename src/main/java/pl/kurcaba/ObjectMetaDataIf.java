package pl.kurcaba;

import java.util.Date;
import javafx.beans.property.StringProperty;

public interface ObjectMetadataIf<T> {

	String getName();
	String getSize();
	String getLastModifiedDate();
	String getOrginalId();
	T getOrginalObject();
	FileServer getFileServer(); 
	boolean isRoot();
	boolean isDirectory();
}