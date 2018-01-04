package culturelog.backend.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import culturelog.backend.CultureLogBackendApplication;
import culturelog.backend.configuration.CultureLogTestConfiguration;
import culturelog.backend.domain.DisplayDate;
import culturelog.backend.domain.DisplayDateType;
import culturelog.backend.domain.Experience;
import culturelog.backend.domain.Location;
import culturelog.backend.domain.Medium;
import culturelog.backend.domain.Moment;
import culturelog.backend.domain.MomentType;
import culturelog.backend.domain.User;
import culturelog.backend.dto.DateMomentDto;
import culturelog.backend.dto.ExperienceDto;
import culturelog.backend.dto.LocationDto;
import culturelog.backend.dto.MediumDto;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.repository.ExperienceRepository;
import culturelog.backend.repository.LocationRepository;
import culturelog.backend.repository.MediumRepository;
import culturelog.backend.repository.UserRepository;
import culturelog.backend.service.ExperienceService;
import culturelog.backend.service.MessageService;
import culturelog.backend.utils.DisplayDateUtils;
import culturelog.backend.utils.LocationUtils;
import culturelog.backend.utils.MediumUtils;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static culturelog.backend.controller.LocationControllerTest.createLocationToSave;
import static culturelog.backend.controller.MediumControllerTest.createMediumToSave;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link ExperienceController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CultureLogBackendApplication.class)
@WebAppConfiguration
@Transactional // db changes in one test are rolled back after test
public class ExperienceControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private MessageService messageService;

    private static final String URL_EXPERIENCES = "/experiences";
    private static final String URL_EXPERIENCE = "/experiences/%d";

    // experiences OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_EXPERIENCES, HttpMethod.POST, HttpMethod.GET);
    }

    // -------------------------
    // experiences POST
    // -------------------------

    @Test
    public void testCreateExperience_notAuthorized() throws Exception {
        ExperienceDto experienceDto = new ExperienceDto();

        mockMvc.perform(post(URL_EXPERIENCES)
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateExperience_failWhenAlreadyId() throws Exception {
        ExperienceDto experienceDto = creatExperienceToSave(null, null);
        experienceDto.setId(1545L);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_nameRequired() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        ExperienceDto experienceDto = creatExperienceToSave(null, filmTypeId);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_typeRequired() throws Exception {
        ExperienceDto experienceDto = creatExperienceToSave("test", null);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_withNameAndTypeWithoutTypeId__typeIdRequired() throws Exception {
        ExperienceDto experienceDto = creatExperienceToSave("Shakespeare in love", null);
        experienceDto.setType(new MediumDto());

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_withNameAndType__typeIdUnknown_fails() throws Exception {
        ExperienceDto experienceDto = creatExperienceToSave("Shakespeare in love", null);
        Long typeUnknownId = 15556L;
        Assert.assertNull(mediumRepository.findOne(typeUnknownId));
        experienceDto.setType(new MediumDto());
        experienceDto.getType().setId(typeUnknownId);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_withNameAndType__typeOfOtherUser_fails() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long mediumId = mediumRepository.save(createMediumToSave("testTwo", user2)).getId();
        //use experienceType of user2 for experience of user1
        ExperienceDto experienceDto = creatExperienceToSave("Shakespeare in love", mediumId);
        Assert.assertNotNull(mediumRepository.findOne(mediumId));

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_withNameAndGlobalTypeWithoutMoment__createSuccesful_momentDefaultCurrentDate() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        String experienceName = "Shakespeare in love";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, filmTypeId);

        int experienceCountBefore = experienceRepository.findAll().size();

        long minCreateDateTime = new Date().getTime();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", equalTo(experienceName)))
                .andExpect(jsonPath("$.type.id", equalTo(filmTypeId.intValue())))
                .andExpect(jsonPath("$.moment", notNullValue()))
                .andExpect(jsonPath("$.moment.id", notNullValue()))
                .andExpect(jsonPath("$.moment.momentType", equalTo(MomentType.DATE.name())))
                .andExpect(jsonPath("$.moment.displayDate", notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.id", notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.type", equalTo(DisplayDateType.DATE.name())))
                .andExpect(jsonPath("$.moment.displayDate.date", greaterThan(minCreateDateTime)))
                .andExpect(jsonPath("$.moment.displayDate.date", lessThan(new Date().getTime())))
                .andExpect(jsonPath("$.location", nullValue()))
                .andExpect(jsonPath("$.comment", nullValue()))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_withNameAndCustomTypeAndMomentAndComment__createSuccesful() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Long customExperienceTypeId = mediumRepository.save(createMediumToSave("testOne", user1)).getId();
        String experienceName = "Shakespeare in love 2";
        String experienceComment = "First one better";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, customExperienceTypeId);
        experienceDto.setComment(experienceComment);
        //create datemoment before
        Date dateYesterday = createDateRelativeToToday(-1);
        DateMomentDto dateMomentDto = new DateMomentDto();
        dateMomentDto.setDisplayDate(DisplayDateUtils.toDisplayDateDto(
                DisplayDateUtils.createDisplayDate(DisplayDateType.DATE_TIME, dateYesterday)));
        experienceDto.setMoment(dateMomentDto);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", equalTo(experienceName)))
                .andExpect(jsonPath("$.type.id", equalTo(customExperienceTypeId.intValue())))
                .andExpect(jsonPath("$.moment", notNullValue()))
                .andExpect(jsonPath("$.moment.id", notNullValue()))
                .andExpect(jsonPath("$.moment.momentType", equalTo(MomentType.DATE.name())))
                .andExpect(jsonPath("$.moment.displayDate", notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.id", notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.type", equalTo(dateMomentDto.getDisplayDate().getType().name())))
                .andExpect(jsonPath("$.moment.displayDate.date", equalTo(dateYesterday.getTime())))
                .andExpect(jsonPath("$.location", nullValue()))
                .andExpect(jsonPath("$.comment", equalTo(experienceComment)))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_onSuccess_doesNotUpdateExperienceType() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        Medium existingType = mediumRepository.findOne(filmTypeId);
        Assert.assertNotNull(existingType);
        String existingTypeNameOriginal = existingType.getName();
        String existingTypeNameAlteration = existingTypeNameOriginal + "Alter";
        String experienceName = "Shakespeare in love";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, null);
        experienceDto.setType(new MediumDto());
        experienceDto.getType().setId(filmTypeId);
        experienceDto.getType().setName(existingTypeNameAlteration);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type.name", equalTo(existingTypeNameOriginal)))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);

        Medium existingTypeAfterCreation = mediumRepository.findOne(filmTypeId);
        Assert.assertEquals(existingTypeNameOriginal, existingTypeAfterCreation.getName());
    }

    // experience create: location optional checks

    @Test
    public void testCreateExperience_locationEmpty_fails() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        String experienceName = "Shakespeare in love";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, filmTypeId);
        experienceDto.setLocation(new LocationDto());

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_locationGlobal_success() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        String experienceName = "Shakespeare in love";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, filmTypeId);
        Long locationGlobalId = CultureLogTestConfiguration.getGlobalLocationIdVooruit();
        Location locationGlobal = locationRepository.findOne(locationGlobalId);
        experienceDto.setLocation(LocationUtils.toLocationDto(locationGlobal));

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.location", notNullValue()))
                .andExpect(jsonPath("$.location.id", equalTo(locationGlobalId.intValue())))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_locationOwn_success_doesNotUpdateLocation() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        String experienceName = "Shakespeare in love";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, filmTypeId);
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Long locationOwnId = locationRepository.save(createLocationToSave("testOne", user1)).getId();
        Location locationOwn = locationRepository.findOne(locationOwnId);
        experienceDto.setLocation(LocationUtils.toLocationDto(locationOwn));
        String existingLocationNameOriginal = locationOwn.getName();
        String existingLocationNameAlteration = existingLocationNameOriginal + "Alter";
        experienceDto.setLocation(new LocationDto());
        experienceDto.getLocation().setId(locationOwnId);
        experienceDto.getLocation().setName(existingLocationNameAlteration);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.location", notNullValue()))
                .andExpect(jsonPath("$.location.id", equalTo(locationOwnId.intValue())))
                .andExpect(jsonPath("$.location.name", equalTo(existingLocationNameOriginal)))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);

        Location existingLocationAfterCreation = locationRepository.findOne(locationOwnId);
        Assert.assertEquals(existingLocationNameOriginal, existingLocationAfterCreation.getName());
    }

    @Test
    public void testCreateExperience_locationOfOtherUser_fails() throws Exception {
        Long filmTypeId = CultureLogTestConfiguration.getGlobalMediumIdFilm();
        String experienceName = "Shakespeare in love";
        ExperienceDto experienceDto = creatExperienceToSave(experienceName, filmTypeId);
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long locationOwnId = locationRepository.save(createLocationToSave("testTwo", user2)).getId();
        experienceDto.setLocation(LocationUtils.toLocationDto(locationRepository.findOne(locationOwnId)));

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
        ;

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    // -----------------------------------------
    // url /experiences?page=X&size=Y GET
    // -----------------------------------------

    @Test
    public void testGetExperiences_notAuthorized() throws Exception {
        mockMvc.perform(get(URL_EXPERIENCES))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperiences_noOwnExperiences() throws Exception {
        int page = 0;
        int size = 3;
        Pageable pageable = new PageRequest(page, size);
        Page experienceListUser = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), pageable);
        Assert.assertEquals(0L, experienceListUser.getTotalElements());
        List<Long> expectedIdList = Collections.emptyList();

        executeAndAssertGetExperiencesPage(page, size, null, false, expectedIdList, experienceListUser);
    }

    @Test
    public void testGetExperiences_defaultPagingInfo() throws Exception {
        String direction0 = ExperienceController.DEFAULT_SORT_ASC ? Sort.Direction.ASC.name() : Sort.Direction.DESC.name();
        mockMvc.perform(get(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.number", equalTo(ExperienceController.DEFAULT_PAGE_NUMBER)))
                .andExpect(jsonPath("$.size", equalTo(ExperienceController.DEFAULT_PAGE_SIZE)))
                .andExpect(jsonPath("$.sort", hasSize(1)))
                .andExpect(jsonPath("$.sort[0].property", equalTo(ExperienceController.DEFAULT_SORT_COLUMN)))
                .andExpect(jsonPath("$.sort[0].direction", equalTo(direction0)))
                .andExpect(jsonPath("$.sort[0].ascending", equalTo(ExperienceController.DEFAULT_SORT_ASC)))
                ;
    }

    @Test
    public void testGetLocations_withOwnExperiences() throws Exception {
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = createExperiencesForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sort = null; //default
        Page<Experience> experiencePage0 = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, pageSize));
        Page<Experience> experiencePage1 = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(1, pageSize));

        //determine ids in pages
        List<Long> experienceIdsSortedBySortDateDesc = savedExperiences.values().stream().map(experience -> experience.getId()).collect(Collectors.toList());
        Collections.reverse(experienceIdsSortedBySortDateDesc);
        List<Long> expectedIdListPage0 = experienceIdsSortedBySortDateDesc.subList(0, 3);
        List<Long> expectedIdListPage1 = experienceIdsSortedBySortDateDesc.subList(3, 5);

        executeAndAssertGetExperiencesPage(0, pageSize, sort, false, expectedIdListPage0, experiencePage0);
        executeAndAssertGetExperiencesPage(1, pageSize, sort, false, expectedIdListPage1, experiencePage1);
    }

    @Test
    public void testGetLocations_withOwnExperiences_sortByExperienceIdAsc() throws Exception {
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = createExperiencesForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sort = "id";
        Page<Experience> experiencePage0 = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, pageSize));
        Page<Experience> experiencePage1 = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(1, pageSize));

        //determine ids in pages
        List<Long> experienceIdsSortedByExperienceAsc = savedExperiences.values().stream().map(experience -> experience.getId()).collect(Collectors.toList());
        Collections.sort(experienceIdsSortedByExperienceAsc);
        List<Long> expectedIdListPage0 = experienceIdsSortedByExperienceAsc.subList(0, 3);
        List<Long> expectedIdListPage1 = experienceIdsSortedByExperienceAsc.subList(3, 5);

        executeAndAssertGetExperiencesPage(0, pageSize, sort, false, expectedIdListPage0, experiencePage0);
        executeAndAssertGetExperiencesPage(1, pageSize, sort, false, expectedIdListPage1, experiencePage1);
    }

    @Test
    public void testGetLocations_withOwnExperiences_sortByExperienceIdDesc() throws Exception {
        //key: moment sortDate, value: savedExperience
        TreeMap<Date, Experience> savedExperiences = createExperiencesForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sort = "id";
        Page<Experience> experiencePage0 = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, pageSize));
        Page<Experience> experiencePage1 = experienceRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(1, pageSize));

        //determine ids in pages
        List<Long> experienceIdsSortedByExperienceAsc = savedExperiences.values().stream().map(experience -> experience.getId()).collect(Collectors.toList());
        Collections.sort(experienceIdsSortedByExperienceAsc);
        Collections.reverse(experienceIdsSortedByExperienceAsc);
        List<Long> expectedIdListPage0 = experienceIdsSortedByExperienceAsc.subList(0, 3);
        List<Long> expectedIdListPage1 = experienceIdsSortedByExperienceAsc.subList(3, 5);

        executeAndAssertGetExperiencesPage(0, pageSize, sort, true, expectedIdListPage0, experiencePage0);
        executeAndAssertGetExperiencesPage(1, pageSize, sort, true, expectedIdListPage1, experiencePage1);
    }

    // helper methods

    private ExperienceDto creatExperienceToSave(String name, Long mediumId) {
        ExperienceDto experience = new ExperienceDto();
        experience.setName(name);
        if (mediumId != null) {
            Medium medium = mediumRepository.findOne(mediumId);
            experience.setType(MediumUtils.toMediumDto(medium));
        }
        return experience;
    }

    private static String getUrlExperiencesPaged(Integer page, Integer size, String sort, boolean desc) {
        if (page == null && size == null && sort == null) {
            return URL_EXPERIENCES;
        }
        StringBuilder stringBuilder = new StringBuilder(URL_EXPERIENCES).append('?');
        if (page != null) {
            stringBuilder.append("page=").append(page).append('&');
        }
        if (size != null) {
            stringBuilder.append("size=").append(size).append('&');
        }
        if (sort != null) {
            stringBuilder.append("sort=").append(sort);
            if (desc) {
                stringBuilder.append(",desc");
            }
            stringBuilder.append('&');
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private void assertExperience(Experience experience, JSONArray jsonPathResult) {
        Assert.assertNotNull(experience);
        Assert.assertNotNull(jsonPathResult);
        Assert.assertEquals(1, jsonPathResult.size());
        Map<String, Object> locationJson = (Map<String, Object>) jsonPathResult.get(0);
        assertExperienceJson(experience, locationJson);
    }

    private void assertExperienceJson(Experience experience, Map<String, Object> experienceJson) {
        Assert.assertNotNull(experience);
        Assert.assertNotNull(experienceJson);
        Assert.assertEquals(experience.getId().longValue(), ((Number) experienceJson.get("id")).longValue());
        Assert.assertEquals(experience.getName(), experienceJson.get("name"));
        Assert.assertEquals(experience.getType().getId().longValue(), ((Number) ((Map<String, Object>) experienceJson.get("type")).get("id")).longValue());
        Assert.assertEquals(experience.getMoment().getId().longValue(), ((Number) ((Map<String, Object>) experienceJson.get("moment")).get("id")).longValue());
        if (experience.getLocation() == null) {
            Assert.assertNull(experienceJson.get("location"));
        } else {
            Assert.assertEquals(experience.getLocation().getId().longValue(), ((Number) ((Map<String, Object>) experienceJson.get("location")).get("id")).longValue());
        }
        Assert.assertEquals(experience.getComment(), experienceJson.get("comment"));
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

    public static Moment createDateMoment(DisplayDateType displayDateType, int dateRelativeToToday) {
        Moment moment = new Moment();
        moment.setType(MomentType.DATE);
        DisplayDate displayDate = new DisplayDate();
        displayDate.setType(displayDateType);
        displayDate.setDate(createDateRelativeToToday(dateRelativeToToday));
        moment.setDisplayDates(Collections.singletonList(displayDate));
        return moment;
    }

    public static Date createDateRelativeToToday(int dateRelativeToToday) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, dateRelativeToToday);
        return cal.getTime();
    }

    private void executeAndAssertGetExperiencesPage(int page, int pageSize, String sort, boolean desc, List<Long> expectedIdList, Page pageInfoExpected) throws Exception {
        MvcResult resultPage = mockMvc.perform(get(getUrlExperiencesPaged(page, pageSize, sort, desc))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content", hasSize(expectedIdList.size())))
                //paging info
                .andExpect(jsonPath("$.totalElements", equalTo((int) pageInfoExpected.getTotalElements())))
                .andExpect(jsonPath("$.totalPages", equalTo(pageInfoExpected.getTotalPages())))
                .andExpect(jsonPath("$.first", equalTo(pageInfoExpected.isFirst())))
                .andExpect(jsonPath("$.last", equalTo(pageInfoExpected.isLast())))
                .andExpect(jsonPath("$.number", equalTo(pageInfoExpected.getNumber())))
                .andExpect(jsonPath("$.numberOfElements", equalTo(pageInfoExpected.getNumberOfElements())))
                .andExpect(jsonPath("$.size", equalTo(pageInfoExpected.getSize())))
                .andReturn();

        ReadContext ctx = JsonPath.parse(resultPage.getResponse().getContentAsString());
        // test all experience id's are present
        assertIdList(expectedIdList, ctx.read("$.content[*].id"));
        for (Long expectedId : expectedIdList) {
            assertExperience(experienceRepository.findOne(expectedId), ctx.read(String.format("$.content.[?(@.id==%d)]", expectedId)));
        }
    }

    private void saveExperience(Experience experienceToSave, Map<Date, Experience> savedExperiences) throws CultureLogException {
        Experience experience = experienceService.save(experienceToSave);
        savedExperiences.put(experience.getMoment().getSortDate(), experience);
    }

    /**
     *
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
                createDateMoment(DisplayDateType.DATE, 0), "ok"), savedExperiences);
        saveExperience(createExperienceToSave("testTwo", user1, mediumTheaterId, null,
                createDateMoment(DisplayDateType.DATE_TIME, 2), "like this"), savedExperiences);
        saveExperience(createExperienceToSave("testThree", user1, mediumBookId, locationThuisId,
                createDateMoment(DisplayDateType.DATE, -2), "nice one"), savedExperiences);
        saveExperience(createExperienceToSave("testFour", user1, mediumTheaterId, null,
                createDateMoment(DisplayDateType.DATE_TIME, -1), null), savedExperiences);
        saveExperience(createExperienceToSave("testFive", user1, mediumBookId, locationThuisId,
                createDateMoment(DisplayDateType.DATE, 1), null), savedExperiences);
        return savedExperiences;
    }
}