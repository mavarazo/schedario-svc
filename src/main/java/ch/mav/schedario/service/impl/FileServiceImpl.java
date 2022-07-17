package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;

  @Override
  public List<File> getFiles() {
    return new ArrayList<>(fileRepository.findAll());
  }
}
