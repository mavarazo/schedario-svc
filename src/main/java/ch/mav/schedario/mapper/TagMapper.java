package ch.mav.schedario.mapper;

import ch.mav.schedario.api.model.TagDto;
import ch.mav.schedario.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring", uses = DateMapper.class)
public interface TagMapper {

    TagDto toTagDto(Tag tag);

    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Tag toTag(TagDto tag);
}
