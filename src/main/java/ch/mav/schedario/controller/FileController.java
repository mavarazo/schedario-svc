package ch.mav.schedario.controller;

import ch.mav.schedario.api.V1Api;
import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.mapper.FileMapper;
import ch.mav.schedario.service.impl.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController implements V1Api {

  private final FileServiceImpl fileService;

  private final FileMapper fileMapper;

  @Override
  public ResponseEntity<List<FileDto>> getFiles() {
    return ResponseEntity.ok(fileService.getFiles().stream().map(fileMapper::toFileDto).toList());
  }
}
