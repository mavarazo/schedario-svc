package ch.mav.schedario.repository;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByChecksum(long checksum);

    List<File> findAllByStatus(Status status);
}
