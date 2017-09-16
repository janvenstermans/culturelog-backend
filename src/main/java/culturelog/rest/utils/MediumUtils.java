package culturelog.rest.utils;


import culturelog.rest.domain.Medium;
import culturelog.rest.domain.User;
import culturelog.rest.dto.MediumDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for {@link Medium}.
 * @author Jan Venstermans
 */
public class MediumUtils {

    private MediumUtils() {
    }

    public static boolean isMediumOfUser(Medium medium, User user, boolean includeGlobal) {
        if (medium == null) {
            throw new IllegalArgumentException("medium cannot be null");
        }
        if (includeGlobal && isGlobal(medium)) {
            return true;
        }
        return UserUtils.areUsersSame(medium.getUser(), user);
    }

    public static boolean isGlobal(Medium medium) {
        return medium.getUser() == null;
    }

    public static List<MediumDto> toMediumDtoList(List<Medium> mediumList) {
        if (mediumList == null) {
            return Collections.emptyList();
        }
        return mediumList.stream().map(MediumUtils::toMediumDto).collect(Collectors.toList());
    }

    public static MediumDto toMediumDto(Medium medium) {
        if (medium == null) {
            return null;
        }
        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(medium.getId());
        mediumDto.setName(medium.getName());
        mediumDto.setDescription(medium.getDescription());
        mediumDto.setGlobal(medium.getUser() == null);
        return mediumDto;
    }

    public static Medium fromMediumDto(MediumDto mediumDto) {
        if (mediumDto == null) {
            return null;
        }
        Medium medium = new Medium();
        medium.setId(mediumDto.getId());
        medium.setName(mediumDto.getName());
        medium.setDescription(mediumDto.getDescription());
        return medium;
    }
}
