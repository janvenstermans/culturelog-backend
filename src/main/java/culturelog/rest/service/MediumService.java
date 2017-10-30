package culturelog.rest.service;

import culturelog.rest.domain.Medium;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.repository.MediumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
@Service
public class MediumService {

    @Autowired
    private MediumRepository mediumRepository;

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

    public List<Medium> getMediaOfUserByUserId(Long userId, boolean includeGeneral) {
        if (includeGeneral) {
            return mediumRepository.findByUserIdIncludingGlobal(userId);
        }
        return mediumRepository.findByUserId(userId);
    }

    public Medium getById(Long userId) {
        return mediumRepository.findOne(userId);
    }
}
