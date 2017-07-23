package culturelog.rest.service;

import culturelog.rest.domain.Location;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jan Venstermans
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location save(Location location) throws CultureLogException {
        return locationRepository.save(location);
    }

    public List<Location> getLocationsOfUserByUserId(Long userId) {
        return locationRepository.findByUserId(userId);
    }

    public Location getById(Long userId) {
        return locationRepository.findOne(userId);
    }
}
