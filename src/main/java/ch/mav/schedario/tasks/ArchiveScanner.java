package ch.mav.schedario.tasks;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
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

    log.info("Process files in '{}'", archivePath);
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try (final Stream<Path> stream = Files.walk(Paths.get(archivePath), Integer.MAX_VALUE)) {
      final List<File> files = stream
              .filter(file -> !Files.isDirectory(file))
              .map(this::createOrUpdateFile)
              .toList();

      if (!files.isEmpty()) {
        fileRepository.saveAllAndFlush(files);
      }
      stopWatch.stop();
      log.info("Processed '{}' files in '{}' sec.", files.size(), stopWatch.getTotalTimeSeconds());
    } catch (final IOException ioex) {
      log.error("Unable to discover new files.", ioex);
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
                    .size(fileAttributes.size())
                    .build())
            .orElse(File.builder()
                    .checksum(checksum)
                    .path(file.toString())
                    .size(fileAttributes.size())
                    .created(fileTimeToDate(fileAttributes.creationTime()))
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
}
