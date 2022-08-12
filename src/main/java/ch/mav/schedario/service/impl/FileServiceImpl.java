package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;

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
    return fileRepository.findById(id)
            .filter(f -> Objects.nonNull(f.getThumbnail()) && f.getThumbnail().length > 0)
            .map(f -> new ByteArrayResource(f.getThumbnail()));
  }

  @Override
  public File updateFile(final File file) {
    return fileRepository.save(file);
  }
}
