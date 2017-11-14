package culturelog.rest.service;

import culturelog.rest.domain.Experience;
import culturelog.rest.exception.CultureLogException;

/**
 * @author Jan Venstermans
 */
public interface ExperienceService {

    Experience save(Experience experience) throws CultureLogException;
}
