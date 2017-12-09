package culturelog.rest.utils;


import culturelog.rest.domain.Experience;
import culturelog.rest.dto.ExperienceDto;

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
        experienceDto.setType(MediumUtils.toMediumDto(experience.getType()));
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
        experience.setType(MediumUtils.fromMediumDto(experienceDto.getType()));
        experience.setMoment(MomentUtils.fromMomentDto(experienceDto.getMoment()));
        experience.setLocation(LocationUtils.fromLocationDto(experienceDto.getLocation()));
        experience.setComment(experienceDto.getComment());
        return experience;
    }
}
