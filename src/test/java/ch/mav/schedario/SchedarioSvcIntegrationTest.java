package ch.mav.schedario;

import ch.mav.schedario.api.model.FileDto;
import ch.mav.schedario.api.model.TagDto;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("/test-data/files.sql")
@Sql("/test-data/tags.sql")
@Sql(value = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
              .hasSize(1)
              .singleElement(InstanceOfAssertFactories.type(FileDto.class))
              .returns(1L, FileDto::getId);
    }
  }

  @Nested
  class GetFileTest {

    @Test
    void status404() {
      // act
      final ResponseEntity<FileDto> response =
              restTemplate.exchange("/v1/files/99", HttpMethod.GET, HttpEntity.EMPTY, FileDto.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // act
      final ResponseEntity<FileDto> response =
              restTemplate.exchange("/v1/files/1", HttpMethod.GET, HttpEntity.EMPTY, FileDto.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNotNull();
    }
  }

  @Nested
  class GetThumbnailTest {

    @Test
    void status404() {
      // arrange
      final HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", "image/jpeg");

      // act
      final ResponseEntity<Resource> response =
              restTemplate.exchange("/v1/files/99/thumbnail", HttpMethod.GET, new HttpEntity<>(headers), Resource.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", "image/jpeg");

      // act
      final ResponseEntity<Resource> response =
              restTemplate.exchange("/v1/files/1/thumbnail", HttpMethod.GET, new HttpEntity<>(headers), Resource.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNotNull();
    }
  }

  @Nested
  class ChangeFileTest {

    @Test
    void status404() {
      // arrange
      final FileDto fileDto = new FileDto()
              .title("Bingo")
              .notes("Bongo");

      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/99", HttpMethod.PUT, new HttpEntity<>(fileDto), Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final FileDto fileDto = new FileDto()
              .title("Bingo")
              .notes("Bongo");

      // act
      final ResponseEntity<Resource> response =
              restTemplate.exchange("/v1/files/1", HttpMethod.PUT, new HttpEntity<>(fileDto), Resource.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNull();
      assertThat(restTemplate.exchange("/v1/files/1", HttpMethod.GET, new HttpEntity<>(fileDto), FileDto.class))
              .extracting(ResponseEntity::getBody)
              .returns("Bingo", FileDto::getTitle)
              .returns("Bongo", FileDto::getNotes);
    }
  }

  @Nested
  class GetTagsForFileTest {

    @Test
    void status404() {
      // act
      final ResponseEntity<TagDto[]> response =
              restTemplate.exchange("/v1/files/99/tags", HttpMethod.GET, HttpEntity.EMPTY, TagDto[].class);

      // assert
      assertThat(response)
              .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // act
      final ResponseEntity<TagDto[]> response =
              restTemplate.exchange("/v1/files/1/tags", HttpMethod.GET, HttpEntity.EMPTY, TagDto[].class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody, InstanceOfAssertFactories.ARRAY)
              .isNotNull()
              .hasSize(1)
              .singleElement(InstanceOfAssertFactories.type(TagDto.class))
              .returns(2L, TagDto::getId);
    }
  }

  @Nested
  class AddTagForFileTest {

    @Test
    void status404() {
      // arrange
      final TagDto tag = new TagDto()
              .id(4L)
              .title("Master");

      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/99/tags", HttpMethod.PUT, new HttpEntity<>(tag), Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    @DirtiesContext
    void status200_assign_existing_Tag_by_id() {
      // arrange
      final TagDto tag = new TagDto()
              .id(4L)
              .title("Scrum Master");

      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/1/tags", HttpMethod.PUT, new HttpEntity<>(tag), Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNull();
      assertThat(restTemplate.exchange("/v1/files/1/tags", HttpMethod.GET, HttpEntity.EMPTY, TagDto[].class))
              .extracting(ResponseEntity::getBody, InstanceOfAssertFactories.array(TagDto[].class))
              .filteredOn(t -> t.getTitle().equals("Scrum Master"))
              .hasSize(1);
    }

    @Test
    @DirtiesContext
    void status200_assign_existing_Tag_by_title() {
      // arrange
      final TagDto tag = new TagDto()
              .title("Scrum Master");

      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/1/tags", HttpMethod.PUT, new HttpEntity<>(tag), Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNull();
      assertThat(restTemplate.exchange("/v1/files/1/tags", HttpMethod.GET, HttpEntity.EMPTY, TagDto[].class))
              .extracting(ResponseEntity::getBody, InstanceOfAssertFactories.array(TagDto[].class))
              .filteredOn(t -> t.getTitle().equals("Scrum Master"))
              .hasSize(1);
    }

    @Test
    @DirtiesContext
    void status200_assign_new_Tag() {
      // arrange
      final TagDto tag = new TagDto()
              .title("Developer");

      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/1/tags", HttpMethod.PUT, new HttpEntity<>(tag), Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNull();
      assertThat(restTemplate.exchange("/v1/files/1/tags", HttpMethod.GET, HttpEntity.EMPTY, TagDto[].class))
              .extracting(ResponseEntity::getBody, InstanceOfAssertFactories.array(TagDto[].class))
              .filteredOn(t -> t.getTitle().equals("Developer"))
              .hasSize(1);
    }
  }

  @Nested
  class DeleteTagFromFileTest {

    @Test
    void status404() {
      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/99/tags/4", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    @DirtiesContext
    void status200() {
      // act
      final ResponseEntity<Void> response =
              restTemplate.exchange("/v1/files/1/tags/2", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

      // assert
      assertThat(response)
              .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
              .extracting(ResponseEntity::getBody)
              .isNull();
      assertThat(restTemplate.exchange("/v1/files/1/tags", HttpMethod.GET, HttpEntity.EMPTY, TagDto[].class))
              .extracting(ResponseEntity::getBody, InstanceOfAssertFactories.array(TagDto[].class))
              .isEmpty();
    }
  }
}
