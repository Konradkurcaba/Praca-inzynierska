package GoogleDrive;

import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.drive.model.File;

import pl.kurcaba.ObjectMetaDataIf;
/**
 * 
 * @author Konrad
 * this class convert files downloaded from Google Drive to GoogleFileMetadata format
 */
public class GoogleFileConverter {

	public List<ObjectMetaDataIf> convert(List<File> aFiles)
	{
		List<ObjectMetaDataIf> files = aFiles.stream().map( f -> {
			GoogleFileMetadata fileMeta = new GoogleFileMetadata(f);
			return fileMeta;
		}).collect(Collectors.toList());
		
		return files;
	}
	
	
}
