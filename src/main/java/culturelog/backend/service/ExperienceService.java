package culturelog.backend.service;

import culturelog.backend.domain.Experience;
import culturelog.backend.domain.User;
import culturelog.backend.exception.CultureLogException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Jan Venstermans
 */
public interface ExperienceService {

    Experience save(Experience experience) throws CultureLogException;

    Page<Experience> getExperiencesOfUser(Long userId, Pageable pageable);

    Experience getById(Long experienceId);
}
