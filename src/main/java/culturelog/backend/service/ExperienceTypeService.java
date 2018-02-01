package culturelog.backend.service;

import culturelog.backend.domain.ExperienceType;
import culturelog.backend.exception.CultureLogException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Jan Venstermans
 */
public interface ExperienceTypeService {

    ExperienceType save(ExperienceType experienceType) throws CultureLogException;

    Page<ExperienceType> getExperienceTypesOfUserByUserId(Long userId, boolean includeGeneral, Pageable pageable);

    ExperienceType getById(Long userId);
}
