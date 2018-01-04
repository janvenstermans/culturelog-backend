package culturelog.backend.service.impl;

import culturelog.backend.domain.Medium;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.MediumRepository;
import culturelog.backend.service.MediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
@Service
public class MediumServiceImpl implements MediumService {

    @Autowired
    private MediumRepository mediumRepository;

    @Override
    public Medium save(Medium medium) throws CultureLogException {
        if (medium.getName() == null) {
            throw new CultureLogException(CultureLogExceptionKey.MEDIUM_NEEDS_NAME_ATTRIBUTE);
        }
        //check user-Naam combination
        Long userId = medium.getUser() != null ? medium.getUser().getId() : null;
        Optional<Medium> existing = mediumRepository.findByUserIdAndName(userId, medium.getName());
        if (existing.isPresent()) {
            throw new CultureLogException(CultureLogExceptionKey.MEDIUM_WITH_NAME_FOR_USER_ALREADY_EXISTS, new Object[] {medium.getName()});
        }
        return mediumRepository.save(medium);
    }

    @Override
    public List<Medium> getMediaOfUserByUserId(Long userId, boolean includeGeneral) {
        if (includeGeneral) {
            return mediumRepository.findByUserIdIncludingGlobal(userId);
        }
        return mediumRepository.findByUserId(userId);
    }

    @Override
    public Medium getById(Long userId) {
        return mediumRepository.findOne(userId);
    }
}
