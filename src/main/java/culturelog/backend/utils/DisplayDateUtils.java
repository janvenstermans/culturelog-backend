package culturelog.backend.utils;


import culturelog.backend.domain.DisplayDate;
import culturelog.backend.domain.DisplayDateType;
import culturelog.backend.dto.DisplayDateDto;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
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

    public static DisplayDate createDisplayDate(@NotNull DisplayDateType type, @NotNull Date date) {
        DisplayDate displayDate = new DisplayDate();
        displayDate.setType(type);
        displayDate.setDate(date);
        return displayDate;
    }
}
