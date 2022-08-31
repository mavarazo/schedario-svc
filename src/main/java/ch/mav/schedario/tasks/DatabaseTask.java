package ch.mav.schedario.tasks;

import ch.mav.schedario.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseTask {

    private final FileRepository fileRepository;

    @Scheduled(cron = "${schedario.database.cleaner.cron}")
    public void clean() {
        log.info("Clean database from files which no longer exists.");
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final AtomicInteger filesDeleted = new AtomicInteger();
        fileRepository.findAll().stream()
                .filter(file -> Files.notExists(Paths.get(file.getPath())))
                .forEach(file -> {
                    log.info("File '{}' does not exists '{}'.", file.getId(), file.getPath());
                    fileRepository.deleteById(file.getId());
                    filesDeleted.incrementAndGet();
                });
        stopWatch.stop();
        log.info("Cleaned '{}' Files in '{}' sec.", filesDeleted.get(), stopWatch.getTotalTimeSeconds());
    }
}
