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
import pl.kurcaba.ObjectMetadataIf;
import pl.kurcaba.ApplicationConfig;
import pl.kurcaba.HelpersBundle;

public class CopyService extends Service<ObjectMetadataIf> {

	private final HelpersBundle bundle;
	private final FileServer targetServer;
	private final ObjectMetadataIf objectToCopy;

	public CopyService(HelpersBundle aBundle, ObjectMetadataIf aObject, FileServer aTargetServer) {
		bundle = aBundle;
		targetServer = aTargetServer;
		objectToCopy = aObject;
	}

	@Override
	protected Task<ObjectMetadataIf> createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				SyncFileDownloader anyFileDownloader = new SyncFileDownloader();
				File downloadedfile = anyFileDownloader.downloadFile(objectToCopy,bundle);
				return moveFile(downloadedfile);
			}
		};
	}

	private ObjectMetadataIf moveFile(File aFileToCopy) throws IOException, GeneralSecurityException {
		if (targetServer.equals(FileServer.Komputer)) {
			return bundle.getLocalFileSupporter().moveFileToCurrentDirectory(aFileToCopy.toPath());
		}
		else if(targetServer.equals(FileServer.GoogleDrive))
		{
			return bundle.getGoogleDriveSupporter().uploadFile(aFileToCopy);
		}
		else if(targetServer.equals(FileServer.AmazonS3))
		{
			return bundle.getAmazonS3Supporter().uploadFileToCurrentDir(aFileToCopy);
		}else throw new IllegalArgumentException("Not supported file Server");
	}
}
