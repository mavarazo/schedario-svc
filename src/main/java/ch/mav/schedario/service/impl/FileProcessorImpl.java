package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.service.FileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessorImpl implements FileProcessor {

    public static final int DPI = 300;
    public static final int FIRST_PAGE = 0;
    private final FileRepository fileRepository;

    @Override
    public void updateFile(final File file) {
        file.setThumbnail(generateThumbnail(file));
        file.setStatus(Status.COMPLETE);
        fileRepository.save(file);
    }

    private byte[] generateThumbnail(final File file) {
        final Path path = Paths.get(file.getPath());
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final PDDocument pdfDocument = PDDocument.load(path.toFile());
            final PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(FIRST_PAGE, DPI, ImageType.RGB);
            pdfDocument.close();
            ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);
        } catch (final IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
