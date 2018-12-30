package Local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import Synchronization.SyncFileData;
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
	
	public LocalFileMetadata moveFileToCurrentDirectory(Path aPath) throws IOException
	{
		File targetFile = new File(currentDirectory.getAbsolutePath() + "//" + aPath.getFileName());
		Files.move(aPath,targetFile.toPath() );
		return new LocalFileMetadata(targetFile);
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
	
	public void deleteSyncFile(SyncFileData aFileToDelete) throws IOException
	{
		File fileToDelete = new File(aFileToDelete.getFileId());
		if(fileToDelete.exists()) Files.delete(fileToDelete.toPath());
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
	
	public ObjectMetaDataIf getLocalWrappedFile(String aPath)
	{
		File file = new File(aPath);
		if(!file.exists()) return null;
		return new LocalFileMetadata(file);
	}
	
	public File getLocalFile(String aPath)
	{
		File file = new File(aPath);
		return file;
	}
	public void moveFile(File aFileToMove,String aDestPath) throws IOException
	{
		Files.move(aFileToMove.toPath(), Paths.get(aDestPath));
	}
}
