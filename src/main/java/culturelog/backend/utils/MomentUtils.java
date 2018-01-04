package culturelog.backend.utils;


import culturelog.backend.domain.Moment;
import culturelog.backend.dto.DateMomentDto;
import culturelog.backend.dto.MomentDto;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for {@link Moment}.
 * @author Jan Venstermans
 */
public class MomentUtils {

    private MomentUtils() {
    }

    public static List<MomentDto> toMomentDtoList(List<Moment> mediumlist) {
        if (mediumlist == null) {
            return Collections.emptyList();
        }
        return mediumlist.stream().map(MomentUtils::toMomentDto).collect(Collectors.toList());
    }

    public static MomentDto toMomentDto(Moment moment) {
        if (moment == null) {
            return null;
        }
        switch (moment.getType()) {
            case DATE:
                return toDateMomentDto(moment);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Moment fromMomentDto(MomentDto momentDto) {
        if (momentDto == null) {
            return null;
        }
        Moment moment = new Moment();
        moment.setId(momentDto.getId());
        switch (momentDto.getMomentType()) {
            case DATE:
                if (momentDto instanceof DateMomentDto) {
                    copyInfoFromDateMomentDto((DateMomentDto) momentDto, moment);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        return moment;
    }

    // helper methods.

    /**
     * Don't allow public access, as it requires the moment object to have some specific values.
     * @param moment
     * @return
     */
    private static DateMomentDto toDateMomentDto(Moment moment) {
        DateMomentDto dateMomentDto = new DateMomentDto();
        dateMomentDto.setId(moment.getId());
        dateMomentDto.setDisplayDate(DisplayDateUtils.toDisplayDateDto(moment.getDisplayDates().get(0)));
        return dateMomentDto;
    }

    public static void copyInfoFromDateMomentDto(@NotNull DateMomentDto momentDto, @NotNull Moment moment) {
        moment.setDisplayDates(Collections.singletonList(DisplayDateUtils.fromDisplayDateDto(momentDto.getDisplayDate())));
    }
}
