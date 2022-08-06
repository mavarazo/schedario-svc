package ch.mav.schedario.service.impl;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveScannerImplTest {

    private static final URL ARCHIVE_PATH = ArchiveScannerImplTest.class.getResource("/test-archive");

    @InjectMocks
    private ArchiveScannerImpl sut;

    @Mock
    private FileRepository fileRepository;

    @ParameterizedTest
    @NullAndEmptySource
    void do_nothing_when_archive_path_is_missing(final String path) {
        // act
        assertThatThrownBy(() -> sut.scan(path))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Archive is required.");

        // assert
        verify(fileRepository, never()).saveAll(any());
    }

    @Test
    @SneakyThrows
    void discover_files_from_archive() {
        // arrange

        // act
        sut.scan(ARCHIVE_PATH.getPath());

        // assert
        final ArgumentCaptor<List<File>> fileArgument = ArgumentCaptor.forClass(List.class);
        verify(fileRepository).saveAll(fileArgument.capture());
        assertThat(fileArgument.getValue())
                .hasSize(1)
                .singleElement()
                .returns(3106909919L, File::getChecksum)
                .doesNotReturn(null, File::getPath)
                .returns(254353L, File::getSize)
                .doesNotReturn(null, File::getCreated)
                .returns("2020-Scrum-Guide-US", File::getTitle);
    }

    @Test
    @SneakyThrows
    void update_file_by_checksum() {
        // arrange
        doReturn(Optional.of(File.builder()
                .id(1L)
                .createdDate(OffsetDateTime.MAX)
                .checksum(3106909919L)
                .path("bingo")
                .size(254353L)
                .created(OffsetDateTime.MIN)
                .title("Bongo")
                .build())).when(fileRepository).findByChecksum(anyLong());

        // act
        sut.scan(ARCHIVE_PATH.getPath());

        // assert
        final ArgumentCaptor<List<File>> fileArgument = ArgumentCaptor.forClass(List.class);
        verify(fileRepository).saveAll(fileArgument.capture());
        assertThat(fileArgument.getValue())
                .hasSize(1)
                .singleElement()
                .returns(1L, File::getId)
                .returns(OffsetDateTime.MAX, File::getCreatedDate)
                .returns(3106909919L, File::getChecksum)
                .doesNotReturn(null, File::getPath)
                .returns(254353L, File::getSize)
                .doesNotReturn(null, File::getCreated)
                .returns("Bongo", File::getTitle);
    }
}