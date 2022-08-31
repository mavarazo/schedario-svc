package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import ch.mav.schedario.model.Tag;
import ch.mav.schedario.repository.FileRepository;
import ch.mav.schedario.repository.TagRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileProcessorImplTest {

    @InjectMocks
    private FileProcessorImpl sut;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private TagRepository tagRepository;

    @Test
    @SneakyThrows
    void process() {
        // arrange
        final URL resource = FileProcessorImplTest.class.getResource("/test-archive/2020-Scrum-Guide-US.pdf");
        final File file = File.builder()
                .id(1L)
                .status(Status.NEW)
                .createdDate(OffsetDateTime.MAX)
                .checksum(3106909919L)
                .path(resource.getPath())
                .size(254353L)
                .created(OffsetDateTime.MIN)
                .build();

        final Tag scrum = Tag.builder()
                .title("Scrum")
                .build();
        doReturn(List.of(scrum, Tag.builder()
                .title("Haggis")
                .build())).when(tagRepository).findAll();

        // act
        sut.updateFile(file);

        // assert
        final ArgumentCaptor<File> fileArgument = ArgumentCaptor.forClass(File.class);
        verify(fileRepository).save(fileArgument.capture());
        assertThat(fileArgument.getValue())
                .returns(Status.COMPLETE, File::getStatus)
                .doesNotReturn(null, File::getThumbnail)
                .satisfies(f -> assertThat(f.getTags()).containsExactly(scrum));
    }
}