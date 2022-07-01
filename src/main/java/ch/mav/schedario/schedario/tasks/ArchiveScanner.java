package ch.mav.schedario.schedario.tasks;

import ch.mav.schedario.schedario.model.File;
import ch.mav.schedario.schedario.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveScanner {

  @Value("${schedario.archive.path}")
  private final String archivePath;

  private final FileRepository fileRepository;

  @Scheduled(cron = "${schedario.archive.cron}")
  public void discoverFiles() {
    if (!StringUtils.hasText(archivePath)) {
      log.warn("No archive specified");
      return;
    }

    log.info("Discover files in '{}'", archivePath);
    try (final Stream<Path> stream = Files.walk(Paths.get(archivePath), Integer.MAX_VALUE)) {
      final List<File> files =
          stream.filter(file -> !Files.isDirectory(file)).map(this::createFile).toList();
      fileRepository.saveAllAndFlush(files);
    } catch (final IOException ioex) {
      log.error("Unable to discover new files.", ioex);
    }
  }

  @SneakyThrows
  private File createFile(final Path file) {
    final BasicFileAttributes fileAttributes =
        Files.getFileAttributeView(file, BasicFileAttributeView.class).readAttributes();
    return File.builder()
        .checksum(generateChecksum(file))
        .path(file.toString())
        .size(fileAttributes.size())
        .created(fileTimeToDate(fileAttributes.creationTime()))
        .build();
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
      return crc.getValue();
    } catch (final IOException e) {
      log.warn(e.getMessage(), e);
    }
    return 0;
  }

  private OffsetDateTime fileTimeToDate(final FileTime fileTime) {
    return fileTime.toInstant().atOffset(ZoneOffset.UTC);
  }
}
