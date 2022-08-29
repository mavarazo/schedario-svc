package ch.mav.schedario.repository;

import ch.mav.schedario.model.File;
import ch.mav.schedario.model.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByChecksum(long checksum);

    List<File> findAllByStatus(Status status);

    @Override
    @Query("delete from File f where f.id = :id")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteById(@NotNull Long id);
}
