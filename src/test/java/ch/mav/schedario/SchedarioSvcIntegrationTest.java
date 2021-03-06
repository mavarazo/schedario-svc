package ch.mav.schedario;

import ch.mav.schedario.api.model.FileDto;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("/test-data/files.sql")
class SchedarioSvcIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Nested
  class GetFilesTest {

    @Test
    void status200() {
      // act
      final ResponseEntity<FileDto[]> response =
          restTemplate.exchange("/v1/files", HttpMethod.GET, HttpEntity.EMPTY, FileDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody, InstanceOfAssertFactories.ARRAY)
          .isNotNull()
          .hasSize(1);
    }
  }
}
