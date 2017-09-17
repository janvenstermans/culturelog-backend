package culturelog.rest.service;

import culturelog.rest.domain.Location;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location save(Location location) throws CultureLogException {
        if (location.getName() == null) {
            throw new CultureLogException("Location needs a name attribute");
        }
        //check user-Naam combination
        Long userId = location.getUser() != null ? location.getUser().getId() : null;
        Optional<Location> existing = locationRepository.findByUserIdAndName(userId, location.getName());
        if (existing.isPresent()) {
            throw new CultureLogException("There is already a location object with name " + location.getName() + " for logged in user");
        }
        return locationRepository.save(location);
    }

    public List<Location> getLocationsOfUserByUserId(Long userId, boolean includeGeneral) {
        if (includeGeneral) {
            return locationRepository.findByUserIdIncludingGlobal(userId);
        }
        return locationRepository.findByUserId(userId);
    }

    public Location getById(Long userId) {
        return locationRepository.findOne(userId);
    }
}
