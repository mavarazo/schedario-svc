package ch.mav.schedario.controller;

import ch.mav.schedario.api.V1Api;
import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.mapper.FileMapper;
import ch.mav.schedario.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController implements V1Api {

    private final FileService fileService;

    private final FileMapper fileMapper;

    @Override
    public ResponseEntity<List<FileDto>> getFiles() {
        return ResponseEntity.ok(fileService.getFiles().stream().map(fileMapper::toFileDto).toList());
    }

    @Override
    public ResponseEntity<FileDto> getFile(final Long fileId) {
        return fileService.getFile(fileId)
                .map(fileMapper::toFileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Resource> getThumbnail(final Long fileId) {
        return fileService.getThumbnail(fileId)
                .map(f -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(f))
                .orElse(ResponseEntity.notFound().build());
    }
}
