package culturelog.rest.service;

import culturelog.rest.domain.Location;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface LocationService {

    Location save(Location location) throws CultureLogException;

    List<Location> getLocationsOfUserByUserId(Long userId, boolean includeGeneral);

    Location getById(Long userId);
}
