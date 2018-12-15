package Local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
	
	public ObservableList<ObjectMetaDataIf> getFilesFromCurrentDir()
	{
		return getFilesList(new LocalFileMetadata(currentDirectory));
	}
	
	public ObservableList<ObjectMetaDataIf> deleteFile(LocalFileMetadata aLocalFile) throws IOException
	{
		if(currentDirectory != null)
		{
			if(aLocalFile.getFileType().equals(FileType.normal))
			{
				if(aLocalFile.getOrginalObject().isDirectory())
				{
					Files.walk(aLocalFile.getOrginalObject().toPath())
						.filter(Files::isRegularFile)
						.map(Path::toFile)
						.forEach(File::delete);
				}
				Files.delete(aLocalFile.getOrginalObject().toPath());
			}
			return getFilesList(new LocalFileMetadata(currentDirectory));
		} else throw new IOException("Cannot delete root directory");
	}
	
	public void createFolder(String aFolderName)
	{
		new File(currentDirectory.getPath() + "\\" + aFolderName).mkdir();
	}
	
	public void changeName(LocalFileMetadata aFileMetadata,String aNewName)
	{
		File file = aFileMetadata.getOrginalObject();
		File newFile = new File(file.getParentFile().toString() + "\\" + aNewName);
		file.renameTo(newFile);
	}
	
	public ObjectMetaDataIf getLocalFile(String aPath)
	{
		File file = new File(aPath);
		return new LocalFileMetadata(file);
	}
	
}
