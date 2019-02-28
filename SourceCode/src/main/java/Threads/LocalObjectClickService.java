package Threads;

import AmazonS3.AmazonS3BucketMetadata;
import Local.FileType;
import Local.LocalFileMetadata;
import Local.LocalFileHelper;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;

public class LocalObjectClickService extends Service<ObservableList<ObjectMetadataIf>>{

	
	private LocalFileHelper localSupporter;
	ObjectMetadataIf clickedObject;
	
	public LocalObjectClickService(LocalFileHelper aLocalSupporter,ObjectMetadataIf aClickedObject) {
		localSupporter = aLocalSupporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>()
		{
			@Override
			protected ObservableList call() {
				if(clickedObject instanceof LocalFileMetadata)
				{
					LocalFileMetadata localFile = (LocalFileMetadata) clickedObject;
					if(localFile.isRoot() && localFile.getFileType() == FileType.previousContainer)
					{
						return localSupporter.getRootsList();
					}else
					{
						return localSupporter.getFilesList(localFile);
					}
				}
				else throw new IllegalArgumentException("Passed argument cannot be recognized");
			}
		};
	}

}
