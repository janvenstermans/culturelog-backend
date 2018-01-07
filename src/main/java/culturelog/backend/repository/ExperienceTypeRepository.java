package culturelog.backend.repository;

import culturelog.backend.domain.ExperienceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface ExperienceTypeRepository extends JpaRepository<ExperienceType, Long> {

    List<ExperienceType> findByUserId(Long userId);

    @Query("Select m from ExperienceType m where m.user is null or m.user.id = ?1")
    List<ExperienceType> findByUserIdIncludingGlobal(Long userId);

    Optional<ExperienceType> findByUserIdAndName(Long userId, String naam);

    /**
     * There can be more than one by same name: combination with userId is important.
     * @param naam
     * @return
     */
    List<ExperienceType> findByName(String naam);
}
