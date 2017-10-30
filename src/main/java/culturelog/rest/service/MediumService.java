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
public interface MediumService {

    Medium save(Medium medium) throws CultureLogException;

    List<Medium> getMediaOfUserByUserId(Long userId, boolean includeGeneral);

    Medium getById(Long userId);
}
