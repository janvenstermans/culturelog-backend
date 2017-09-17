package culturelog.rest.repository;

import culturelog.rest.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUserId(Long userId);

    @Query("Select l from Location l where l.user is null or l.user.id = ?1")
    List<Location> findByUserIdIncludingGlobal(Long userId);

    Optional<Location> findByUserIdAndName(Long userId, String naam);

    /**
     * There can be more than one by same name: combination with userId is important.
     * @param naam
     * @return
     */
    List<Location> findByName(String naam);
}
