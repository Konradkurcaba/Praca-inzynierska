package Local;

import java.io.File;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.ObjectMetaDataIf;

public class LocalFileSupporter {

	public ObservableList<ObjectMetaDataIf> getFilesList(LocalFileMetadata file)
	{
		File directory;
		if(file.getName().equals("..."))
		{
			directory = file.getOrginalObject().getParentFile();
		}
		else
		{
			directory = file.getOrginalObject();
		}
		LocalFileExplorer filesExplorer = new LocalFileExplorer();
		List<File> files = filesExplorer.getFilesFromDirectory(directory);
		LocalFileConverter filesConverter = new LocalFileConverter();
		List<ObjectMetaDataIf> convertedFiles = filesConverter.convert(files);
		ObservableList<ObjectMetaDataIf> observableList = FXCollections.observableList(convertedFiles);
		file.setName("...");
		observableList.add(0,file);
		return FXCollections.observableList(convertedFiles);
	}
	
	public ObservableList<ObjectMetaDataIf> getRootsList()
	{
		LocalFileExplorer filesExplorer = new LocalFileExplorer();
		List<File> files = filesExplorer.getRoots();
		LocalFileConverter filesConverter = new LocalFileConverter();
		List<ObjectMetaDataIf> convertedFiles = filesConverter.convert(files);
		return FXCollections.observableList(convertedFiles);
	}
}
