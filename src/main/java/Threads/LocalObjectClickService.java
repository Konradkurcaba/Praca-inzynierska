package Threads;

import AmazonS3.AmazonS3BucketMetadata;
import Local.LocalFileMetadata;
import Local.LocalFileSupporter;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;

public class LocalObjectClickService extends Service<ObservableList<ObjectMetaDataIf>>{

	
	private LocalFileSupporter localSupporter;
	ObjectMetaDataIf clickedObject;
	
	public LocalObjectClickService(LocalFileSupporter aLocalSupporter,ObjectMetaDataIf aClickedObject) {
		localSupporter = aLocalSupporter;
		clickedObject = aClickedObject;
	}
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>()
		{
			@Override
			protected ObservableList call() {
				if(clickedObject instanceof LocalFileMetadata)
				{
					return localSupporter.getFilesList((LocalFileMetadata)clickedObject);
				}
				else throw new IllegalArgumentException("Passed argument cannot be recognized");
			}
		};
	}

}
