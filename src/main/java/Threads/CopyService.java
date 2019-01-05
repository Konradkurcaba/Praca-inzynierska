package Threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import com.google.common.io.Files;

import AmazonS3.AmazonS3FileDownloader;
import AmazonS3.AmazonS3SummaryMetadata;
import GoogleDrive.GoogleFileMetadata;
import Local.LocalFileMetadata;
import Synchronization.SyncFileDownloader;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.FileServer;
import pl.kurcaba.ObjectMetaDataIf;
import pl.kurcaba.ApplicationConfig;
import pl.kurcaba.SupportersBundle;

public class CopyService extends Service<ObjectMetaDataIf> {

	private final SupportersBundle bundle;
	private final FileServer targetServer;
	private final ObjectMetaDataIf objectToCopy;

	public CopyService(SupportersBundle aBundle, ObjectMetaDataIf aObject, FileServer aTargetServer) {
		bundle = aBundle;
		targetServer = aTargetServer;
		objectToCopy = aObject;
	}

	@Override
	protected Task<ObjectMetaDataIf> createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				SyncFileDownloader anyFileDownloader = new SyncFileDownloader();
				File downloadedfile = anyFileDownloader.downloadFile(objectToCopy,bundle);
				return moveFile(downloadedfile);
			}
		};
	}

	private ObjectMetaDataIf moveFile(File aFileToCopy) throws IOException, GeneralSecurityException {
		if (targetServer.equals(FileServer.Local)) {
			return bundle.getLocalFileSupporter().moveFileToCurrentDirectory(aFileToCopy.toPath());
		}
		else if(targetServer.equals(FileServer.Google))
		{
			return bundle.getGoogleDriveSupporter().uploadFile(aFileToCopy);
		}
		else if(targetServer.equals(FileServer.Amazon))
		{
			return bundle.getAmazonS3Supporter().uploadFileToCurrentDir(aFileToCopy);
		}else throw new IllegalArgumentException("Not supported file Server");
	}
}
