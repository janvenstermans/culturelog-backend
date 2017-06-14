package culturelog.rest.service;

import culturelog.rest.domain.Location;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.repository.LocationRepository;
import culturelog.rest.utils.CultureLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jan Venstermans
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location save(Location location) throws CultureLogException {
        // check required values
        if (CultureLogUtils.isNullOrEmpty(location.getDescription())) {
            throw new CultureLogException("Cannot create location: description is required");
        }
        if (CultureLogUtils.isNullOrEmpty(location.getUserId())) {
            throw new CultureLogException("Cannot create location: user must be set");
        }
        return locationRepository.save(location);
    }

    public List<Location> getLocationsOfUserByUserId(String userId) {
        return locationRepository.findByUserId(userId);
    }

    public Location getById(String userId) {
        return locationRepository.findOne(userId);
    }
}
