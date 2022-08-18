package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Tag;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.repository.TagRepository;
import ch.mav.schedario.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final TagRepository tagRepository;

    @Override
    public List<File> getFiles() {
        return fileRepository.findAll(Sort.by(Sort.Direction.ASC, "path"));
    }

    @Override
    public Optional<File> getFile(final long id) {
        return fileRepository.findById(id);
    }

    @Override
    public Optional<Resource> getThumbnail(final long id) {
        return fileRepository.findById(id).filter(f -> Objects.nonNull(f.getThumbnail()) && f.getThumbnail().length > 0).map(f -> new ByteArrayResource(f.getThumbnail()));
    }

    @Override
    public File updateFile(final File file) {
        return fileRepository.save(file);
    }

    @Override
    public Set<Tag> getTagsForFile(final long id) {
        return fileRepository.findById(id)
                .map(File::getTags)
                .orElse(Collections.emptySet());
    }

    @Override
    public void addTagForFile(final File file, final Tag tag) {
        final Tag persistentTag = Optional.ofNullable(tag.getId())
                .map(tagRepository::findById)
                .orElse(tagRepository.findByTitle(tag.getTitle()))
                .orElse(tagRepository.save(Tag.builder().title(tag.getTitle()).build()));
        file.getTags().add(persistentTag);
        fileRepository.save(file);
    }

    @Override
    public void deleteTagFromFile(final File file, final long tagId) {
        file.getTags().removeIf(t -> t.getId() == tagId);
        fileRepository.save(file);
    }
}
