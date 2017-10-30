package culturelog.rest.service.impl;

import culturelog.rest.repository.ExperienceRepository;
import culturelog.rest.service.ExperienceService;

/**
 * @author Jan Venstermans
 */
//@Service
public class ExperienceServiceImpl implements ExperienceService {

//    @Autowired
    private ExperienceRepository experienceRepository;

//    public Experience save(Experience experience) throws CultureLogException {
//        checkRequiredFieldsForSave(experience);
//        return experienceRepository.save(experience);
//    }
//
//    public List<Experience> getExperiencesOfUser(String username) {
//        return experienceRepository.findByUsername(username);
//    }
//
//    public Experience getById(Long experienceId) {
//        return experienceRepository.findOne(experienceId);
//    }

//    private void checkRequiredFieldsForSave(Experience experience) throws CultureLogException {
//        if (experience == null) {
//            return;
//        }
//        if (CultureLogUtils.isNullOrEmpty(experience.getUsername())) {
//            throw new CultureLogException("Cannot save experience with empty field username");
//        }
//        if (CultureLogUtils.isNullOrEmpty(experience.getTitle())) {
//            throw new CultureLogException("Cannot save experience with empty field title");
//        }
//        if (experience.getDate() == null) {
//            throw new CultureLogException("Cannot save experience with empty field date");
//        }
//    }
}
