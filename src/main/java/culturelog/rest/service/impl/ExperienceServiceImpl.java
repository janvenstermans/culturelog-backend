package culturelog.rest.service.impl;

import culturelog.rest.domain.*;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.repository.ExperienceRepository;
import culturelog.rest.repository.MediumRepository;
import culturelog.rest.repository.MomentRepository;
import culturelog.rest.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Collections;

/**
 * @author Jan Venstermans
 */
@Service
@Transactional
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private MediumRepository mediumRepository;

    public Experience save(Experience experience) throws CultureLogException {
        checkRequiredFieldsForSave(experience);
        // first save moment
//        experience.setMoment(momentRepository.save(experience.getMoment()));
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

        //type required
        if (experience.getType() == null || experience.getType().getId() == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_TYPE_ATTRIBUTE);
        }
        Medium experienceType = mediumRepository.findOne(experience.getType().getId());
        if (experienceType == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_EXISTING_TYPE_ATTRIBUTE_FOR_USER, new Object[]{experience.getType().getId()});
        }
        if (experience.getUser() == null && experienceType.getUser() != null) {
            // experience is global, but the type is not
            //TODO: test
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_EXISTING_TYPE_ATTRIBUTE_FOR_USER, new Object[]{experience.getType().getId()});
        }
        if (experience.getUser() != null && experienceType.getUser() != null) {
            // both experience and type are not global: users must be same
            if (experience.getUser().getId() != experienceType.getUser().getId()) {
                throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_EXISTING_TYPE_ATTRIBUTE_FOR_USER, new Object[]{experience.getType().getId()});
            }

        }
        experience.setType(experienceType);

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
        if (experience.getMoment() == null) {
            Moment moment = new Moment();
            moment.setType(MomentType.DATE);
            moment.setDisplayDates(Collections.singletonList(new DisplayDate()));
            experience.setMoment(moment);
        }
    }
}
