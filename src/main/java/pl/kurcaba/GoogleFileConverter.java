package pl.kurcaba;

import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.drive.model.File;

public class GoogleFileConverter {

	
	public List<GoogleFileMetadata> convert(List<File> aFiles)
	{
		List<GoogleFileMetadata> files = aFiles.stream().map( f -> {
			GoogleFileMetadata fileMeta = new GoogleFileMetadata(f);
			return fileMeta;
		}).collect(Collectors.toList());
		
		return files;
	}
	
	
}
