package culturelog.rest.service;

import culturelog.rest.domain.Medium;
import culturelog.rest.exception.CultureLogException;
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
            throw new CultureLogException("Medium needs a name attribute");
        }
        //check user-Naam combination
        Long userId = medium.getUser() != null ? medium.getUser().getId() : null;
        Optional<Medium> existing = mediumRepository.findByUserIdAndName(userId, medium.getName());
        if (existing.isPresent()) {
            throw new CultureLogException("There is already a medium object with name " + medium.getName() + " for logged in user");
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
