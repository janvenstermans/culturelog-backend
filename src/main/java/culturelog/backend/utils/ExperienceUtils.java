package culturelog.backend.utils;


import culturelog.backend.domain.Experience;
import culturelog.backend.domain.User;
import culturelog.backend.dto.ExperienceDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for {@link Experience}.
 * @author Jan Venstermans
 */
public class ExperienceUtils {

    private ExperienceUtils() {
    }

    public static boolean isExperienceOfUser(Experience experience, User user) {
        if (experience == null) {
            throw new IllegalArgumentException("experience cannot be null");
        }
        return UserUtils.areUsersSame(experience.getUser(), user);
    }

    public static List<ExperienceDto> toExperienceDtoList(List<Experience> experienceList) {
        if (experienceList == null) {
            return Collections.emptyList();
        }
        return experienceList.stream().map(ExperienceUtils::toExperienceDto).collect(Collectors.toList());
    }

    public static ExperienceDto toExperienceDto(Experience experience) {
        if (experience == null) {
            return null;
        }
        ExperienceDto experienceDto = new ExperienceDto();
        experienceDto.setId(experience.getId());
        experienceDto.setName(experience.getName());
        experienceDto.setName(experience.getName());
        experienceDto.setType(ExperienceTypeUtils.toExperienceTypeDto(experience.getType()));
        experienceDto.setMoment(MomentUtils.toMomentDto(experience.getMoment()));
        experienceDto.setLocation(LocationUtils.toLocationDto(experience.getLocation()));
        experienceDto.setComment(experience.getComment());
        return experienceDto;
    }

    public static Experience fromExperienceDto(ExperienceDto experienceDto) {
        if (experienceDto == null) {
            return null;
        }
        Experience experience = new Experience();
        experience.setId(experienceDto.getId());
        experience.setName(experienceDto.getName());
        experience.setType(ExperienceTypeUtils.fromExperienceTypeDto(experienceDto.getType()));
        experience.setMoment(MomentUtils.fromMomentDto(experienceDto.getMoment()));
        experience.setLocation(LocationUtils.fromLocationDto(experienceDto.getLocation()));
        experience.setComment(experienceDto.getComment());
        return experience;
    }
}
