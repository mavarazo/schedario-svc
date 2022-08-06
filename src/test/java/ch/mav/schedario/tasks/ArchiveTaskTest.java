package ch.mav.schedario.tasks;

import ch.mav.schedario.service.ArchiveScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArchiveTaskTest {

  private ArchiveTask sut;

  @Mock
  private ArchiveScanner archiveScanner;

  @BeforeEach
  void setUp() {
    final URL resource = ArchiveTaskTest.class.getResource("/test-archive");
    sut = new ArchiveTask(resource.getPath(), archiveScanner);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void do_nothing_if_archive_path_is_missing(final String path) {
    // arrange
    sut = new ArchiveTask(path, archiveScanner);

    // act
    sut.scanArchive();

    // assert
    verify(archiveScanner, never()).scan(any());
  }

  @Test
  void discover_files_from_archive() {
    // act
    sut.scanArchive();

    // assert
    verify(archiveScanner).scan(any());
  }
}
