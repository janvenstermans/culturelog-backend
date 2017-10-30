package culturelog.rest.service.impl;

import culturelog.rest.domain.Location;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.repository.LocationRepository;
import culturelog.rest.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public Location save(Location location) throws CultureLogException {
        if (location.getName() == null) {
            throw new CultureLogException(CultureLogExceptionKey.LOCATION_NEEDS_NAME_ATTRIBUTE);
        }
        //check user-Naam combination
        Long userId = location.getUser() != null ? location.getUser().getId() : null;
        Optional<Location> existing = locationRepository.findByUserIdAndName(userId, location.getName());
        if (existing.isPresent()) {
            throw new CultureLogException(CultureLogExceptionKey.LOCATION_WITH_NAME_FOR_USER_ALREADY_EXISTS, new Object[]{location.getName()});
        }
        return locationRepository.save(location);
    }

    @Override
    public List<Location> getLocationsOfUserByUserId(Long userId, boolean includeGeneral) {
        if (includeGeneral) {
            return locationRepository.findByUserIdIncludingGlobal(userId);
        }
        return locationRepository.findByUserId(userId);
    }

    @Override
    public Location getById(Long userId) {
        return locationRepository.findOne(userId);
    }
}
