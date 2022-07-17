package ch.mav.schedario.tasks;

import ch.mav.schedario.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseTasks {

    private final FileRepository fileRepository;

    @Scheduled(cron = "${schedario.database.cleaner.cron}")
    public void clean() {
        fileRepository.findAll().stream()
                .filter(file -> Files.notExists(Paths.get(file.getPath())))
                .forEach(file -> {
                    log.info("Delete File '{}' with path '{}' from database.", file.getId(), file.getPath());
                    fileRepository.delete(file);
                });
    }
}
