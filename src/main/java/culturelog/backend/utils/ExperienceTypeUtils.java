package culturelog.backend.utils;


import culturelog.backend.domain.ExperienceType;
import culturelog.backend.domain.User;
import culturelog.backend.dto.ExperienceTypeDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for {@link ExperienceType}.
 * @author Jan Venstermans
 */
public class ExperienceTypeUtils {

    private ExperienceTypeUtils() {
    }

    public static boolean isExperienceTypeOfUser(ExperienceType experienceType, User user, boolean includeGlobal) {
        if (experienceType == null) {
            throw new IllegalArgumentException("experienceType cannot be null");
        }
        if (includeGlobal && isGlobal(experienceType)) {
            return true;
        }
        return UserUtils.areUsersSame(experienceType.getUser(), user);
    }

    public static boolean isGlobal(ExperienceType experienceType) {
        return experienceType.getUser() == null;
    }

    public static List<ExperienceTypeDto> toExperienceTypeDtoList(List<ExperienceType> experienceTypeList) {
        if (experienceTypeList == null) {
            return Collections.emptyList();
        }
        return experienceTypeList.stream().map(ExperienceTypeUtils::toExperienceTypeDto).collect(Collectors.toList());
    }

    public static ExperienceTypeDto toExperienceTypeDto(ExperienceType experienceType) {
        if (experienceType == null) {
            return null;
        }
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceType.getId());
        experienceTypeDto.setName(experienceType.getName());
        experienceTypeDto.setDescription(experienceType.getDescription());
        experienceTypeDto.setGlobal(experienceType.getUser() == null);
        return experienceTypeDto;
    }

    public static ExperienceType fromExperienceTypeDto(ExperienceTypeDto experienceTypeDto) {
        if (experienceTypeDto == null) {
            return null;
        }
        ExperienceType experienceType = new ExperienceType();
        experienceType.setId(experienceTypeDto.getId());
        experienceType.setName(experienceTypeDto.getName());
        experienceType.setDescription(experienceTypeDto.getDescription());
        return experienceType;
    }
}
