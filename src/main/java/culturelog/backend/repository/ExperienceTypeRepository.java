package culturelog.backend.repository;

import culturelog.backend.domain.ExperienceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface ExperienceTypeRepository extends JpaRepository<ExperienceType, Long> {

    Page<ExperienceType> findByUserId(Long userId, Pageable pageable);

    @Query("Select m from ExperienceType m where m.user is null or m.user.id = ?1")
    Page<ExperienceType> findByUserIdIncludingGlobal(Long userId, Pageable pageable);

    Optional<ExperienceType> findByUserIdAndName(Long userId, String naam);

    /**
     * There can be more than one by same name: combination with userId is important.
     * @param naam
     * @return
     */
    Page<ExperienceType> findByName(String naam, Pageable pageable);
}
