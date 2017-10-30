package culturelog.rest.controller;

import culturelog.rest.domain.Location;
import culturelog.rest.domain.User;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.security.CultureLogSecurityService;
import culturelog.rest.service.LocationService;
import culturelog.rest.utils.LocationUtils;
import culturelog.rest.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CultureLogSecurityService securityService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createLocation(@RequestBody(required = true) LocationDto locationDto) {
        Location location = LocationUtils.fromLocationDto(locationDto);
        if (location.getId() != null) {
            return ResponseEntity.badRequest().body("Cannot create location with id ");
        }
        location.setUser(securityService.getLoggedInUser());
        try {
            Location newLocation = locationService.save(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(LocationUtils.toLocationDto(newLocation));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLocationsOfUser() {
        Long userId = securityService.getLoggedInUserId();
        List<Location> locationList = locationService.getLocationsOfUserByUserId(userId, true);
        return ResponseEntity.ok(LocationUtils.toLocationDtoList(locationList));
    }

    @RequestMapping(value = "/{locationId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLocation(@PathVariable(value="locationId", required = true) Long locationId) {
        User user = securityService.getLoggedInUser();
        Location location = locationService.getById(locationId);
        if (location != null && LocationUtils.isLocationOfUser(location, user, true)) {
            return ResponseEntity.ok(LocationUtils.toLocationDto(location));
        }
        return ResponseEntity.badRequest().body("Cannot find location with id " + locationId);
    }

    @RequestMapping(value = "/{locationId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateLocation(@PathVariable(value="locationId", required = true) Long locationId,
                                              @RequestBody(required = true) LocationDto locationDto) {
        Location location = LocationUtils.fromLocationDto(locationDto);
        User user = securityService.getLoggedInUser();
        Location existingLocation = locationService.getById(locationId);
        if (existingLocation == null || !UserUtils.areUsersSame(existingLocation.getUser(), user)) {
            return ResponseEntity.badRequest().body("Cannot update location with id " + locationId);
        }
        location.setId(locationId);
        location.setUser(user);
        try {
            Location updatedLocation = locationService.save(location);
            return ResponseEntity.ok(LocationUtils.toLocationDto(updatedLocation));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    //TODO: remove location, with check if used in experience
}