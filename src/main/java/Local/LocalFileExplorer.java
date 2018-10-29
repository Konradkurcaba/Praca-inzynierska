package Local;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileExplorer {

	
	public List<File> getFilesFromDirectory(File aDirectory)
	{
		List<File> fileList = new ArrayList();
		fileList = Arrays.stream(aDirectory.listFiles()).collect(Collectors.toList());
		return fileList;
	}
	
	public List<File> getRoots()
	{
		return Arrays.stream(File.listRoots()).collect(Collectors.toList());
	}
	
	
}
