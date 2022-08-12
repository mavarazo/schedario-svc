package ch.mav.schedario.mapper;

import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.model.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface FileMapper {

  FileDto toFileDto(File file);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "modifiedDate", ignore = true)
  @Mapping(target = "checksum", ignore = true)
  @Mapping(target = "path", ignore = true)
  @Mapping(target = "size", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "thumbnail", ignore = true)
  void updateFile(@MappingTarget File file, FileDto fileDto);
}
