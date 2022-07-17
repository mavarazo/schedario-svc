package ch.mav.schedario.tasks;

import ch.mav.schedario.service.ArchiveScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveTasks {

  @Value("${schedario.archive.path}")
  private final String archivePath;

  private final ArchiveScanner archiveScanner;

  @Scheduled(cron = "${schedario.archive.scanner.cron}")
  public void scanArchive() {
    if (!StringUtils.hasText(archivePath)) {
      log.warn("No archive specified.");
      return;
    }

    log.info("Process files in '{}'", archivePath);
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    archiveScanner.scan(archivePath);
    stopWatch.stop();
    log.info("Processed in '{}' sec.", stopWatch.getTotalTimeSeconds());
  }
}
