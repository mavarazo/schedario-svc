package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import ch.mav.schedario.model.Tag;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.repository.TagRepository;
import ch.mav.schedario.service.FileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessorImpl implements FileProcessor {

    public static final int DPI = 300;
    public static final int FIRST_PAGE = 0;
    private final FileRepository fileRepository;
    private final TagRepository tagRepository;

    @Override
    public void updateFile(final File file) {
        file.setThumbnail(generateThumbnail(file));
        file.setTags(evaluateTags(file));
        file.setStatus(Status.COMPLETE);
        fileRepository.save(file);
    }

    private byte[] generateThumbnail(final File file) {
        final Path path = Paths.get(file.getPath());
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final PDDocument pdfDocument = getPdfDocument(path)) {
            final PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(FIRST_PAGE, DPI, ImageType.RGB);
            ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);
        } catch (final IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private Set<Tag> evaluateTags(final File file) {
        final Path path = Paths.get(file.getPath());
        try (final PDDocument pdfDocument = getPdfDocument(path)) {
            final String text = new PDFTextStripper().getText(pdfDocument);
            return tagRepository.findAll().stream()
                    .filter(tag -> text.contains(tag.getTitle()))
                    .collect(Collectors.toUnmodifiableSet());
        } catch (final IOException ex) {
            log.debug(ex.getMessage(), ex);
        }
        return Collections.emptySet();
    }

    private static PDDocument getPdfDocument(final Path path) {
        try {
            return PDDocument.load(path.toFile());
        } catch (final IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
        return null;
    }
}
