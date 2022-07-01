package ch.mav.schedario.schedario.repository;

import ch.mav.schedario.schedario.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
}
