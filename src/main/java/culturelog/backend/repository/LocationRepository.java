package culturelog.backend.repository;

import culturelog.backend.domain.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

    Page<Location> findByUserId(Long userId, Pageable pageable);

    @Query("Select l from Location l where l.user is null or l.user.id = ?1")
    Page<Location> findByUserIdIncludingGlobal(Long userId, Pageable pageable);

    Optional<Location> findByUserIdAndName(Long userId, String naam);

    /**
     * There can be more than one by same name: combination with userId is important.
     * @param naam
     * @return
     */
    Page<Location> findByName(String naam, Pageable pageable);
}
