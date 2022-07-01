package ch.mav.schedario.schedario.service.impl;

import ch.mav.schedario.schedario.model.File;
import ch.mav.schedario.schedario.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl {

  private final FileRepository fileRepository;

  public List<File> getFiles() {
    return new ArrayList<>(fileRepository.findAll());
  }
}
