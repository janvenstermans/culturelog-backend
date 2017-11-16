package culturelog.rest.service.impl;

import culturelog.rest.domain.DisplayDate;
import culturelog.rest.domain.Experience;
import culturelog.rest.domain.Moment;
import culturelog.rest.domain.MomentType;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.repository.ExperienceRepository;
import culturelog.rest.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Collections;

/**
 * @author Jan Venstermans
 */
@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceRepository experienceRepository;

    public Experience save(Experience experience) throws CultureLogException {
        checkRequiredFieldsForSave(experience);
        return experienceRepository.save(experience);
    }
//
//    public List<Experience> getExperiencesOfUser(String username) {
//        return experienceRepository.findByUsername(username);
//    }
//
//    public Experience getById(Long experienceId) {
//        return experienceRepository.findOne(experienceId);
//    }

    private void checkRequiredFieldsForSave(@NotNull Experience experience) throws CultureLogException {
        if (experience.getName() == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_NAME_ATTRIBUTE);
        }
        if (experience.getType() == null || experience.getType().getId() == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_TYPE_ATTRIBUTE);
        }
//        if (CultureLogUtils.isNullOrEmpty(experience.getUsername())) {
//            throw new CultureLogException("Cannot save experience with empty field username");
//        }
//        if (CultureLogUtils.isNullOrEmpty(experience.getTitle())) {
//            throw new CultureLogException("Cannot save experience with empty field title");
//        }
//        if (experience.getDate() == null) {
//            throw new CultureLogException("Cannot save experience with empty field date");
//        }
        //default fallback for the moment: type date, displaytype DATE
//        if (experience.getMoment() == null) {
//            Moment moment = new Moment();
//            moment.setType(MomentType.DATE);
//            moment.setDisplayDates(Collections.singletonList(new DisplayDate()));
//            experience.setMoment(moment);
//        }
    }
}
