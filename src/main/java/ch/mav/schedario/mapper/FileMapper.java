package ch.mav.schedario.mapper;

import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.model.File;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface FileMapper {

  FileDto toFileDto(File file);
}
