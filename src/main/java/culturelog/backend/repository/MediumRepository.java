package culturelog.backend.repository;

import culturelog.backend.domain.Medium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface MediumRepository extends JpaRepository<Medium, Long> {

    List<Medium> findByUserId(Long userId);

    @Query("Select m from Medium m where m.user is null or m.user.id = ?1")
    List<Medium> findByUserIdIncludingGlobal(Long userId);

    Optional<Medium> findByUserIdAndName(Long userId, String naam);

    /**
     * There can be more than one by same name: combination with userId is important.
     * @param naam
     * @return
     */
    List<Medium> findByName(String naam);
}
