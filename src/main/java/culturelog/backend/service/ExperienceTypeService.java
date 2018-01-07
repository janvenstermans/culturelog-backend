package culturelog.backend.service;

import culturelog.backend.domain.ExperienceType;
import culturelog.backend.exception.CultureLogException;

import java.util.List;

/**
 * @author Jan Venstermans
 */
public interface ExperienceTypeService {

    ExperienceType save(ExperienceType experienceType) throws CultureLogException;

    List<ExperienceType> getExperienceTypesOfUserByUserId(Long userId, boolean includeGeneral);

    ExperienceType getById(Long userId);
}
