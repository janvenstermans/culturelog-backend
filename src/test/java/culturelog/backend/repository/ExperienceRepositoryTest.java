package culturelog.backend.repository;

import culturelog.backend.CultureLogBackendApplication;
import culturelog.backend.configuration.CultureLogTestConfiguration;
import culturelog.backend.controller.ExperienceControllerTest;
import culturelog.backend.domain.DisplayDateType;
import culturelog.backend.domain.Experience;
import culturelog.backend.domain.Moment;
import culturelog.backend.domain.User;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.service.ExperienceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static culturelog.backend.controller.LocationControllerTest.createLocationToSave;
import static culturelog.backend.controller.MediumControllerTest.createMediumToSave;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CultureLogBackendApplication.class)
@Transactional // db changes in one test are rolled back after test
public class ExperienceRepositoryTest {

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void findByUserId_sortByMomentSortDateIdDesc_pageSize3() throws CultureLogException {
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = createExperiencesForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sortProperty = "moment.sortDate";
        Sort.Direction sortDirection = Sort.Direction.DESC;
        Sort sort = new Sort(new Sort.Order(sortDirection, sortProperty));
        int totalPages = 2;

        List<Long> experienceIdsSorted = savedExperiences.values().stream().map(experience -> experience.getId()).collect(Collectors.toList());
        Collections.reverse(experienceIdsSorted);
        List<Long> expectedIdListPage0 = experienceIdsSorted.subList(0, pageSize);
        List<Long> expectedIdListPage1 = experienceIdsSorted.subList(pageSize, savedExperiences.size());

        executeAndAssertFindByUserId(CultureLogTestConfiguration.getUser1Id(), 0, pageSize, sort, expectedIdListPage0, savedExperiences.size(), totalPages, true, false);
        executeAndAssertFindByUserId(CultureLogTestConfiguration.getUser1Id(), 1, pageSize, sort, expectedIdListPage1, savedExperiences.size(), totalPages, false, true);
    }

    @Test
    public void findByUserId_sortByMomentSortDateIdAsc_pageSize2() throws CultureLogException {
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = createExperiencesForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 2;
        String sortProperty = "moment.sortDate";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Sort sort = new Sort(new Sort.Order(sortDirection, sortProperty));
        int totalPages = 3;

        List<Long> experienceIdsSorted = savedExperiences.values().stream().map(experience -> experience.getId()).collect(Collectors.toList());
        List<Long> expectedIdListPage0 = experienceIdsSorted.subList(0, pageSize);
        List<Long> expectedIdListPage1 = experienceIdsSorted.subList(pageSize, pageSize*2);
        List<Long> expectedIdListPage2 = experienceIdsSorted.subList(pageSize*2, savedExperiences.size());

        executeAndAssertFindByUserId(CultureLogTestConfiguration.getUser1Id(), 0, pageSize, sort, expectedIdListPage0, savedExperiences.size(), totalPages, true, false);
        executeAndAssertFindByUserId(CultureLogTestConfiguration.getUser1Id(), 1, pageSize, sort, expectedIdListPage1, savedExperiences.size(), totalPages, false, false);
        executeAndAssertFindByUserId(CultureLogTestConfiguration.getUser1Id(), 2, pageSize, sort, expectedIdListPage2, savedExperiences.size(), totalPages, false, true);
    }


    @Test
    public void findByUserId_sortByIdAsc_pageSize20() throws CultureLogException {
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = createExperiencesForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 20;
        String sortProperty = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Sort sort = new Sort(new Sort.Order(sortDirection, sortProperty));
        int totalPages = 1;

        List<Long> experienceIdsSorted = savedExperiences.values().stream().map(experience -> experience.getId()).collect(Collectors.toList());
        Collections.sort(experienceIdsSorted);
        List<Long> expectedIdListPage0 = experienceIdsSorted.subList(0, savedExperiences.size());

        executeAndAssertFindByUserId(CultureLogTestConfiguration.getUser1Id(), 0, pageSize, sort, expectedIdListPage0, savedExperiences.size(), totalPages, true, true);
    }

    // helper methods

    /**
     * @param userId
     * @return key: moment sortDate, value: savedExperience
     * @throws CultureLogException
     */
    private TreeMap<Date, Experience> createExperiencesForUser(Long userId) throws CultureLogException {
        User user1 = userRepository.findOne(userId);
        Long mediumFilmId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        Long mediumBookId = CultureLogTestConfiguration.getGlobalMediumIdBook();
        Long mediumTheaterId = mediumRepository.save(createMediumToSave("theaterCommon", user1)).getId();
        Long locationKinepolisId = CultureLogTestConfiguration.getGlobalLocationIdKinepolis();
        Long locationThuisId = locationRepository.save(createLocationToSave("thuis", user1)).getId();
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = new TreeMap<>();
        saveExperience(createExperienceToSave("testOne", user1, mediumFilmId, locationKinepolisId,
                ExperienceControllerTest.createDateMoment(DisplayDateType.DATE, 0), "ok"), savedExperiences);
        saveExperience(createExperienceToSave("testTwo", user1, mediumTheaterId, null,
                ExperienceControllerTest.createDateMoment(DisplayDateType.DATE_TIME, 2), "like this"), savedExperiences);
        saveExperience(createExperienceToSave("testThree", user1, mediumBookId, locationThuisId,
                ExperienceControllerTest.createDateMoment(DisplayDateType.DATE, -2), "nice one"), savedExperiences);
        saveExperience(createExperienceToSave("testFour", user1, mediumTheaterId, null,
                ExperienceControllerTest.createDateMoment(DisplayDateType.DATE_TIME, -1), null), savedExperiences);
        saveExperience(createExperienceToSave("testFive", user1, mediumBookId, locationThuisId,
                ExperienceControllerTest.createDateMoment(DisplayDateType.DATE, 1), null), savedExperiences);
        return savedExperiences;
    }

    private void saveExperience(Experience experienceToSave, Map<Date, Experience> savedExperiences) throws CultureLogException {
        Experience experience = experienceService.save(experienceToSave);
        savedExperiences.put(experience.getMoment().getSortDate(), experience);
    }

    public Experience createExperienceToSave(String name, User user, Long mediumId, Long locationId, Moment moment, String comment) {
        Experience experience = new Experience();
        experience.setName(name);
        experience.setUser(user);
        if (mediumId != null) {
            experience.setType(mediumRepository.findOne(mediumId));
        }
        if (locationId != null) {
            experience.setLocation(locationRepository.findOne(locationId));
        }
        experience.setMoment(moment);
        experience.setComment(comment);
        return experience;
    }

    private void executeAndAssertFindByUserId(Long userId, int page, int size, Sort sort, List<Long> expectedIds, long totalElements, int totalPages, boolean first, boolean last) {
        Page<Experience> experiencePage1 = experienceRepository.findByUserId(userId, new PageRequest(page, size, sort));

        // assert pagingInfo
        Assert.assertEquals(totalElements, experiencePage1.getTotalElements());
        Assert.assertEquals(totalPages, experiencePage1.getTotalPages());
        Assert.assertEquals(size, experiencePage1.getSize());
        Assert.assertEquals(sort, experiencePage1.getSort());
        Assert.assertEquals(expectedIds.size(), experiencePage1.getNumberOfElements());
        Assert.assertEquals(first, experiencePage1.isFirst());
        Assert.assertEquals(last, experiencePage1.isLast());
        Assert.assertEquals(page, experiencePage1.getNumber());
        // assert content
        for (int i = 0; i < experiencePage1.getContent().size(); i++) {
            Experience experience = experiencePage1.getContent().get(i);
            Assert.assertEquals(expectedIds.get(i), experience.getId());
        }
    }
}