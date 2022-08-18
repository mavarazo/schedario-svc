package ch.mav.schedario.controller;

import ch.mav.schedario.api.V1Api;
import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.api.model.TagDto;
import ch.mav.schedario.mapper.FileMapper;
import ch.mav.schedario.mapper.TagMapper;
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
    private final TagMapper tagMapper;

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

    @Override
    public ResponseEntity<Void> changeFile(final Long fileId, final FileDto fileDto) {
        return fileService.getFile(fileId)
                .map(file -> {
                    fileMapper.updateFile(file, fileDto);
                    fileService.updateFile(file);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<TagDto>> getTagsForFile(final Long fileId) {
        return fileService.getFile(fileId)
                .map(f -> f.getTags().stream().map(tagMapper::toTagDto).toList())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> addTagForFile(final Long fileId, final TagDto tagDto) {
        return fileService.getFile(fileId)
                .map(f -> {
                    fileService.addTagForFile(f, tagMapper.toTag(tagDto));
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteTagFromFile(final Long fileId, final Long tagId) {
        return fileService.getFile(fileId)
                .map(f -> {
                    fileService.deleteTagFromFile(f, tagId);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
