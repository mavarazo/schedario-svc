package ch.mav.schedario.schedario.mapper;

import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.schedario.model.File;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-01T21:13:04+0200",
    comments = "version: 1.5.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.4.1.jar, environment: Java 17.0.3 (Eclipse Adoptium)"
)
@Component
public class FileMapperImpl implements FileMapper {

    @Override
    public FileDto toFileDto(File file) {
        if ( file == null ) {
            return null;
        }

        FileDto fileDto = new FileDto();

        fileDto.setId( file.getId() );
        fileDto.setCreatedDate( file.getCreatedDate() );
        fileDto.setModifiedDate( file.getModifiedDate() );
        fileDto.setTitle( file.getTitle() );
        fileDto.setNotes( file.getNotes() );
        fileDto.setPath( file.getPath() );
        fileDto.setSize( (int) file.getSize() );
        fileDto.setCreated( file.getCreated() );

        return fileDto;
    }
}
