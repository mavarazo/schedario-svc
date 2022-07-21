package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.service.ArchiveScanner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveScannerImpl implements ArchiveScanner {

    private final FileRepository fileRepository;

    @Override
    public void scan(final String archivePath) {
        if (!StringUtils.hasText(archivePath)) {
            throw new NullPointerException("Archive is required.");
        }

        try (final Stream<Path> stream = Files.walk(Paths.get(archivePath), Integer.MAX_VALUE)) {
            final List<File> files = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(this::createOrUpdateFile)
                    .toList();

            if (!files.isEmpty()) {
                fileRepository.saveAll(files);
            }
        } catch (
                final IOException ioex) {
            log.error(format("Error scanning Path '%s'.", archivePath), ioex);
        }
    }

    @SneakyThrows
    private File createOrUpdateFile(final Path file) {
        log.debug("Processing '{}'", file.toString());
        final long checksum = generateChecksum(file);

        final BasicFileAttributes fileAttributes =
                Files.getFileAttributeView(file, BasicFileAttributeView.class).readAttributes();

        return fileRepository.findByChecksum(checksum)
                .map(f -> f.toBuilder()
                        .path(file.toString())
                        .build())
                .orElse(File.builder()
                        .checksum(checksum)
                        .path(file.toString())
                        .size(fileAttributes.size())
                        .created(fileTimeToDate(fileAttributes.creationTime()))
                        .title(getFileName(file))
                        .build());
    }

    private long generateChecksum(final Path file) {
        final int SIZE = 16 * 1024;
        try (final FileInputStream in = new FileInputStream(file.toFile())) {
            final FileChannel channel = in.getChannel();
            final CRC32 crc = new CRC32();
            final int length = (int) channel.size();
            final MappedByteBuffer mb = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);
            final byte[] bytes = new byte[SIZE];
            int remaining;
            while (mb.hasRemaining()) {
                remaining = Math.min(mb.remaining(), SIZE);
                mb.get(bytes, 0, remaining);
                crc.update(bytes, 0, remaining);
            }
            final long checksum = crc.getValue();
            log.debug("Calculated checksum '{}' of '{}'", checksum, file.getFileName());
            return checksum;
        } catch (final IOException e) {
            log.warn(e.getMessage(), e);
        }
        return 0;
    }

    private OffsetDateTime fileTimeToDate(final FileTime fileTime) {
        final OffsetDateTime result = fileTime.toInstant().atOffset(ZoneOffset.UTC);
        log.debug("Converted filetime '{}' to '{}'", fileTime, result);
        return result;
    }

    private String getFileName(final Path file) {
        final String extPattern = "(?<!^)[.][^.]*$";
        return file.getFileName().toString().replaceAll(extPattern, "");
    }
}
