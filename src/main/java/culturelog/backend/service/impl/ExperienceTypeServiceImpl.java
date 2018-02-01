package culturelog.backend.service.impl;

import culturelog.backend.domain.ExperienceType;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.ExperienceTypeRepository;
import culturelog.backend.service.ExperienceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
@Service
public class ExperienceTypeServiceImpl implements ExperienceTypeService {

    @Autowired
    private ExperienceTypeRepository experienceTypeRepository;

    @Override
    public ExperienceType save(ExperienceType experienceType) throws CultureLogException {
        if (experienceType.getName() == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCETYPE_NEEDS_NAME_ATTRIBUTE);
        }
        //check user-Naam combination
        Long userId = experienceType.getUser() != null ? experienceType.getUser().getId() : null;
        Optional<ExperienceType> existing = experienceTypeRepository.findByUserIdAndName(userId, experienceType.getName());
        if (existing.isPresent()) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCETYPE_WITH_NAME_FOR_USER_ALREADY_EXISTS, new Object[] {experienceType.getName()});
        }
        return experienceTypeRepository.save(experienceType);
    }

    @Override
    public Page<ExperienceType> getExperienceTypesOfUserByUserId(Long userId, boolean includeGeneral, Pageable pageable) {
        if (includeGeneral) {
            return experienceTypeRepository.findByUserIdIncludingGlobal(userId, pageable);
        }
        return experienceTypeRepository.findByUserId(userId, pageable);
    }

    @Override
    public ExperienceType getById(Long userId) {
        return experienceTypeRepository.findOne(userId);
    }
}
