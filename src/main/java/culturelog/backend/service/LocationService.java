package culturelog.backend.service;

import culturelog.backend.domain.Location;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface LocationService {

    Location save(Location location) throws CultureLogException;

    Page<Location> getLocationsOfUserByUserId(Long userId, boolean includeGeneral, Pageable pageable);

    Location getById(Long userId);
}
