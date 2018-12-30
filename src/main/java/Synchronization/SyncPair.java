package Synchronization;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class SyncPair {

	private SyncFileData sourceFile;
	private StringProperty sourceFileName;
	private SyncFileData targetFile;
	private StringProperty targetFileName;
	
	public SyncPair(SyncFileData aSourceFile,SyncFileData aTargetFile)
	{
		sourceFile = aSourceFile;
		sourceFileName = new SimpleStringProperty(sourceFile.getFileServer() + ":" + sourceFile.getFileId());
		targetFile = aTargetFile;
		targetFileName = new SimpleStringProperty(targetFile.getFileServer() + ":" + targetFile.getFileId());
	}
		
	
	public StringProperty sourceFileNameProperty()
	{
		return sourceFileName;
	}
	
	public StringProperty targetFileNameProperty()
	{
		return targetFileName;
	}
	
	public String getSourceFileName()
	{
		return sourceFileName.get();
	}
	
	public String targetFileName()
	{
		return targetFileName.get();
	}
	
	public SyncFileData getSourceFile() {
		return sourceFile;
	}

	public SyncFileData getTargetFile() {
		return targetFile;
	}
	
	
}
