package ch.mav.schedario.tasks;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.service.FileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileTask {

    private final FileRepository fileRepository;
    private final FileProcessor fileProcessor;

    @Scheduled(cron = "${schedario.database.cleaner.cron}")
    public void process() {
        log.info("Process files.");
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final List<File> newFiles = fileRepository.findAllByStatus(Status.NEW);
        newFiles.forEach(fileProcessor::updateFile);
        stopWatch.stop();
        log.info("Processed '{}' Files in '{}' sec.", newFiles.size(), stopWatch.getTotalTimeSeconds());
    }
}
