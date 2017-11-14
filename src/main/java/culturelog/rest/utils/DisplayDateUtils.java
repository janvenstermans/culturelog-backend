package culturelog.rest.utils;


import culturelog.rest.domain.DisplayDate;
import culturelog.rest.dto.DisplayDateDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for {@link DisplayDate}.
 * @author Jan Venstermans
 */
public class DisplayDateUtils {

    private DisplayDateUtils() {
    }

    public static List<DisplayDateDto> toLocationDtoList(List<DisplayDate> displayDateList) {
        if (displayDateList == null) {
            return Collections.emptyList();
        }
        return displayDateList.stream().map(DisplayDateUtils::toDisplayDateDto).collect(Collectors.toList());
    }

    public static DisplayDateDto toDisplayDateDto(DisplayDate displayDate) {
        if (displayDate == null) {
            return null;
        }
        DisplayDateDto locationDto = new DisplayDateDto();
        locationDto.setId(displayDate.getId());
        locationDto.setType(displayDate.getType());
        locationDto.setDate(displayDate.getDate());
        return locationDto;
    }

    public static DisplayDate fromDisplayDateDto(DisplayDateDto displayDateDto) {
        if (displayDateDto == null) {
            return null;
        }
        DisplayDate displayDate = new DisplayDate();
        displayDate.setId(displayDateDto.getId());
        displayDate.setType(displayDateDto.getType());
        displayDate.setDate(displayDateDto.getDate());
        return displayDate;
    }
}
