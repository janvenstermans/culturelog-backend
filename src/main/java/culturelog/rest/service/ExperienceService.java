package culturelog.rest.service;

import culturelog.rest.domain.Experience;
import culturelog.rest.domain.User;
import culturelog.rest.exception.CultureLogException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Jan Venstermans
 */
public interface ExperienceService {

    Experience save(Experience experience) throws CultureLogException;
    Page<Experience> getExperiencesOfUser(Long userId, Pageable pageable);
}
