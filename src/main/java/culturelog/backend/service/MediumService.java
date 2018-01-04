package culturelog.backend.service;

import culturelog.backend.domain.Medium;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.MediumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface MediumService {

    Medium save(Medium medium) throws CultureLogException;

    List<Medium> getMediaOfUserByUserId(Long userId, boolean includeGeneral);

    Medium getById(Long userId);
}
