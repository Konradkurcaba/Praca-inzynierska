package Synchronization;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.api.services.drive.model.File;

import AmazonS3.AmazonS3SummaryMetadata;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetadataIf;

public class SyncFileData {

	private String fileName;
	private String fileId;
	private String size;
	private String lastModifyDate;
	private FileServer fileServer;
	private String accountName;
	
	protected SyncFileData()
	{
	}
	
	public SyncFileData(String aName,String aId,String aSize,String aModifyDate, FileServer aServer,String aAccountName)
	{
		fileName = aName;
		fileId = aId;
		size = aSize;
		lastModifyDate = aModifyDate;
		fileServer = aServer;
		accountName = aAccountName;
	}
	
	public SyncFileData(ObjectMetadataIf aFile,String aAccountName)
	{
		fileName = aFile.getName();
		fileId = aFile.getOrginalId();
		size = aFile.getSize();
		lastModifyDate = aFile.getLastModifiedDate();
		fileServer = aFile.getFileServer();
		accountName = aAccountName;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getLastSize() {
		return size;
	}

	public String getLastModifyDate() {
		return lastModifyDate;
	}

	public FileServer getFileServer() {
		return fileServer;
	}
	public String getAccountName()
	{
		return accountName;
	}
	protected void setAccountName(String aAccountName) {
		this.accountName = aAccountName;
	}
	protected void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	protected void setFileId(String fileId) {
		this.fileId = fileId;
	}
	protected void setSize(String size) {
		this.size = size;
	}
	protected void setLastModifyDate(String lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}
	protected void setFileServer(FileServer fileServer) {
		this.fileServer = fileServer;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		if(! (obj instanceof SyncFileData)) return false;
		
		SyncFileData syncFileData = (SyncFileData) obj;
		boolean idEquals = syncFileData.getFileId().equals(this.getFileId());
		boolean sizeEquals = syncFileData.getLastSize().equals(this.getLastSize());
		boolean modifyDateEquals = syncFileData.getLastModifyDate().equals(this.getLastModifyDate());
		if(idEquals && sizeEquals && modifyDateEquals ) return true;
		else return false;
	}
	
	
	
}
