package Local;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import pl.kurcaba.ObjectMetadataIf;

public class LocalFileConverter {

	public List<ObjectMetadataIf> convert(List<File> aFilesList)
	{
		return aFilesList.stream()
				.map(file -> {
					return new LocalFileMetadata(file);
				}).collect(Collectors.toList());
	}
}
