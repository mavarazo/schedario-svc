package ch.mav.schedario.service;

import ch.mav.schedario.model.File;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Optional;

public interface FileService {

    List<File> getFiles();

    Optional<File> getFile(long id);

    Optional<Resource> getThumbnail(long id);

    File updateFile(File file);
}
