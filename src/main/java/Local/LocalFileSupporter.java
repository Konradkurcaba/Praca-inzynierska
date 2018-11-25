package Local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.kurcaba.ObjectMetaDataIf;

public class LocalFileSupporter {

	
	private File currentDirectory;
	
	
	public ObservableList<ObjectMetaDataIf> getFilesList(LocalFileMetadata file)
	{
		File directory;
		if(file.getFileType() == FileType.previousContainer)
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
		
		LocalFileMetadata previousContainer = new LocalFileMetadata(directory);
		previousContainer.setFileType(FileType.previousContainer);
		observableList.add(0,previousContainer);
		
		currentDirectory = directory;
		return FXCollections.observableList(convertedFiles);
	}
	
	public ObservableList<ObjectMetaDataIf> getRootsList()
	{
		LocalFileExplorer filesExplorer = new LocalFileExplorer();
		List<File> files = filesExplorer.getRoots();
		LocalFileConverter filesConverter = new LocalFileConverter();
		List<ObjectMetaDataIf> convertedFiles = filesConverter.convert(files);
		currentDirectory = null;
		return FXCollections.observableList(convertedFiles);
	}
	
	public void moveFileToCurrentDirectory(Path aPath) throws IOException
	{
		Files.move(aPath, new File(currentDirectory.getAbsolutePath() + "//" + aPath.getFileName()).toPath());
	}
	
	
}
