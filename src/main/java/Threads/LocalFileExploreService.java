package Threads;
import java.io.File;

import Local.LocalFileSupporter;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.kurcaba.ObjectMetaDataIf;

public class LocalFileExploreService extends Service<ObservableList<ObjectMetaDataIf>>{

	LocalFileSupporter localSupporter;
	
	public LocalFileExploreService(LocalFileSupporter aLocalSupporter) {
		localSupporter = aLocalSupporter;
	}
	
	
	@Override
	protected Task<ObservableList<ObjectMetaDataIf>> createTask() {
		return new Task<ObservableList<ObjectMetaDataIf>>()
		{

			@Override
			protected ObservableList<ObjectMetaDataIf> call() throws Exception {
				return localSupporter.getRootsList();
			}
			
		};
	}

	
	
}
