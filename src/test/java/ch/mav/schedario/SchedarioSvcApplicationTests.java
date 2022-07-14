package ch.mav.schedario;

import ch.mav.schedario.controller.FileController;
import ch.mav.schedario.tasks.ArchiveScanner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SchedarioSvcApplicationTests {

  @Autowired private FileController fileController;

  @Autowired private ArchiveScanner archiveScanner;

  @Test
  void contextLoads() {
    assertThat(fileController).isNotNull();
    assertThat(archiveScanner).isNotNull();
  }
}
