package culturelog.rest.utils;


import culturelog.rest.domain.Location;
import culturelog.rest.dto.LocationDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for {@link Location}.
 * @author Jan Venstermans
 */
public class LocationUtils {

    private LocationUtils() {
    }

    public static List<LocationDto> toLocationDtoList(List<Location> locationList) {
        if (locationList == null) {
            return Collections.emptyList();
        }
        return locationList.stream().map(LocationUtils::toLocationDto).collect(Collectors.toList());
    }

    public static LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        LocationDto locationDto = new LocationDto();
        locationDto.setId(location.getId());
        locationDto.setName(location.getName());
        locationDto.setDescription(location.getDescription());
//        locationDto.setAddress(location.getAddress());
//        locationDto.setLat(location.getLat());
//        locationDto.setLng(location.getLng());
        return locationDto;
    }

    public static Location fromLocationDto(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        Location location = new Location();
        location.setId(locationDto.getId());
        location.setName(locationDto.getName());
        location.setDescription(locationDto.getDescription());
//        location.setAddress(locationDto.getAddress());
//        location.setLat(locationDto.getLat());
//        location.setLng(locationDto.getLng());
        return location;
    }
}
