package Threads;
import java.io.File;

import Local.LocalFileHelper;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetadataIf;

public class LocalFileExploreService extends Service<ObservableList<ObjectMetadataIf>>{

	LocalFileHelper localSupporter;
	
	public LocalFileExploreService(LocalFileHelper aLocalSupporter) {
		localSupporter = aLocalSupporter;
	}
	
	
	@Override
	protected Task<ObservableList<ObjectMetadataIf>> createTask() {
		return new Task<ObservableList<ObjectMetadataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetadataIf> call() throws Exception {
				return localSupporter.getRootsList();
			}
			
		};
	}

	
	
}
