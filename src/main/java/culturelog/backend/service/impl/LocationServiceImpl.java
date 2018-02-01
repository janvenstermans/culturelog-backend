package culturelog.backend.service.impl;

import culturelog.backend.domain.Location;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.LocationRepository;
import culturelog.backend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Location> getLocationsOfUserByUserId(Long userId, boolean includeGeneral, Pageable pageable) {
        if (includeGeneral) {
            return locationRepository.findByUserIdIncludingGlobal(userId, pageable);
        }
        return locationRepository.findByUserId(userId, pageable);
    }

    @Override
    public Location getById(Long userId) {
        return locationRepository.findOne(userId);
    }
}
