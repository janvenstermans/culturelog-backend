package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.*;
import culturelog.rest.dto.DateMomentDto;
import culturelog.rest.dto.ExperienceDto;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.dto.MediumDto;
import culturelog.rest.repository.ExperienceRepository;
import culturelog.rest.repository.LocationRepository;
import culturelog.rest.repository.MediumRepository;
import culturelog.rest.repository.UserRepository;
import culturelog.rest.service.MessageService;
import culturelog.rest.utils.DisplayDateUtils;
import culturelog.rest.utils.LocationUtils;
import culturelog.rest.utils.MediumUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static culturelog.rest.controller.MediumControllerTest.createMediumToSave;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link ExperienceController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
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
    private MessageService messageService;

    private static final String URL_EXPERIENCES = "/experiences";
    private static final String URL_EXPERIENCE = "/experiences/%d";

    // experiences OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_EXPERIENCES, HttpMethod.POST);
    }

    // experiences POST

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
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(experienceName)))
                .andExpect(jsonPath("$.type.id", Matchers.equalTo(filmTypeId.intValue())))
                .andExpect(jsonPath("$.moment", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.momentType", Matchers.equalTo(MomentType.DATE.name())))
                .andExpect(jsonPath("$.moment.displayDate", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.type", Matchers.equalTo(DisplayDateType.DATE.name())))
                .andExpect(jsonPath("$.moment.displayDate.date", Matchers.greaterThan(minCreateDateTime)))
                .andExpect(jsonPath("$.moment.displayDate.date", Matchers.lessThan(new Date().getTime())))
                .andExpect(jsonPath("$.location", Matchers.nullValue()))
                .andExpect(jsonPath("$.comment", Matchers.nullValue()))
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        Date dateYesterday = cal.getTime();
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
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(experienceName)))
                .andExpect(jsonPath("$.type.id", Matchers.equalTo(customExperienceTypeId.intValue())))
                .andExpect(jsonPath("$.moment", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.momentType", Matchers.equalTo(MomentType.DATE.name())))
                .andExpect(jsonPath("$.moment.displayDate", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.moment.displayDate.type", Matchers.equalTo(dateMomentDto.getDisplayDate().getType().name())))
                .andExpect(jsonPath("$.moment.displayDate.date", Matchers.equalTo(dateYesterday.getTime())))
                .andExpect(jsonPath("$.location", Matchers.nullValue()))
                .andExpect(jsonPath("$.comment", Matchers.equalTo(experienceComment)))
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
                .andExpect(jsonPath("$.type.name", Matchers.equalTo(existingTypeNameOriginal)))
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
                .andExpect(jsonPath("$.location", Matchers.notNullValue()))
                .andExpect(jsonPath("$.location.id", Matchers.equalTo(locationGlobalId.intValue())))
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
        Long locationOwnId = locationRepository.save(LocationControllerTest.createLocationToSave("testOne", user1)).getId();
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
                .andExpect(jsonPath("$.location", Matchers.notNullValue()))
                .andExpect(jsonPath("$.location.id", Matchers.equalTo(locationOwnId.intValue())))
                .andExpect(jsonPath("$.location.name", Matchers.equalTo(existingLocationNameOriginal)))
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
        Long locationOwnId = locationRepository.save(LocationControllerTest.createLocationToSave("testTwo", user2)).getId();
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
}