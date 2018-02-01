package culturelog.backend.controller;

import culturelog.backend.domain.Location;
import culturelog.backend.domain.User;
import culturelog.backend.dto.LocationDto;
import culturelog.backend.exception.CultureLogControllerExceptionKey;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.security.CultureLogSecurityService;
import culturelog.backend.service.LocationService;
import culturelog.backend.service.MessageService;
import culturelog.backend.utils.LocationUtils;
import culturelog.backend.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

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

    @Autowired
    private MessageService messageService;

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_COLUMN_0 = "name";
    public static final boolean DEFAULT_SORT_ASC_0 = true;
    public static final String DEFAULT_SORT_COLUMN_1 = "id";
    public static final boolean DEFAULT_SORT_ASC_1 = true;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createLocation(@RequestBody LocationDto locationDto, Locale locale) {
        Location location = LocationUtils.fromLocationDto(locationDto);
        try {
            if (location.getId() != null) {
                throw new CultureLogException(CultureLogExceptionKey.CREATE_WITH_ID);
            }
            location.setUser(securityService.getLoggedInUser());

            Location newLocation = locationService.save(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(LocationUtils.toLocationDto(newLocation));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(CultureLogControllerExceptionKey.LOCATIONS_CREATE, e, locale));
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLocationsOfUser(@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE)
                                                    @SortDefault.SortDefaults({
                                                            @SortDefault(sort = DEFAULT_SORT_COLUMN_0, direction = Sort.Direction.ASC),
                                                            @SortDefault(sort = DEFAULT_SORT_COLUMN_1, direction = Sort.Direction.ASC)
                                                    }) Pageable pageable) {
        Long userId = securityService.getLoggedInUserId();
        Page<Location> locationPage = locationService.getLocationsOfUserByUserId(userId, true, pageable);
        return ResponseEntity.ok(locationPage.map(LocationUtils::toLocationDto));
    }

    @RequestMapping(value = "/{locationId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLocation(@PathVariable(value="locationId") Long locationId, Locale locale) {
        User user = securityService.getLoggedInUser();
        Location location = locationService.getById(locationId);
        if (location != null && LocationUtils.isLocationOfUser(location, user, true)) {
            return ResponseEntity.ok(LocationUtils.toLocationDto(location));
        }
        return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                CultureLogControllerExceptionKey.LOCATIONS_GET_ONE_UNKNOWN_ID, new Object[]{locationId}, locale));
    }

    @RequestMapping(value = "/{locationId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateLocation(@PathVariable(value="locationId") Long locationId,
                                              @RequestBody LocationDto locationDto, Locale locale) {
        Location location = LocationUtils.fromLocationDto(locationDto);
        User user = securityService.getLoggedInUser();
        Location existingLocation = locationService.getById(locationId);
        if (existingLocation == null || !UserUtils.areUsersSame(existingLocation.getUser(), user)) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                    CultureLogControllerExceptionKey.LOCATIONS_UPDATE_ONE, new Object[]{locationId}, locale));
        }
        location.setId(locationId);
        location.setUser(user);
        try {
            Location updatedLocation = locationService.save(location);
            return ResponseEntity.ok(LocationUtils.toLocationDto(updatedLocation));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                    CultureLogControllerExceptionKey.LOCATIONS_UPDATE_ONE, new Object[]{locationId}, e, locale));
        }
    }

    //TODO: remove location, with check if used in experience
}