package ch.mav.schedario.tasks;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.service.FileProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileTaskTest {

    @InjectMocks
    private FileTask sut;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileProcessor fileProcessor;

    @Test
    void start_process() {
        final File file = File.builder()
                .id(1L)
                .status(Status.NEW)
                .createdDate(OffsetDateTime.MAX)
                .checksum(3106909919L)
                .path("bingo")
                .size(254353L)
                .created(OffsetDateTime.MIN)
                .build();
        doReturn(List.of(file)).when(fileRepository).findAllByStatus(any());

        // act
        sut.process();

        // assert
        verify(fileRepository).findAllByStatus(Status.NEW);
        verify(fileProcessor).updateFile(file);
    }
}