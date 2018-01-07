package culturelog.backend.service.impl;

import culturelog.backend.domain.*;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.ExperienceRepository;
import culturelog.backend.repository.LocationRepository;
import culturelog.backend.repository.ExperienceTypeRepository;
import culturelog.backend.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private ExperienceTypeRepository experienceTypeRepository;

    @Autowired
    private LocationRepository locationRepository;

    public Experience save(Experience experience) throws CultureLogException {
        checkRequiredFieldsForSave(experience);
        return experienceRepository.save(experience);
    }

    @Override
    public Page<Experience> getExperiencesOfUser(Long userId, Pageable pageable) {
        return experienceRepository.findByUserId(userId, pageable);
    }

//    public Experience getById(Long experienceId) {
//        return experienceRepository.findOne(experienceId);
//    }

    private void checkRequiredFieldsForSave(@NotNull Experience experience) throws CultureLogException {
        if (experience.getName() == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_NAME_ATTRIBUTE);
        }

        checkTypeFieldForSave(experience);

        checkLocationFieldForSave(experience);

        //default fallback for the moment: type date, displaytype DATE
        if (experience.getMoment() == null) {
            Moment moment = new Moment();
            moment.setType(MomentType.DATE);
            moment.setDisplayDates(Collections.singletonList(new DisplayDate()));
            experience.setMoment(moment);
        }
        Moment moment = experience.getMoment();
        if (moment.getSortDate() == null) {
            moment.setSortDate(moment.getDisplayDates().get(0).getDate());
        }
    }

    private void checkTypeFieldForSave(@NotNull Experience experience) throws CultureLogException {
        //type required
        if (experience.getType() == null || experience.getType().getId() == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_NEEDS_TYPE_ATTRIBUTE);
        }
        ExperienceType experienceType = experienceTypeRepository.findOne(experience.getType().getId());
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
    }

    private void checkLocationFieldForSave(@NotNull Experience experience) throws CultureLogException {
        //type option
        if (experience.getLocation() == null) {
            return;
        }
        Long locationId = experience.getLocation().getId();
        if (locationId == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_OPTIONAL_LOCATION_ATTRIBUTE_ID_REQUIRED);
        }
        Location location = locationRepository.findOne(locationId);
        if (location == null) {
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_OPTIONAL_LOCATION_NOT_FOUND_FOR_USER, new Object[]{locationId});
        }
        if (experience.getUser() == null && location.getUser() != null) {
            // experience is global, but the type is not
            //TODO: test
            throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_OPTIONAL_LOCATION_NOT_FOUND_FOR_USER, new Object[]{locationId});
        }
        if (experience.getUser() != null && location.getUser() != null) {
            // both experience and type are not global: users must be same
            if (experience.getUser().getId() != location.getUser().getId()) {
                throw new CultureLogException(CultureLogExceptionKey.EXPERIENCE_OPTIONAL_LOCATION_NOT_FOUND_FOR_USER, new Object[]{locationId});
            }

        }
        experience.setLocation(location);
    }
}
