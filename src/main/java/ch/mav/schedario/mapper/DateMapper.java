package ch.mav.schedario.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface DateMapper {

    default OffsetDateTime toDate(final Date date) {
        return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    default Date toDate(final OffsetDateTime date) {
        return Date.from(date.toInstant());
    }
}
