package ch.mav.schedario.service;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Tag;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileService {

    List<File> getFiles();

    Optional<File> getFile(long id);

    Optional<Resource> getThumbnail(long id);

    File updateFile(File file);

    Set<Tag> getTagsForFile(long id);

    void changeTagsForFile(File file, List<Tag> tags);

    void addTagForFile(File file, Tag tag);

    void deleteTagFromFile(File file, long tagId);
}
