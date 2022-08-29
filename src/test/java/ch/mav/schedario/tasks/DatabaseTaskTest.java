package ch.mav.schedario.tasks;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseTaskTest {

    @InjectMocks
    private DatabaseTask sut;

    @Mock
    private FileRepository fileRepository;

    @Test
    void file_no_longer_exists() {
        // arrange
        final URL url = DatabaseTask.class.getResource("/test-archive/2020-Scrum-Guide-US.pdf");
        final File bingo = File.builder()
                .id(1L)
                .path(url.getPath())
                .build();
        final File bongo = File.builder()
                .id(2L)
                .path("bongo")
                .build();
        doReturn(List.of(bingo, bongo)).when(fileRepository).findAll();

        // act
        sut.clean();

        // assert
        verify(fileRepository).deleteById(2L);
        verifyNoMoreInteractions(fileRepository);
    }
}