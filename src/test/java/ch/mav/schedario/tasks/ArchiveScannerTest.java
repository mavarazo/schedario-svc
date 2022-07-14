package ch.mav.schedario.tasks;

import ch.mav.schedario.model.File;
import ch.mav.schedario.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveScannerTest {

  private ArchiveScanner sut;

  @Mock private FileRepository fileRepository;

  @BeforeEach
  void setUp() {
    final URL resource = ArchiveScannerTest.class.getResource("/test-archive");
    sut = new ArchiveScanner(resource.getPath(), fileRepository);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void do_not_discover_files_if_archive_path_is_missing(final String path) {
    // arrange
    sut = new ArchiveScanner(path, fileRepository);

    // act
    sut.discoverFiles();

    // assert
    verify(fileRepository, never()).saveAllAndFlush(any());
  }

  @Test
  void discover_files_from_archive() {
    // act
    sut.discoverFiles();

    // assert
    final ArgumentCaptor<List<File>> fileArgument = ArgumentCaptor.forClass(List.class);
    verify(fileRepository).saveAllAndFlush(fileArgument.capture());
    assertThat(fileArgument.getValue())
            .hasSize(1)
            .singleElement()
            .returns(3106909919L, File::getChecksum)
            .doesNotReturn(null, File::getPath)
            .returns(254353L, File::getSize)
            .doesNotReturn(null, File::getCreated);
  }

  @Test
  void ignore_file_by_checksum() {
    // arrange
    doReturn(true).when(fileRepository).existsByChecksum(anyLong());

    // act
    sut.discoverFiles();

    // assert
    verify(fileRepository, never()).saveAllAndFlush(any());
  }
}
