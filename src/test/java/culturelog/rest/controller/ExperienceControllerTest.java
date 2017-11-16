package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.DisplayDateType;
import culturelog.rest.domain.Experience;
import culturelog.rest.domain.Medium;
import culturelog.rest.domain.User;
import culturelog.rest.dto.DateMomentDto;
import culturelog.rest.dto.ExperienceDto;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.repository.ExperienceRepository;
import culturelog.rest.repository.MediumRepository;
import culturelog.rest.repository.UserRepository;
import culturelog.rest.service.MessageService;
import culturelog.rest.utils.DisplayDateUtils;
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

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
    public void testCreateExperience_nameRequired() throws Exception {
        Medium film = mediumRepository.findOne(CultureLogTestConfiguration.getGlobalMediumIdFilm());
        ExperienceDto experienceDto = creatExperienceToSave(null, film);

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
    public void testCreateExperience_withNameAndTypeWithoutMoment__createSuccesful_momentDefaultCurrentDate() throws Exception {
        Medium film = mediumRepository.findOne(CultureLogTestConfiguration.getGlobalMediumIdFilm());
        ExperienceDto experienceDto = creatExperienceToSave("Shakespeare in love", film);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type.id", Matchers.equalTo(film.getId())));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);
    }

    @Test
    public void testCreateExperience_withNameAndTypeAndMoment__createSuccesful() throws Exception {
        Medium film = mediumRepository.findOne(CultureLogTestConfiguration.getGlobalMediumIdFilm());
        ExperienceDto experienceDto = creatExperienceToSave("Shakespeare in love", film);
        DateMomentDto dateMomentDto = new DateMomentDto();
        dateMomentDto.setDisplayDate(DisplayDateUtils.toDisplayDateDto(DisplayDateUtils.createDisplayDate(DisplayDateType.DATE_TIME, new Date(0))));
        experienceDto.setMoment(dateMomentDto);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type.id", Matchers.equalTo(film.getId())));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore + 1, experienceCountAfter);
    }

    // helper methods

    private ExperienceDto creatExperienceToSave(String name, Medium medium) {
        ExperienceDto experience = new ExperienceDto();
        experience.setName(name);
        experience.setType(MediumUtils.toMediumDto(medium));
        return experience;
    }
}